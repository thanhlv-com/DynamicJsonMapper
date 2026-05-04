package com.thanhlv.DynamicJsonMapper;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonTemplateMapperTest {

    @Test
    void render_shouldReplacePlaceholdersInUserTemplate() {
        Map<String, Object> rawData = Map.of(
                "${name}", "Thanh",
                "${noio}", "Ha Noi",
                "${tuoi}", 23
        );

        JsonNode result = JsonTemplateMapper.render("templates/user-template.json", rawData);

        assertEquals("SUCCESS", result.path("status").asText());
        assertEquals("Thanh", result.path("data").path("account_info").path("user_name").asText());
        assertEquals(23, result.path("data").path("account_info").path("age").asInt());
        assertEquals("Ha Noi", result.path("data").path("location_details").path("city").asText());
        assertEquals("Việt Nam", result.path("data").path("location_details").path("country").asText());
    }

    @Test
    void render_shouldMapDeepNestedTemplate() {
        Map<String, Object> rawData = Map.of(
                "${id}", "USR-99",
                "${name}", "Thanh",
                "${city}", "Ha Noi",
                "${dist}", "Cau Giay",
                "${street}", "Duy Tan",
                "${skill_primary}", "Java",
                "${skill_level}", "Senior"
        );

        JsonNode result = JsonTemplateMapper.render("templates/deep-nested-template.json", rawData);

        assertEquals("USR-99", result.path("user_id").asText());
        assertEquals("Thanh", result.path("profile").path("display_name").asText());
        assertEquals("Ha Noi", result.path("profile").path("address").path("city").asText());
        assertEquals("Cau Giay", result.path("profile").path("address").path("district").asText());
        assertEquals("Duy Tan", result.path("profile").path("address").path("detail").asText());
        assertEquals("Java", result.path("expertise").path("main").asText());
        assertEquals("Senior", result.path("expertise").path("rank").asText());
    }

    @Test
    void render_shouldSetNullForMissingPlaceholderValue() {
        JsonNode result = JsonTemplateMapper.render("templates/user-template.json", Map.of("${name}", "Thanh"));

        assertEquals("Thanh", result.path("data").path("account_info").path("user_name").asText());
        assertTrue(result.path("data").path("account_info").path("age").isNull());
        assertTrue(result.path("data").path("location_details").path("city").isNull());
    }

    @Test
    void render_shouldThrowWhenTemplateNotFound() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> JsonTemplateMapper.render("templates/missing-template.json", Map.of()));

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Lỗi render dynamic JSON"));
    }

    @Test
    void refreshCache_shouldKeepRenderWorking() {
        JsonNode first = JsonTemplateMapper.render("templates/user-template.json", Map.of("${name}", "A"));
        JsonTemplateMapper.refreshCache();
        JsonNode second = JsonTemplateMapper.render("templates/user-template.json", Map.of("${name}", "B"));

        assertEquals("A", first.path("data").path("account_info").path("user_name").asText());
        assertEquals("B", second.path("data").path("account_info").path("user_name").asText());
    }
}
