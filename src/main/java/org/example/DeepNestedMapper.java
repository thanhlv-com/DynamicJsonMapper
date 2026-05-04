package org.example;


import tools.jackson.databind.JsonNode;
import java.util.Map;

public class DeepNestedMapper {

    public static void main(String[] args) {
        // 1. Dữ liệu gốc "phẳng" hoặc "lồng" (Raw Data)
        // Giả sử các key được đặt theo style đặc thù của hệ thống cũ
        Map<String, Object> rawData = Map.of(
                "[id]", "USR-99",
                "[name]", "Thanh",
                "[city]", "Hà Nội",
                "[dist]", "Cầu Giấy",
                "[street]", "Duy Tân",
                "[skill_primary]", "Java",
                "[skill_level]", "Senior"
        );

        // 2. Load template file và mapping bằng JsonTemplateMapper
        String templatePath = "templates/deep-nested-template.json";
        JsonNode finalResponse = JsonTemplateMapper.render(templatePath, rawData);

        // 3. In kết quả
        System.out.println(JsonTemplateMapper.jsonNodeToString(finalResponse));
    }
}
