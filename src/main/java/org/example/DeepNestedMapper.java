package org.example;


import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.node.ObjectNode;

import java.util.Map;

public class DeepNestedMapper {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.isEnabled(SerializationFeature.INDENT_OUTPUT);

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

        // 2. Template định nghĩa cấu trúc JSON mong muốn (3 cấp)
        // Cấu trúc này có thể đọc từ 1 file JSON hoặc DB
        Map<String, Object> template = Map.of(
                "user_id", "[id]",
                "profile", Map.of(
                        "display_name", "[name]",
                        "address", Map.of(
                                "city", "[city]",
                                "district", "[dist]",
                                "detail", "[street]"
                        )
                ),
                "expertise", Map.of(
                        "main", "[skill_primary]",
                        "rank", "[skill_level]"
                )
        );

        // 3. Thực hiện mapping đệ quy
        ObjectNode finalResponse = mapRecursive(mapper, rawData, template);

        // 4. In kết quả
        System.out.println(mapper.writeValueAsString(finalResponse));
    }

    /**
     * Hàm đệ quy để xử lý mapping không giới hạn độ sâu
     */
    @SuppressWarnings("unchecked")
    public static ObjectNode mapRecursive(ObjectMapper mapper, Map<String, Object> rawData, Map<String, Object> template) {
        ObjectNode resultNode = mapper.createObjectNode();

        template.forEach((key, value) -> {
            if (value instanceof Map) {
                // Nếu giá trị trong template là 1 Map -> Tiếp tục đệ quy xuống cấp sâu hơn
                resultNode.set(key, mapRecursive(mapper, rawData, (Map<String, Object>) value));
            } else if (value instanceof String) {
                // Nếu là String -> Lấy dữ liệu từ rawData dựa trên key định nghĩa trong template
                String sourceKey = (String) value;
                Object actualValue = rawData.get(sourceKey);

                // Đổ dữ liệu vào (có thể check null hoặc ép kiểu tùy ý ở đây)
                if (actualValue != null) {
                    resultNode.put(key, actualValue.toString());
                } else {
                    resultNode.putNull(key);
                }
            }
        });

        return resultNode;
    }
}