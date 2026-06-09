package com.bank.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
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
import java.beans.Introspector;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class DwrBridgeController {

    private static final Logger logger = LoggerFactory.getLogger(DwrBridgeController.class);

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
//        for (Method method : serviceClass.getMethods()) {
//            if (method.getName().equals(methodName) && method.getParameterTypes().length <= 1) {
//                return method;
//            }
//        }
//        throw new IllegalArgumentException("Unsupported dwr method: " + methodName);
        Method bestMatch = null;
        int minParams = Integer.MAX_VALUE;

        for (Method method : serviceClass.getMethods()) {
            if (method.getName().equals(methodName)) {
                int paramCount = method.getParameterTypes().length;
                // 优先选参数最少的方法（0个优先，其次1个，其次多个）
                if (paramCount < minParams) {
                    minParams = paramCount;
                    bestMatch = method;
                }
            }
        }

        if (bestMatch != null) {
            return bestMatch;
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
        // 多参数方法：前两个通常是 (ServletContext, HttpServletRequest)，DWR 框架自动注入
        // 剩余的业务参数从请求 body 解析
        Object[] args = new Object[parameterTypes.length];
        int businessParamCount = 0;

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramType = parameterTypes[i];
            if (ServletContext.class.isAssignableFrom(paramType)) {
                args[i] = request.getServletContext();
            } else if (HttpServletRequest.class.isAssignableFrom(paramType)) {
                args[i] = request;
            } else {
                // 如果参数类型是 String[]，从 parameterMap 里找 "pks" 参数并解析为数组
                if (paramType.equals(String[].class)) {
                    args[i] = parseStringArrayParam(request);
                } else if (businessParamCount == 0) {
                    args[i] = parseRequest(request, paramType);
                } else {
                    // 多个业务参数时，逐个尝试从 paramMap 中匹配
                    args[i] = parseRequestOrParamMap(request, paramType);
                }
                businessParamCount++;
            }
        }

        return args;
    }

    /**
     * 从 parameterMap 中解析 String[] 类型参数
     * 支持 pks=["1","2","3"] 格式（JSON 数组字符串）
     */
    private String[] parseStringArrayParam(HttpServletRequest request) throws IOException {
        // 先找常见的 pks 参数
        String[] possibleKeys = {"pks", "ids", "keys"};
        for (String key : possibleKeys) {
            String[] vals = request.getParameterMap().get(key);
            if (vals != null && vals.length > 0) {
                String val = vals[0];
                if (val != null && val.trim().startsWith("[")) {
                    // JSON 数组格式：["1","2","3"]
                    logger.info("parseStringArrayParam: key={}, value={}", key, val.substring(0, Math.min(80, val.length())));
                    List<String> list = objectMapper.readValue(val, new TypeReference<List<String>>() {});
                    return list.toArray(new String[0]);
                } else if (val != null && !val.trim().isEmpty()) {
                    // 逗号分隔格式：1,2,3
                    return val.split(",");
                }
            }
        }
        // 如果没找到上述 key，遍历所有参数
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String val = entry.getValue() != null && entry.getValue().length > 0 ? entry.getValue()[0] : null;
            if (val != null && val.trim().startsWith("[")) {
                logger.info("parseStringArrayParam fallback: key={}, value={}", entry.getKey(), val.substring(0, Math.min(80, val.length())));
                List<String> list = objectMapper.readValue(val, new TypeReference<List<String>>() {});
                return list.toArray(new String[0]);
            }
        }
        return new String[0];
    }

    /**
     * 尝试从请求 body（JSON）或 parameterMap（form-urlencoded）解析单个业务参数
     */
    private Object parseRequestOrParamMap(HttpServletRequest request, Class<?> paramType) throws IOException {
        String contentType = request.getContentType();
        // JSON 请求：从 body 解析
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            String body = readBody(request);
            if (body != null && !body.trim().isEmpty()) {
                try {
                    return objectMapper.convertValue(
                            objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {}),
                            paramType);
                } catch (Exception ignored) {
                }
            }
        }
        // form-urlencoded 请求：从 parameterMap 解析（不提前消费 body）
        return parseForm(request, paramType);
    }
    private Object parseRequest(HttpServletRequest request, Class<?> targetType) throws IOException {
        String contentType = request.getContentType();
        logger.info("parseRequest DEBUG: targetType={}, contentType={}", targetType.getName(), contentType);
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            logger.info("parseRequest DEBUG: 走 JSON 解析分支");
            return parseJson(readBody(request), targetType);
        }
        // form 模式下，检测 body 是否为 JSON（以 { 或 [ 开头）
        String body = readBody(request);
        if (body != null) {
            String trimmed = body.trim();
            if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                try {
                    logger.info("parseRequest DEBUG: body 以 {} 开头，尝试 JSON 解析", trimmed.charAt(0));
                    return parseJson(trimmed, targetType);
                } catch (Exception e) {
                    logger.warn("parseRequest WARN: JSON 解析失败，回退到 parseForm: {}", e.getMessage());
                }
            }
        }
        logger.info("parseRequest DEBUG: 走 parseForm 分支");
        return parseForm(request, targetType);
    }

    private String readBody(HttpServletRequest request) throws IOException {
        StringBuilder body = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        String result = body.toString();
        logger.error("readBody DEBUG: bodyLength={}, bodyStartsWith={}",
            result.length(), result.length() > 0 ? result.substring(0, Math.min(200, result.length())) : "<EMPTY>");
        return result;
    }

    private Object parseJson(String value, Class<?> targetType) throws IOException {
        if (value == null || value.trim().isEmpty()) {
            logger.warn("parseJson WARN: value 为空，创建空实例: targetType={}", targetType.getSimpleName());
            return newInstance(targetType);
        }
        String trimmed = value.trim();
        logger.info("parseJson DEBUG: 开始解析 JSON, targetType={}, valueStartsWith={}",
            targetType.getSimpleName(), trimmed.substring(0, Math.min(80, trimmed.length())));

        // JSON 数组 [{...}, {...}]
        if (trimmed.startsWith("[")) {
            List<Map<String, Object>> list = objectMapper.readValue(trimmed, new TypeReference<List<Map<String, Object>>>() {});
            logger.info("parseJson DEBUG: JSON 数组解析, length={}", list.size());
            if (targetType.isArray()) {
                Class<?> componentType = targetType.getComponentType();
                Object array = java.lang.reflect.Array.newInstance(componentType, list.size());
                for (int i = 0; i < list.size(); i++) {
                    java.lang.reflect.Array.set(array, i, convertMapToTarget(list.get(i), componentType));
                }
                return array;
            }
            // 非数组目标类型 + JSON 数组 → 取第一个元素
            if (!list.isEmpty()) {
                return convertMapToTarget(list.get(0), targetType);
            }
            return newInstance(targetType);
        }

        // JSON 对象 {...}
        try {
            Map<String, Object> map = objectMapper.readValue(trimmed, new TypeReference<Map<String, Object>>() {});
            logger.info("parseJson DEBUG: JSON 对象解析, keys={}", map.keySet());
            Object result = convertMapToTarget(map, targetType);
            logger.info("parseJson DEBUG: 转换后对象类型={}, 对象={}", result.getClass().getSimpleName(), result);
            // 尝试打印关键字段（如 tskId）
            try {
                Object idValue = result.getClass().getMethod("getTskId").invoke(result);
                logger.info("parseJson DEBUG: 解析后对象的 tskId={}", idValue);
            } catch (Exception e) {
                logger.info("parseJson DEBUG: 无法获取 tskId (可能不是 TemplateSinglePk)");
            }
            
            // ⚠️ 关键修复：如果 result 是 SingleBean 子类，将 ID 字段的值同步到 primaryKey 字段
            // 因为 Hibernate 可能使用 SingleBean.primaryKey 作为实体 ID
            if (result instanceof com.bank.core.pojo.SingleBean) {
                // 尝试从 map 中获取 ID 字段的值，并设置到 primaryKey
                // 先尝试常见的 ID 字段名
                String[] possibleIdKeys = {"tskId", "id", "pk", "primaryKey"};
                for (String idKey : possibleIdKeys) {
                    if (map.containsKey(idKey)) {
                        Object idVal = map.get(idKey);
                        if (idVal != null) {
                            // 使用原始类型避免泛型编译错误，转换为 String（大部分主键是 String）
                            ((com.bank.core.pojo.SingleBean) result).setPrimaryKey(String.valueOf(idVal));
                            logger.info("parseJson FIX: 将 map 中的 {}={} 同步到 primaryKey", idKey, idVal);
                            break;
                        }
                    }
                }
            }
            
            return result;
        } catch (Exception e) {
            logger.error("parseJson ERROR: 解析失败, targetType={}, error={}", targetType.getSimpleName(), e.getMessage(), e);
            throw e;
        }
    }

    private Object parseForm(HttpServletRequest request, Class<?> targetType) throws IOException {
        // 根据目标类型自动计算实体参数名（如 TemplateSinglePk -> templateSinglePk）
        String entityName = Introspector.decapitalize(targetType.getSimpleName());
        logger.info("parseForm ENTRY: targetType={}, entityName={}, paramMapSize={}",
            targetType.getSimpleName(), entityName, request.getParameterMap().size());

        // 【优先处理】直接从 parameterMap 里取实体名参数，如果是 JSON 则直接解析返回
        // 这样不依赖 for 循环里的判断逻辑，更可靠
        String[] entityValues = request.getParameterMap().get(entityName);
        if (entityValues != null && entityValues.length > 0) {
            String entityValue = entityValues[0];
            if (entityValue != null && entityValue.trim().startsWith("{")) {
                logger.info("parseForm HIT: 直接命中实体名参数 {}={}", entityName,
                    entityValue.substring(0, Math.min(80, entityValue.length())));
                return parseJson(entityValue, targetType);
            }
        }

        // 同样处理 pager 参数
        String[] pagerValues = request.getParameterMap().get("pager");
        if (pagerValues != null && pagerValues.length > 0) {
            String pagerValue = pagerValues[0];
            if (pagerValue != null && pagerValue.trim().startsWith("{")) {
                logger.info("parseForm HIT: 直接命中 pager 参数");
                return parseJson(pagerValue, targetType);
            }
        }

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> queryParams = new HashMap<>();
        Map<Integer, Map<String, Object>> dataListMap = new LinkedHashMap<>();

        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            String value = values != null && values.length > 0 ? values[0] : null;
            logger.info("parseForm PARAM: key={}, valueStartsWith={}", key,
                value != null && !value.isEmpty() ? value.substring(0, Math.min(50, value.length())) : "null");

            // 参数值是 JSON 数组且目标类型也是数组 → 直接 JSON 解析
            if (targetType.isArray() && value != null && value.trim().startsWith("[")) {
                try {
                    logger.info("parseForm: 参数 {} 值为 JSON 数组，目标类型也是数组，直接 JSON 解析", key);
                    return parseJson(value, targetType);
                } catch (Exception e) {
                    logger.warn("parseForm: JSON 数组解析失败，回退: {}", e.getMessage());
                }
            }

            if ("pack".equals(key)) {
                return parseJson(value, targetType);
            } else if (entityName.equals(key) && value != null && value.trim().startsWith("{")) {
                // 自动识别实体名参数：key 与实体类名匹配，且值为 JSON 对象字符串
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
        // 当目标类型是数组时，将单个 Map 包装成单元素数组
        // 例: {loanId: "xxx", tskId: "yyy"} → PKTemplateCompPk[{loanId: "xxx", tskId: "yyy"}]
        if (targetType.isArray()) {
            Class<?> componentType = targetType.getComponentType();
            Object single = objectMapper.convertValue(normalizeMapForClass(source, componentType), componentType);
            Object array = java.lang.reflect.Array.newInstance(componentType, 1);
            java.lang.reflect.Array.set(array, 0, single);
            return array;
        }
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