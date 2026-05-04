package com.thanhlv.dynamicjsonmapper;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class TemplateRepository {
    private final ObjectMapper mapper;
    private final Map<String, JsonNode> cache;

    TemplateRepository(ObjectMapper mapper) {
        this.mapper = mapper;
        this.cache = new ConcurrentHashMap<>();
    }

    JsonNode getTemplate(String templatePath) {
        return cache.computeIfAbsent(templatePath, path -> {
            try (InputStream is = JsonTemplateMapper.class.getClassLoader().getResourceAsStream(path)) {
                if (is == null) {
                    throw new IllegalArgumentException("Template does not exist: " + path);
                }
                return mapper.readTree(is);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load template file", e);
            }
        });
    }

    void clear() {
        cache.clear();
    }
}
