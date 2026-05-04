package com.thanhlv.DynamicJsonMapper;

import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaginatedListMapper {
    private static final String TEMPLATE_PATH = "templates/paginated-users-template.json";

    public static void main(String[] args) {
        List<Map<String, Object>> users = List.of(
                Map.of("id", 1, "name", "An"),
                Map.of("id", 2, "name", "Binh"),
                Map.of("id", 3, "name", "Chi"),
                Map.of("id", 4, "name", "Dung"),
                Map.of("id", 5, "name", "Giang")
        );

        int page = 2;
        int size = 2;

        PaginationResult paginationResult = paginate(users, page, size);

        Map<String, Object> rawData = Map.of(
                "${page}", paginationResult.page(),
                "${size}", paginationResult.size(),
                "${total}", paginationResult.total(),
                "${total_pages}", paginationResult.totalPages(),
                "${items}", paginationResult.items()
        );

        JsonNode finalResponse = JsonTemplateMapper.render(TEMPLATE_PATH, rawData);
        System.out.println(JsonTemplateMapper.jsonNodeToString(finalResponse));
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

    record PaginationResult(int page, int size, int total, int totalPages, List<?> items) {
    }
}
