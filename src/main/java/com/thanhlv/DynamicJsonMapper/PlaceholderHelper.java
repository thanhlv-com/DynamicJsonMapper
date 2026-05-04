package com.thanhlv.dynamicjsonmapper;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.HashSet;
import java.util.Set;

final class PlaceholderHelper {
    private static final String PLACEHOLDER_PREFIX = "${";
    private static final String PLACEHOLDER_SUFFIX = "}";

    private PlaceholderHelper() {
    }

    static boolean isPlaceholder(String text) {
        return text.startsWith(PLACEHOLDER_PREFIX) && text.endsWith(PLACEHOLDER_SUFFIX);
    }

    static String toPlaceholder(String key) {
        return PLACEHOLDER_PREFIX + key + PLACEHOLDER_SUFFIX;
    }

    static Set<String> collectPlaceholders(JsonNode node) {
        Set<String> placeholders = new HashSet<>();
        collect(node, placeholders);
        return placeholders;
    }

    private static void collect(JsonNode node, Set<String> placeholders) {
        if (node == null || node.isNull()) {
            return;
        }

        if (node.isTextual()) {
            String value = node.asText();
            if (isPlaceholder(value)) {
                placeholders.add(value);
            }
            return;
        }

        if (node instanceof ObjectNode objectNode) {
            var fields = objectNode.properties().iterator();
            while (fields.hasNext()) {
                collect(fields.next().getValue(), placeholders);
            }
            return;
        }

        if (node instanceof ArrayNode arrayNode) {
            for (JsonNode item : arrayNode) {
                collect(item, placeholders);
            }
        }
    }
}
