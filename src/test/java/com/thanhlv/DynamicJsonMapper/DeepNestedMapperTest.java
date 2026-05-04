package com.thanhlv.dynamicjsonmapper;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeepNestedMapperTest {
    private static final String TEMPLATE_PATH = "templates/deep-nested-template.json";
    private static final JsonTemplateMapper MAPPER = JsonTemplateMapper.defaultInstance();

    @Test
    void shouldRenderDeepNestedTemplateWithExpectedValues() {
        Map<String, Object> rawData = Map.of(
                "${id}", "USR-99",
                "${name}", "Thanh",
                "${city}", "Hà Nội",
                "${dist}", "Cầu Giấy",
                "${street}", "Duy Tân",
                "${skill_primary}", "Java",
                "${skill_level}", "Senior"
        );

        JsonNode finalResponse = MAPPER.render(TEMPLATE_PATH, rawData);

        assertEquals("USR-99", finalResponse.path("user_id").asText());
        assertEquals("Thanh", finalResponse.path("profile").path("display_name").asText());
        assertEquals("Hà Nội", finalResponse.path("profile").path("address").path("city").asText());
        assertEquals("Cầu Giấy", finalResponse.path("profile").path("address").path("district").asText());
        assertEquals("Duy Tân", finalResponse.path("profile").path("address").path("detail").asText());
        assertEquals("Java", finalResponse.path("expertise").path("main").asText());
        assertEquals("Senior", finalResponse.path("expertise").path("rank").asText());
    }
}
