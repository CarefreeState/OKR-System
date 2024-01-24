package com.macaku.common.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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


    public static Map<String, Object> getMapFromJWT(HttpServletRequest request) {
        return JwtUtil.getJWTRawDataOnRequest(request, Map.class);
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
