package com.thanhlv.DynamicJsonMapper;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Common Utility để xử lý Dynamic JSON Response từ Template
 */
public class JsonTemplateMapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    // Cache giúp tránh việc đọc I/O liên tục, tăng tốc độ phản hồi
    private static final Map<String, JsonNode> cache = new ConcurrentHashMap<>();

    /**
     * Render dữ liệu vào template JSON
     * * @param templatePath Đường dẫn file trong folder resources
     *
     * @param rawData Map chứa dữ liệu nguồn (ví dụ: "[name]" -> "Thanh")
     * @return JsonNode đã được map dữ liệu
     */
    public static JsonNode render(String templatePath, Map<String, Object> rawData) {
        try {
            // Lấy template từ cache hoặc load mới từ resources
            JsonNode rootTemplate = cache.computeIfAbsent(templatePath, path -> {
                try (InputStream is = JsonTemplateMapper.class.getClassLoader().getResourceAsStream(path)) {
                    if (is == null) throw new IllegalArgumentException("Template không tồn tại: " + path);
                    return mapper.readTree(is);
                } catch (Exception e) {
                    throw new RuntimeException("Lỗi khi load template file", e);
                }
            });

            // Quan trọng: Sử dụng deepCopy() để tránh sửa đè lên bản gốc trong cache
            return processNode(rootTemplate.deepCopy(), rawData);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi render dynamic JSON", e);
        }
    }

    public static String jsonNodeToString(JsonNode node) {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
    }

    private static JsonNode processNode(JsonNode node, Map<String, Object> rawData) {
        // 1. Xử lý Object Node
        if (node instanceof ObjectNode obj) {
            // Trong Jackson 3, dùng properties() để duyệt qua các entry
            // Nếu không thấy properties(), bạn có thể dùng fieldNames() hoặc iterator()
            var fields = obj.properties().iterator();

            while (fields.hasNext()) {
                var entry = fields.next();
                String key = entry.getKey().toString(); // Lấy key
                JsonNode value = entry.getValue();      // Lấy value

                // Đệ quy để cập nhật node con
                obj.set(key, processNode(value, rawData));
            }
            return obj;
        }

        // 2. Xử lý Array Node
        if (node instanceof ArrayNode array) {
            for (int i = 0; i < array.size(); i++) {
                array.set(i, processNode(array.get(i), rawData));
            }
            return array;
        }

        // 3. Xử lý Text Node (Mapping logic)
        if (node.isTextual()) {
            String text = node.asText();
            if (text.startsWith("[") && text.endsWith("]")) {
                Object value = rawData.get(text);
                return mapper.valueToTree(value);
            }
        }

        return node;
    }

    /**
     * Làm mới cache (dùng khi bạn cập nhật file template mà không muốn restart server)
     */
    public static void refreshCache() {
        cache.clear();
    }
}
