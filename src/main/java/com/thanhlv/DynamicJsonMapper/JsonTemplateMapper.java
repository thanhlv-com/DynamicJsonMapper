package com.thanhlv.dynamicjsonmapper;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.util.Map;

/**
 * Facade for rendering dynamic JSON responses from classpath templates.
 */
public final class JsonTemplateMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TemplateRepository TEMPLATE_REPOSITORY = new TemplateRepository(MAPPER);
    private static final TemplateRenderer TEMPLATE_RENDERER = new TemplateRenderer(MAPPER, TEMPLATE_REPOSITORY);

    private JsonTemplateMapper() {
    }

    /**
     * Renders a JSON template with dynamic data.
     *
     * @param templatePath classpath path under resources
     * @param rawData placeholder map, for example "${name}" -> "Thanh"
     * @return rendered JsonNode
     */
    public static JsonNode render(String templatePath, Map<String, Object> rawData) {
        try {
            return TEMPLATE_RENDERER.render(templatePath, rawData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to render dynamic JSON", e);
        }
    }

    public static String jsonNodeToString(JsonNode node) {
        return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(node);
    }

    /**
     * Clears template cache.
     */
    public static void refreshCache() {
        TEMPLATE_REPOSITORY.clear();
    }
}
