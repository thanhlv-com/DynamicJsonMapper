package com.thanhlv.dynamicjsonmapper;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Resolves object nodes that declare a child template expansion contract:
 * <ul>
 *     <li>{@code $template_ref}: child template path</li>
 *     <li>{@code $source}: placeholder key resolving to object/array data</li>
 * </ul>
 */
final class TemplateReferenceResolver {
    private static final String TEMPLATE_REF_KEY = "$template_ref";
    private static final String TEMPLATE_SOURCE_KEY = "$source";

    private final ObjectMapper mapper;
    private final TemplateRepository templateRepository;

    TemplateReferenceResolver(ObjectMapper mapper, TemplateRepository templateRepository) {
        this.mapper = mapper;
        this.templateRepository = templateRepository;
    }

    JsonNode resolve(ObjectNode node, Map<String, Object> rawData, TemplateRenderer renderer) {
        JsonNode templateRefNode = node.get(TEMPLATE_REF_KEY);
        if (templateRefNode == null) {
            return null;
        }

        JsonNode sourceNode = node.get(TEMPLATE_SOURCE_KEY);
        if (sourceNode == null || !sourceNode.isTextual()) {
            throw new IllegalArgumentException(
                    "Template ref requires textual field '" + TEMPLATE_SOURCE_KEY + "'");
        }
        if (!templateRefNode.isTextual()) {
            throw new IllegalArgumentException(
                    "Template ref requires textual field '" + TEMPLATE_REF_KEY + "'");
        }

        String sourceToken = sourceNode.asText();
        if (!PlaceholderHelper.isPlaceholder(sourceToken)) {
            throw new IllegalArgumentException(
                    "Template ref '" + TEMPLATE_SOURCE_KEY + "' must be a placeholder like ${key}");
        }

        Object sourceValue = rawData.get(sourceToken);
        if (sourceValue == null) {
            // Missing source token renders as JSON null instead of throwing.
            return NullNode.getInstance();
        }

        String childTemplatePath = templateRefNode.asText();
        JsonNode sourceNodeValue = mapper.valueToTree(sourceValue);
        if (sourceNodeValue.isArray()) {
            ArrayNode renderedItems = mapper.createArrayNode();
            for (JsonNode itemNode : sourceNodeValue) {
                renderedItems.add(renderChildTemplate(childTemplatePath, rawData, itemNode, renderer));
            }
            return renderedItems;
        }

        if (sourceNodeValue.isObject()) {
            return renderChildTemplate(childTemplatePath, rawData, sourceNodeValue, renderer);
        }

        throw new IllegalArgumentException(
                "Template ref '" + TEMPLATE_SOURCE_KEY + "' must resolve to object or array");
    }

    private JsonNode renderChildTemplate(
            String childTemplatePath,
            Map<String, Object> parentRawData,
            JsonNode itemNode,
            TemplateRenderer renderer
    ) {
        // Child rendering inherits parent placeholders unless item-level values override them.
        Map<String, Object> childRawData = new HashMap<>(parentRawData);
        if (itemNode instanceof ObjectNode itemObject) {
            var fields = itemObject.properties().iterator();
            while (fields.hasNext()) {
                var entry = fields.next();
                childRawData.put(
                        PlaceholderHelper.toPlaceholder(entry.getKey()),
                        mapper.convertValue(entry.getValue(), Object.class)
                );
            }
        } else {
            Object primitiveValue = mapper.convertValue(itemNode, Object.class);
            JsonNode childTemplate = templateRepository.getTemplate(childTemplatePath);
            Set<String> placeholders = PlaceholderHelper.collectPlaceholders(childTemplate);
            // Primitive arrays are "broadcast" to each placeholder in child template if not preset.
            for (String placeholder : placeholders) {
                childRawData.putIfAbsent(placeholder, primitiveValue);
            }
        }
        return renderer.render(childTemplatePath, childRawData);
    }
}
