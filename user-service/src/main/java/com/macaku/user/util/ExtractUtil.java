package com.macaku.user.util;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.util.JsonUtil;
import com.macaku.common.util.JwtUtil;
import com.macaku.redis.repository.RedisCache;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

    public final static String UNIONID = "unionid";

    public final static String SESSION_KEY = "session_key";

    public final static String ID = "id";

    private static final RedisCache REDIS_CACHE = SpringUtil.getBean(RedisCache.class);

    private static final String TOKEN_BLACKLIST = "tokenBlacklist:";


    public static <T> T getJWTRawDataOnRequest(HttpServletRequest request, Class<T> clazz) {
        final String token = request.getHeader(JwtUtil.JWT_HEADER);
        if(Objects.isNull(token)) {
            return null;
        }
        String rawData = JwtUtil.parseJWTRawData(token);
        return JsonUtil.analyzeJson(rawData, clazz);
    }

    public static void joinTheTokenBlacklist(HttpServletRequest request) {
        final String token = request.getHeader(JwtUtil.JWT_HEADER);
        String redisKey = TOKEN_BLACKLIST + token;
        long keyTTL = JwtUtil.getExpiredDate(token).getTime() - System.currentTimeMillis();
        REDIS_CACHE.setCacheObject(redisKey, Boolean.TRUE, keyTTL, TimeUnit.MILLISECONDS);
    }

    public static Boolean isInTheTokenBlacklist(HttpServletRequest request) {
        final String token = request.getHeader(JwtUtil.JWT_HEADER);
        String redisKey = TOKEN_BLACKLIST + token;
        return (Boolean) REDIS_CACHE.getCacheObject(redisKey).orElse(Boolean.FALSE);
    }

    public static Map<String, Object> getMapFromJWT(HttpServletRequest request) {
        return getJWTRawDataOnRequest(request, Map.class);
    }

    public static String getOpenIDFromJWT(HttpServletRequest request) {
        return (String) getMapFromJWT(request).get(OPENID);
    }

    public static String getUnionIDFromJWT(HttpServletRequest request) {
        return (String) getMapFromJWT(request).get(UNIONID);
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
