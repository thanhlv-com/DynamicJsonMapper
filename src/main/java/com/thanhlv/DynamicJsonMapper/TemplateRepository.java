package com.thanhlv.dynamicjsonmapper;

import tools.jackson.databind.JsonNode;

/**
 * Abstraction for template loading/caching strategy.
 */
public interface TemplateRepository {
    JsonNode getTemplate(String templatePath);

    default void clear() {
        // Optional for repositories that do not cache templates.
    }
}
