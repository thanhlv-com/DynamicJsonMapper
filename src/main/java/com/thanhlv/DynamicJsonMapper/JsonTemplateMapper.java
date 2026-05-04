package com.thanhlv.dynamicjsonmapper;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Facade for rendering dynamic JSON responses from templates.
 */
public class JsonTemplateMapper {
    private static final JsonTemplateMapper DEFAULT_INSTANCE = new JsonTemplateMapper();

    private final ObjectMapper mapper;
    private final TemplateRepository templateRepository;
    private final TemplateRenderer templateRenderer;

    public JsonTemplateMapper() {
        this(new ObjectMapper());
    }

    public JsonTemplateMapper(ObjectMapper mapper) {
        this(mapper, new CachingClasspathTemplateRepository(mapper));
    }

    public JsonTemplateMapper(TemplateRepository templateRepository) {
        this(new ObjectMapper(), templateRepository);
    }

    public JsonTemplateMapper(ObjectMapper mapper, TemplateRepository templateRepository) {
        this.mapper = mapper;
        this.templateRepository = templateRepository;
        this.templateRenderer = new TemplateRenderer(mapper, templateRepository);
    }

    public static JsonTemplateMapper defaultInstance() {
        return DEFAULT_INSTANCE;
    }

    /**
     * Renders a JSON template with dynamic data.
     *
     * @param templatePath classpath path under resources
     * @param rawData placeholder map, for example "${name}" -> "Thanh"
     * @return rendered JsonNode
     */
    public JsonNode render(String templatePath, Map<String, Object> rawData) {
        try {
            return templateRenderer.render(templatePath, rawData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to render dynamic JSON", e);
        }
    }

    public String jsonNodeToString(JsonNode node) {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
    }

    /**
     * Clears template cache.
     */
    public void refreshCache() {
        templateRepository.clear();
    }
}
