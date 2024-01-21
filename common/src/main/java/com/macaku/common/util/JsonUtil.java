package com.macaku.common.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 12:17
 */
public class JsonUtil {

    public static <T> String analyzeData(T data) {
        return JSONUtil.parse(data).toStringPretty();
    }

    public static <T> T analyzeJson(String json, Class<T> clazz) {
        return JSONUtil.toBean(json, clazz);
    }

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        String json = analyzeData(map);
        JSONObject map1 = analyzeJson(json, JSONObject.class);
        System.out.println(map1);
        System.out.println((String) map1.get("key1"));
        json = analyzeData(map1);
        System.out.println(json);

    }


}
