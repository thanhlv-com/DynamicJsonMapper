package com.thanhlv.dynamicjsonmapper;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PaginatedListMapperTest {
    private static final String TEMPLATE_PATH = "templates/paginated-users-template.json";
    private static final JsonTemplateMapper MAPPER = JsonTemplateMapper.defaultInstance();

    @Test
    void shouldRenderPaginatedResponseWithExpectedItems() {
        List<Map<String, Object>> users = List.of(
                Map.of("id", 1, "name", "An"),
                Map.of("id", 2, "name", "Binh"),
                Map.of("id", 3, "name", "Chi", "name1", "Chi2"),
                Map.of("id", 4, "name", "Dung"),
                Map.of("id", 5, "name", "Giang")
        );

        int page = 2;
        int size = 2;

        PaginationResult paginationResult = paginate(users, page, size);

        Map<String, Object> rawData = new HashMap<>();
        rawData.put("${page}", paginationResult.page());
        rawData.put("${size}", paginationResult.size());
        rawData.put("${total}", paginationResult.total());
        rawData.put("${total_pages}", paginationResult.totalPages());
        rawData.put("${items}", paginationResult.items());
        rawData.put("${first_item}", firstItem(paginationResult.items()));
        rawData.put("${last_item}", lastItem(paginationResult.items()));

        JsonNode finalResponse = MAPPER.render(TEMPLATE_PATH, rawData);

        assertEquals("SUCCESS", finalResponse.path("status").asText());
        assertEquals(2, finalResponse.path("data").path("page").asInt());
        assertEquals(2, finalResponse.path("data").path("size").asInt());
        assertEquals(5, finalResponse.path("data").path("total").asInt());
        assertEquals(3, finalResponse.path("data").path("total_pages").asInt());

        JsonNode items = finalResponse.path("data").path("items");
        assertEquals(2, items.size());
        assertEquals(3, items.get(0).path("user_id").asInt());
        assertEquals("Chi", items.get(0).path("display_name").asText());
        assertEquals(4, items.get(1).path("user_id").asInt());
        assertEquals("Dung", items.get(1).path("display_name").asText());

        assertEquals(3, finalResponse.path("data").path("first_item").path("user_id").asInt());
        assertEquals("Chi", finalResponse.path("data").path("first_item").path("display_name").asText());
        assertEquals(4, finalResponse.path("data").path("last_item").path("user_id").asInt());
        assertEquals("Dung", finalResponse.path("data").path("last_item").path("display_name").asText());
    }

    @Test
    void shouldNormalizeInvalidPaginationInput() {
        PaginationResult result = paginate(List.of(1, 2, 3), 0, 0);

        assertEquals(1, result.page());
        assertEquals(1, result.size());
        assertEquals(3, result.total());
        assertEquals(3, result.totalPages());
        assertEquals(List.of(1), result.items());
    }

    @Test
    void shouldSupportCustomTemplateRepository() {
        ObjectMapper objectMapper = new ObjectMapper();
        TemplateRepository repository = new TemplateRepository() {
            @Override
            public JsonNode getTemplate(String templatePath) {
                try {
                    return objectMapper.readTree("{\"message\": \"${message}\"}");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        JsonTemplateMapper customMapper = new JsonTemplateMapper(objectMapper, repository);

        JsonNode result = customMapper.render("ignored-by-custom-repository", Map.of("${message}", "hello"));

        assertNotNull(result);
        assertEquals("hello", result.path("message").asText());
    }

    static <T> PaginationResult paginate(List<T> source, int page, int size) {
        int normalizedSize = Math.max(1, size);
        int normalizedPage = Math.max(1, page);
        int total = source.size();
        int totalPages = (int) Math.ceil((double) total / normalizedSize);

        int fromIndex = Math.min((normalizedPage - 1) * normalizedSize, total);
        int toIndex = Math.min(fromIndex + normalizedSize, total);
        List<T> items = new ArrayList<>(source.subList(fromIndex, toIndex));

        return new PaginationResult(normalizedPage, normalizedSize, total, Math.max(1, totalPages), items);
    }

    private static Object firstItem(List<?> items) {
        if (items.isEmpty()) {
            return null;
        }
        return items.get(0);
    }

    private static Object lastItem(List<?> items) {
        if (items.isEmpty()) {
            return null;
        }
        return items.get(items.size() - 1);
    }

    record PaginationResult(int page, int size, int total, int totalPages, List<?> items) {
    }
}
