package com.macaku.common.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 13:51
 */
public class ExtractUtil {

    public final static String OPENID = "openid";

    public final static String SESSION_KEY = "session_key";

    public final static String ID = "id";


    public static String getOpenIDFromJWT(HttpServletRequest request) {
        return (String) JwtUtil.getJWTRawDataOnRequest(request, Map.class).get(OPENID);
    }

    public static String getSessionKeyFromJWT(HttpServletRequest request) {
        return (String) JwtUtil.getJWTRawDataOnRequest(request, Map.class).get(SESSION_KEY);
    }

    public static String getUserIdFromJWT(HttpServletRequest request) {
        return (String) JwtUtil.getJWTRawDataOnRequest(request, Map.class).get(ID);
    }

}
