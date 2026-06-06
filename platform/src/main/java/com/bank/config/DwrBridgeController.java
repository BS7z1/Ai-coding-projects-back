package com.bank.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class DwrBridgeController {

    private static final Pattern DATA_LIST_FIELD_PATTERN = Pattern.compile("dataList\\[(\\d+)](?:\\.|\\[)([^\\]]+)]?");
    private static final Pattern QUERY_PARAMS_FIELD_PATTERN = Pattern.compile("queryParams(?:\\.|\\[)([^\\]]+)]?");
    private static final String DWR_PACKAGE_MARKER = "controller.dwr";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/{module}/{methodName}")
    public Object invoke(@PathVariable String module,
                         @PathVariable String methodName,
                         HttpServletRequest request) throws Exception {
        String beanName = moduleToBeanName(module);
        Object service = getDwrService(beanName);
        Method method = findDwrMethod(service.getClass(), methodName);
        Object[] args = buildMethodArgs(method, request);
        try {
            return method.invoke(service, args);
        } catch (InvocationTargetException ex) {
            Throwable targetException = ex.getTargetException();
            if (targetException instanceof Exception) {
                throw (Exception) targetException;
            }
            throw ex;
        }
    }

    @PostMapping("/{area}/{module}/{serviceName}/{methodName}")
    public Object invokeLegacyDwr(@PathVariable String area,
                                  @PathVariable String module,
                                  @PathVariable String serviceName,
                                  @PathVariable String methodName,
                                  HttpServletRequest request) throws Exception {
        Object service = getDwrService(serviceName);
        Method method = findDwrMethod(service.getClass(), methodName);
        Object[] args = buildMethodArgs(method, request);

        try {
            return method.invoke(service, args);
        } catch (InvocationTargetException ex) {
            Throwable targetException = ex.getTargetException();
            if (targetException instanceof Exception) {
                throw (Exception) targetException;
            }
            throw ex;
        }
    }

    /**
     * 将模块名(kebab-case) 转换为 DWR Service 的 Spring Bean 名称(camelCase)
     * 例: "loan-ledger" → "dwrLoanLedgerService"
     *     "template-single-pk" → "dwrTemplateSinglePkService"
     */
    private String moduleToBeanName(String module) {
        if (module == null || module.isEmpty()) {
            throw new IllegalArgumentException("Module name is required");
        }
        StringBuilder pascal = new StringBuilder();
        for (String part : module.split("-")) {
            if (!part.isEmpty()) {
                pascal.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    pascal.append(part.substring(1));
                }
            }
        }
        String className = "Dwr" + pascal + "Service";
        // Spring 默认 bean 名称: 类名首字母小写
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }

    /**
     * 从 Spring 容器获取 DWR Service Bean，并校验其位于 controller.dwr 包下
     */
    private Object getDwrService(String beanName) {
        if (!applicationContext.containsBean(beanName)) {
            throw new IllegalArgumentException("DWR service bean not found: " + beanName);
        }
        Object service = applicationContext.getBean(beanName);
        // 安全校验：确保 Bean 类位于 controller.dwr 包下
        String className = service.getClass().getName();
        if (!className.contains(DWR_PACKAGE_MARKER)) {
            throw new IllegalArgumentException(
                    "Bean " + beanName + " (" + className + ") is not a valid DWR service. " +
                    "DWR services must be in a package containing 'controller.dwr'.");
        }
        return service;
    }

    private Method findDwrMethod(Class<?> serviceClass, String methodName) {
        for (Method method : serviceClass.getMethods()) {
            if (method.getName().equals(methodName) && method.getParameterTypes().length <= 1) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unsupported dwr method: " + methodName);
    }

    private Object[] buildMethodArgs(Method method, HttpServletRequest request) throws IOException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            return new Object[0];
        }
        if (parameterTypes.length == 1) {
            return new Object[]{parseRequest(request, parameterTypes[0])};
        }
        throw new IllegalArgumentException("Only zero-arg or one-arg dwr methods are supported");
    }

    private Object parseRequest(HttpServletRequest request, Class<?> targetType) throws IOException {
        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            return parseJson(readBody(request), targetType);
        }
        return parseForm(request, targetType);
    }

    private String readBody(HttpServletRequest request) throws IOException {
        StringBuilder body = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        return body.toString();
    }

    private Object parseJson(String value, Class<?> targetType) throws IOException {
        if (value == null || value.trim().isEmpty()) {
            return newInstance(targetType);
        }
        Map<String, Object> map = objectMapper.readValue(value, new TypeReference<Map<String, Object>>() {});
        return convertMapToTarget(map, targetType);
    }

    private Object parseForm(HttpServletRequest request, Class<?> targetType) throws IOException {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> queryParams = new HashMap<>();
        Map<Integer, Map<String, Object>> dataListMap = new LinkedHashMap<>();

        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            String value = values != null && values.length > 0 ? values[0] : null;

            if ("pack".equals(key)) {
                return parseJson(value, targetType);
            } else if ("operator".equals(key)) {
                map.put("userId", value);
            } else if ("id".equals(key)) {
                if (value != null && !value.trim().isEmpty()) {
                    map.put("ids", Collections.singletonList(Long.valueOf(value)));
                }
            } else if ("ids".equals(key) || "ids[]".equals(key)) {
                map.put("ids", parseLongList(values));
            } else if ("queryParams".equals(key)) {
                queryParams.putAll(parseObjectMap(value));
            } else if ("dataList".equals(key)) {
                map.put("dataList", objectMapper.readValue(value, new TypeReference<List<Object>>() {}));
            } else if ("loanLedger".equals(key)) {
                Map<String, Object> data = objectMapper.readValue(value, new TypeReference<Map<String, Object>>() {});
                map.put("dataList", Collections.singletonList(data));
            } else {
                Matcher queryMatcher = QUERY_PARAMS_FIELD_PATTERN.matcher(key);
                Matcher dataMatcher = DATA_LIST_FIELD_PATTERN.matcher(key);
                Field targetField = findField(targetType, key);
                if (queryMatcher.matches()) {
                    queryParams.put(queryMatcher.group(1), value);
                } else if (dataMatcher.matches()) {
                    Integer index = Integer.valueOf(dataMatcher.group(1));
                    String fieldName = dataMatcher.group(2);
                    dataListMap.computeIfAbsent(index, i -> new HashMap<>()).put(fieldName, value);
                } else if (targetField != null) {
                    map.put(key, parseFieldValue(value, targetField));
                } else {
                    map.put(key, value);
                }
            }
        }

        if (!queryParams.isEmpty()) {
            map.put("queryParams", queryParams);
        }
        if (!dataListMap.isEmpty()) {
            map.put("dataList", new ArrayList<>(dataListMap.values()));
        }
        return convertMapToTarget(map, targetType);
    }

    private Object parseFieldValue(String value, Field field) throws IOException {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("{")) {
            return objectMapper.readValue(trimmed, new TypeReference<Map<String, Object>>() {});
        }
        if (trimmed.startsWith("[")) {
            if (field.getType().isArray()) {
                return objectMapper.readValue(trimmed, new TypeReference<List<Object>>() {}).toArray();
            }
            return objectMapper.readValue(trimmed, new TypeReference<List<Object>>() {});
        }
        return value;
    }

    private List<Long> parseLongList(String[] values) throws IOException {
        List<Long> ids = new ArrayList<>();
        if (values == null) {
            return ids;
        }
        for (String value : values) {
            if (value == null || value.trim().isEmpty()) {
                continue;
            }
            String trimmed = value.trim();
            if (trimmed.startsWith("[")) {
                ids.addAll(objectMapper.readValue(trimmed, new TypeReference<List<Long>>() {}));
            } else {
                for (String item : trimmed.split(",")) {
                    if (!item.trim().isEmpty()) {
                        ids.add(Long.valueOf(item.trim()));
                    }
                }
            }
        }
        return ids;
    }

    private Map<String, Object> parseObjectMap(String value) throws IOException {
        if (value == null || value.trim().isEmpty()) {
            return new HashMap<>();
        }
        return objectMapper.readValue(value, new TypeReference<Map<String, Object>>() {});
    }

    private Object convertMapToTarget(Map<String, Object> source, Class<?> targetType) {
        return objectMapper.convertValue(normalizeMapForClass(source, targetType), targetType);
    }

    private Map<String, Object> normalizeMapForClass(Map<String, Object> source, Class<?> targetType) {
        Map<String, Object> normalized = new HashMap<>();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            Field field = findField(targetType, entry.getKey());
            if (field == null) {
                normalized.put(entry.getKey(), entry.getValue());
            } else {
                normalized.put(entry.getKey(), normalizeValue(entry.getValue(), field.getType(), field.getGenericType()));
            }
        }
        return normalized;
    }

    @SuppressWarnings("unchecked")
    private Object normalizeValue(Object value, Class<?> fieldType, Type genericType) {
        if (value == null) {
            return null;
        }
        if (Integer.class.equals(fieldType) || int.class.equals(fieldType)) {
            return parseIntegerValue(value);
        }
        if (Long.class.equals(fieldType) || long.class.equals(fieldType)) {
            return parseLongValue(value);
        }
        if (java.sql.Date.class.equals(fieldType)) {
            return parseSqlDateValue(value);
        }
        if (Timestamp.class.equals(fieldType)) {
            return parseTimestampValue(value);
        }
        if (fieldType.isArray()) {
            return normalizeArrayValue(value, fieldType.getComponentType());
        }
        if (List.class.isAssignableFrom(fieldType) && value instanceof List) {
            Class<?> elementType = getListElementType(genericType);
            List<Object> list = new ArrayList<>();
            for (Object item : (List<?>) value) {
                if (item instanceof Map && elementType != null) {
                    list.add(convertMapToTarget((Map<String, Object>) item, elementType));
                } else {
                    list.add(item);
                }
            }
            return list;
        }
        if (value instanceof Map && !Map.class.isAssignableFrom(fieldType)) {
            return convertMapToTarget((Map<String, Object>) value, fieldType);
        }
        return value;
    }

    private Object normalizeArrayValue(Object value, Class<?> componentType) {
        List<?> values;
        if (value instanceof List) {
            values = (List<?>) value;
        } else if (value instanceof Object[]) {
            values = java.util.Arrays.asList((Object[]) value);
        } else {
            String text = String.valueOf(value).trim();
            if (text.isEmpty()) {
                values = Collections.emptyList();
            } else {
                values = java.util.Arrays.asList(text.split(","));
            }
        }

        Object array = java.lang.reflect.Array.newInstance(componentType, values.size());
        for (int i = 0; i < values.size(); i++) {
            Object item = values.get(i);
            if (String.class.equals(componentType)) {
                java.lang.reflect.Array.set(array, i, String.valueOf(item).trim());
            } else if (Long.class.equals(componentType) || long.class.equals(componentType)) {
                java.lang.reflect.Array.set(array, i, parseLongValue(item));
            } else if (Integer.class.equals(componentType) || int.class.equals(componentType)) {
                java.lang.reflect.Array.set(array, i, parseIntegerValue(item));
            } else {
                java.lang.reflect.Array.set(array, i, item);
            }
        }
        return array;
    }

    private Integer parseIntegerValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return null;
        }
        if ("1".equals(text) || "true".equalsIgnoreCase(text) || "yes".equalsIgnoreCase(text)
                || "y".equalsIgnoreCase(text) || "\u662f".equals(text) || text.startsWith("\u93c4")) {
            return 1;
        }
        if ("0".equals(text) || "false".equalsIgnoreCase(text) || "no".equalsIgnoreCase(text)
                || "n".equalsIgnoreCase(text) || "\u5426".equals(text) || text.startsWith("\u935a")) {
            return 0;
        }
        return Integer.valueOf(text);
    }

    private Long parseLongValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : Long.valueOf(text);
    }

    private java.sql.Date parseSqlDateValue(Object value) {
        if (value instanceof java.sql.Date) {
            return (java.sql.Date) value;
        }
        Date date = parseDateValue(value);
        return date == null ? null : new java.sql.Date(date.getTime());
    }

    private Timestamp parseTimestampValue(Object value) {
        if (value instanceof Timestamp) {
            return (Timestamp) value;
        }
        Date date = parseDateValue(value);
        return date == null ? null : new Timestamp(date.getTime());
    }

    private Date parseDateValue(Object value) {
        if (value instanceof Date) {
            return (Date) value;
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return null;
        }
        String[] patterns = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm",
                "yyyy-MM-dd",
                "yyyy-MM-dd'T'HH:mm:ss.SSSX",
                "yyyy-MM-dd'T'HH:mm:ssX"
        };
        for (String pattern : patterns) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                format.setLenient(false);
                return format.parse(text);
            } catch (ParseException ignored) {
            }
        }
        throw new IllegalArgumentException("Unsupported date format: " + text);
    }

    private Field findField(Class<?> type, String fieldName) {
        Class<?> current = type;
        while (current != null && !Object.class.equals(current)) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private Class<?> getListElementType(Type genericType) {
        if (!(genericType instanceof ParameterizedType)) {
            return null;
        }
        Type actualType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
        return actualType instanceof Class ? (Class<?>) actualType : null;
    }

    private Object newInstance(Class<?> targetType) {
        try {
            return targetType.newInstance();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot create request object: " + targetType.getName(), ex);
        }
    }
}