package com.thanhlv.dynamicjsonmapper;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicJsonMapperTest {
    private static final String TEMPLATE_PATH = "templates/user-template.json";
    private static final JsonTemplateMapper MAPPER = JsonTemplateMapper.defaultInstance();

    @Test
    void shouldRenderUserTemplateWithExpectedValues() {
        final Map<String, Object> rawData = new HashMap<>();
        rawData.put("${name}", "Thanh");
        rawData.put("${noio}", "Hà Nội");
        rawData.put("${tuoi}", 23);

        final JsonNode finalJsonResponse = MAPPER.render(TEMPLATE_PATH, rawData);

        assertEquals("SUCCESS", finalJsonResponse.path("status").asText());
        assertEquals("Thanh", finalJsonResponse.path("data").path("account_info").path("user_name").asText());
        assertEquals(23, finalJsonResponse.path("data").path("account_info").path("age").asInt());
        assertEquals("Hà Nội", finalJsonResponse.path("data").path("location_details").path("city").asText());
        assertEquals("Việt Nam", finalJsonResponse.path("data").path("location_details").path("country").asText());
        assertEquals("SV-01", finalJsonResponse.path("metadata").path("server_id").asText());
    }
}
