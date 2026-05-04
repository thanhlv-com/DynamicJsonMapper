package com.thanhlv.dynamicjsonmapper;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Public facade for rendering JSON templates with placeholder-driven data.
 * <p>
 * Placeholder keys are matched by their full token form (for example {@code ${name}}),
 * not by bare field names.
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
     * @param rawData placeholder map, for example {@code "${name}" -> "Thanh"}
     * @return rendered JsonNode
     * @throws RuntimeException wraps template loading/rendering failures
     */
    public JsonNode render(String templatePath, Map<String, Object> rawData) {
        try {
            return templateRenderer.render(templatePath, rawData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to render dynamic JSON", e);
        }
    }

    /**
     * Serializes a rendered node using pretty-print formatting for diagnostics/examples.
     */
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
