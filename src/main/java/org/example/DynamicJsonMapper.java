package org.example;

import tools.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class DynamicJsonMapper {
    public static void main(String[] args) {
        // 1. Giả lập dữ liệu dynamic (Ví dụ lấy từ DB hoặc từ một Map lồng)
        final Map<String, Object> rawData = new HashMap<>();
        rawData.put("[name]", "Thanh");
        rawData.put("[noio]", "Hà Nội");
        rawData.put("[tuoi]", 23);

        // 2. Đường dẫn file template (nằm trong src/main/resources)
        String templatePath = "templates/user-template.json";

        // 3. Thực hiện Mapping
        final JsonNode finalJsonResponse = JsonTemplateMapper.render(templatePath, rawData);

        // 4. In kết quả
        System.out.println("--- KẾT QUẢ API RESPONSE ---");
        System.out.println(JsonTemplateMapper.jsonNodeToString(finalJsonResponse));
    }
}