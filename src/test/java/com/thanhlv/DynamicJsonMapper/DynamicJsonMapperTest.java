package com.thanhlv.dynamicjsonmapper;

import tools.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class DynamicJsonMapperTest {
    private static final String TEMPLATE_PATH = "templates/user-template.json";
    private static final JsonTemplateMapper MAPPER = JsonTemplateMapper.defaultInstance();

    public static void main(String[] args) {
        // 1. Giả lập dữ liệu dynamic (Ví dụ lấy từ DB hoặc từ một Map lồng)
        final Map<String, Object> rawData = new HashMap<>();
        rawData.put("${name}", "Thanh");
        rawData.put("${noio}", "Hà Nội");
        rawData.put("${tuoi}", 23);

        // 2. Thực hiện Mapping từ template file trong resources
        final JsonNode finalJsonResponse = MAPPER.render(TEMPLATE_PATH, rawData);

        // 4. In kết quả
        System.out.println("--- KẾT QUẢ API RESPONSE ---");
        System.out.println(MAPPER.jsonNodeToString(finalJsonResponse));
    }
}
