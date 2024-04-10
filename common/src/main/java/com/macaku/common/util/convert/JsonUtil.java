package com.macaku.common.util.convert;

import cn.hutool.json.JSONUtil;

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


    // 这个转化不知道之前是什么类型的，因为 1 和 1L 在json中就是 1，默认被认定为 integer 的 1！
    public static <T> T analyzeJson(String json, Class<T> clazz) {
        return JSONUtil.toBean(json, clazz);
    }

    public static <T> T analyzeJsonField(String json, String path, Class<T> clazz) {
        return JSONUtil.parse(json).getByPath(path, clazz);
    }

    public static Object analyzeJsonField(String json, String path) {
        return JSONUtil.parse(json).getByPath(path);
    }
}
