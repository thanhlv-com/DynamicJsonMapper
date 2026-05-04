package com.thanhlv.dynamicjsonmapper;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.InputStream;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Common Utility để xử lý Dynamic JSON Response từ Template
 */
public final class JsonTemplateMapper {
    private static final String TEMPLATE_REF_KEY = "$template_ref";
    private static final String TEMPLATE_SOURCE_KEY = "$source";
    private static final String PLACEHOLDER_PREFIX = "${";
    private static final String PLACEHOLDER_SUFFIX = "}";

    private static final ObjectMapper mapper = new ObjectMapper();

    // Cache giúp tránh việc đọc I/O liên tục, tăng tốc độ phản hồi
    private static final Map<String, JsonNode> cache = new ConcurrentHashMap<>();

    /**
     * Render dữ liệu vào template JSON
     * * @param templatePath Đường dẫn file trong folder resources
     *
     * @param rawData Map chứa dữ liệu nguồn (ví dụ: "${name}" -> "Thanh")
     * @return JsonNode đã được map dữ liệu
     */
    public static JsonNode render(String templatePath, Map<String, Object> rawData) {
        try {
            // Lấy template từ cache hoặc load mới từ resources
            JsonNode rootTemplate = getTemplate(templatePath);

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
            JsonNode templateRefResult = resolveTemplateRef(obj, rawData);
            if (templateRefResult != null) {
                return templateRefResult;
            }

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
            if (isPlaceholder(text)) {
                Object value = rawData.get(text);
                return mapper.valueToTree(value);
            }
        }

        return node;
    }

    private static JsonNode resolveTemplateRef(ObjectNode node, Map<String, Object> rawData) {
        JsonNode templateRefNode = node.get(TEMPLATE_REF_KEY);
        if (templateRefNode == null) {
            return null;
        }

        JsonNode sourceNode = node.get(TEMPLATE_SOURCE_KEY);
        if (sourceNode == null || !sourceNode.isTextual()) {
            throw new IllegalArgumentException(
                    "Template ref bắt buộc có field textual '" + TEMPLATE_SOURCE_KEY + "'");
        }
        if (!templateRefNode.isTextual()) {
            throw new IllegalArgumentException(
                    "Template ref bắt buộc có field textual '" + TEMPLATE_REF_KEY + "'");
        }

        String sourceToken = sourceNode.asText();
        if (!isPlaceholder(sourceToken)) {
            throw new IllegalArgumentException(
                    "Template ref '" + TEMPLATE_SOURCE_KEY + "' phải là placeholder dạng ${key}");
        }

        Object sourceValue = rawData.get(sourceToken);
        if (sourceValue == null) {
            return NullNode.getInstance();
        }

        String childTemplatePath = templateRefNode.asText();
        JsonNode sourceNodeValue = mapper.valueToTree(sourceValue);
        if (sourceNodeValue.isArray()) {
            ArrayNode renderedItems = mapper.createArrayNode();
            for (JsonNode itemNode : sourceNodeValue) {
                renderedItems.add(renderChildTemplate(childTemplatePath, rawData, itemNode));
            }
            return renderedItems;
        }

        if (sourceNodeValue.isObject()) {
            return renderChildTemplate(childTemplatePath, rawData, sourceNodeValue);
        }

        throw new IllegalArgumentException(
                "Template ref '" + TEMPLATE_SOURCE_KEY + "' phải ánh xạ đến dữ liệu kiểu object hoặc array");
    }

    private static JsonNode renderChildTemplate(String childTemplatePath, Map<String, Object> parentRawData, JsonNode itemNode) {
        Map<String, Object> childRawData = new HashMap<>(parentRawData);
        if (itemNode instanceof ObjectNode itemObject) {
            var fields = itemObject.properties().iterator();
            while (fields.hasNext()) {
                var entry = fields.next();
                childRawData.put(toPlaceholder(entry.getKey()), mapper.convertValue(entry.getValue(), Object.class));
            }
        } else {
            Object primitiveValue = mapper.convertValue(itemNode, Object.class);
            JsonNode childTemplate = getTemplate(childTemplatePath);
            Set<String> placeholders = collectPlaceholders(childTemplate);
            for (String placeholder : placeholders) {
                childRawData.putIfAbsent(placeholder, primitiveValue);
            }
        }
        return render(childTemplatePath, childRawData);
    }

    private static JsonNode getTemplate(String templatePath) {
        return cache.computeIfAbsent(templatePath, path -> {
            try (InputStream is = JsonTemplateMapper.class.getClassLoader().getResourceAsStream(path)) {
                if (is == null) throw new IllegalArgumentException("Template không tồn tại: " + path);
                return mapper.readTree(is);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi load template file", e);
            }
        });
    }

    private static Set<String> collectPlaceholders(JsonNode node) {
        Set<String> placeholders = new HashSet<>();
        collectPlaceholders(node, placeholders);
        return placeholders;
    }

    private static void collectPlaceholders(JsonNode node, Set<String> placeholders) {
        if (node == null || node.isNull()) {
            return;
        }

        if (node.isTextual()) {
            String value = node.asText();
            if (isPlaceholder(value)) {
                placeholders.add(value);
            }
            return;
        }

        if (node instanceof ObjectNode objectNode) {
            var fields = objectNode.properties().iterator();
            while (fields.hasNext()) {
                collectPlaceholders(fields.next().getValue(), placeholders);
            }
            return;
        }

        if (node instanceof ArrayNode arrayNode) {
            for (JsonNode item : arrayNode) {
                collectPlaceholders(item, placeholders);
            }
        }
    }

    private static boolean isPlaceholder(String text) {
        return text.startsWith(PLACEHOLDER_PREFIX) && text.endsWith(PLACEHOLDER_SUFFIX);
    }

    private static String toPlaceholder(String key) {
        return PLACEHOLDER_PREFIX + key + PLACEHOLDER_SUFFIX;
    }

    /**
     * Làm mới cache (dùng khi bạn cập nhật file template mà không muốn restart server)
     */
    public static void refreshCache() {
        cache.clear();
    }
}
