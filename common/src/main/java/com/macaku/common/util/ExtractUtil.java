package com.macaku.common.util;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 13:51
 */
@Slf4j
public class ExtractUtil {

    public final static String OPENID = "openid";

    public final static String SESSION_KEY = "session_key";

    public final static String ID = "id";

    private static final RedisCache REDIS_CACHE = SpringUtil.getBean(RedisCache.class);


    public static <T> T getJWTRawDataOnRequest(HttpServletRequest request, Class<T> clazz) {
        final String token = request.getHeader(JwtUtil.JWT_HEADER);
        if(Objects.isNull(token)) {
            return null;
        }
        String redisKey = JwtUtil.JWT_RAW_DATA_MAP + token;
        String rawData = (String) REDIS_CACHE.getCacheObject(redisKey)
                .orElseGet(() -> {
                    String raw = JwtUtil.parseJWTRawData(token);
                    REDIS_CACHE.setCacheObject(redisKey, raw, JwtUtil.JWT_MAP_TTL, JwtUtil.JWT_TTL_UNIT);
                    return raw;
                });
        return JsonUtil.analyzeJson(rawData, clazz);
    }

    public static Map<String, Object> getMapFromJWT(HttpServletRequest request) {
        return getJWTRawDataOnRequest(request, Map.class);
    }

    public static String getOpenIDFromJWT(HttpServletRequest request) {
        return (String) getMapFromJWT(request).get(OPENID);
    }

    public static String getSessionKeyFromJWT(HttpServletRequest request) {
        return (String) getMapFromJWT(request).get(SESSION_KEY);
    }

    // 获取 json 中的数字类型的元素，要进行判断~
    public static Long getUserIdFromJWT(HttpServletRequest request) {
        Object ret = getMapFromJWT(request).get(ID);
        if(ret instanceof Integer) {
            return ((Integer) ret).longValue();
        }
        return (Long) ret;
    }

}
