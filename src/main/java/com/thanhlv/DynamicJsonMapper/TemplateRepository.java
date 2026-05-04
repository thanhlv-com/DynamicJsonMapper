package com.thanhlv.dynamicjsonmapper;

import tools.jackson.databind.JsonNode;

/**
 * Abstraction for template loading/caching strategy.
 */
public interface TemplateRepository {
    /**
     * Returns a parsed template node for a path.
     * Implementations may cache and reuse immutable nodes.
     */
    JsonNode getTemplate(String templatePath);

    /**
     * Clears internal cache/state when supported.
     */
    default void clear() {
        // Optional for repositories that do not cache templates.
    }
}
