package com.thanhlv.dynamicjsonmapper;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.Map;

/**
 * Recursively renders a template tree by replacing placeholders and resolving template references.
 * This class is package-private and coordinated through {@link JsonTemplateMapper}.
 */
final class TemplateRenderer {
    private final ObjectMapper mapper;
    private final TemplateRepository templateRepository;
    private final TemplateReferenceResolver templateReferenceResolver;

    TemplateRenderer(ObjectMapper mapper, TemplateRepository templateRepository) {
        this.mapper = mapper;
        this.templateRepository = templateRepository;
        this.templateReferenceResolver = new TemplateReferenceResolver(mapper, templateRepository);
    }

    JsonNode render(String templatePath, Map<String, Object> rawData) {
        JsonNode rootTemplate = templateRepository.getTemplate(templatePath);
        // Render from a deep copy so cached templates in the repository remain immutable.
        return processNode(rootTemplate.deepCopy(), rawData);
    }

    private JsonNode processNode(JsonNode node, Map<String, Object> rawData) {
        if (node instanceof ObjectNode objectNode) {
            JsonNode templateRefResult = templateReferenceResolver.resolve(objectNode, rawData, this);
            if (templateRefResult != null) {
                return templateRefResult;
            }

            var fields = objectNode.properties().iterator();
            while (fields.hasNext()) {
                var entry = fields.next();
                objectNode.set(entry.getKey(), processNode(entry.getValue(), rawData));
            }
            return objectNode;
        }

        if (node instanceof ArrayNode arrayNode) {
            for (int i = 0; i < arrayNode.size(); i++) {
                arrayNode.set(i, processNode(arrayNode.get(i), rawData));
            }
            return arrayNode;
        }

        if (node.isTextual()) {
            String text = node.asText();
            if (PlaceholderHelper.isPlaceholder(text)) {
                // Only full-token placeholders are replaced; partial string interpolation is unsupported.
                return mapper.valueToTree(rawData.get(text));
            }
        }

        return node;
    }
}
