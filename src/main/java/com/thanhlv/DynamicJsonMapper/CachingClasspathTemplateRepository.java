package com.thanhlv.dynamicjsonmapper;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default classpath-backed template repository with in-memory cache.
 */
public class CachingClasspathTemplateRepository implements TemplateRepository {
    private final ObjectMapper mapper;
    private final Map<String, JsonNode> cache;

    public CachingClasspathTemplateRepository(ObjectMapper mapper) {
        this.mapper = mapper;
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public JsonNode getTemplate(String templatePath) {
        return cache.computeIfAbsent(templatePath, path -> {
            try (InputStream is = openTemplateStream(path)) {
                if (is == null) {
                    throw new IllegalArgumentException("Template does not exist: " + path);
                }
                return mapper.readTree(is);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load template file", e);
            }
        });
    }

    @Override
    public void clear() {
        cache.clear();
    }

    /**
     * Extension point to customize where templates are loaded from.
     */
    protected InputStream openTemplateStream(String templatePath) {
        return JsonTemplateMapper.class.getClassLoader().getResourceAsStream(templatePath);
    }
}
