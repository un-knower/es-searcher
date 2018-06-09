package com.star.searcher.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2018-06-06 01:03:00
 */
public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 对象映射
     */
    private static final ObjectMapper OBJ_MAPPER = new ObjectMapper();

    static {
        OBJ_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJ_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        OBJ_MAPPER.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
        OBJ_MAPPER.setSerializationInclusion(Include.NON_NULL);
    }


    /**
     * Java对象转换为Json串
     *
     * @param obj Java对象
     * @return Json串
     */
    public static String toJson(Object obj) {
        String rst;
        if (obj == null || obj instanceof String) {
            return (String) obj;
        }
        try {
            rst = OBJ_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("将Java对象转换成Json串出错！");
            throw new RuntimeException("将Java对象转换成Json串出错！", e);
        }
        return rst;
    }

    /**
     * Json串转换为Java对象
     *
     * @param json Json串
     * @param type Java对象类型
     * @return Java对象
     */
    public static <T> T fromJson(String json, Class<T> type) {
        T rst;
        try {
            rst = OBJ_MAPPER.readValue(json, type);
        } catch (Exception e) {
            logger.error("Json串转换成对象出错：{}", json);
            throw new RuntimeException("Json串转换成对象出错!", e);
        }
        return rst;
    }

    /**
     * Json串转换为Java对象
     * <br>使用引用类型，适用于List&ltObject&gt、Set&ltObject&gt 这种无法直接获取class对象的场景
     * <br>使用方法：TypeReference ref = new TypeReference&ltList&ltInteger&gt&gt(){};
     *
     * @param json    Json串
     * @param typeRef Java对象类型引用
     * @return Java对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        T rst;
        try {
            rst = OBJ_MAPPER.readValue(json, typeRef);
        } catch (Exception e) {
            logger.error("Json串转换成对象出错：{}", json);
            throw new RuntimeException("Json串转换成对象出错!", e);
        }
        return rst;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String json) {
        Map<String, Object> map;
        try {
            map = OBJ_MAPPER.readValue(json, HashMap.class);
        } catch (Exception e) {
            map = null;
            logger.error("Json串转换成对象出错：{}", json);
        }
        return map;
    }
}