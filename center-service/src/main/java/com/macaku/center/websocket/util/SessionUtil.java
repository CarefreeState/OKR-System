package com.macaku.center.websocket.util;

import com.macaku.common.exception.GlobalServiceException;

import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 13:59
 */
public class SessionUtil {

    public static void close(Session session) {
        if(Objects.isNull(session)) {
            return;
        }
        try {
            synchronized (session) {
                if(session.isOpen()) {
                    session.close();
                }
            }
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static Map<String, String> getPathParameter(Session session) {
        return session.getPathParameters();
    }

    public static Map<String, Object> getUserProperties(Session session) {
        return session.getUserProperties();
    }

    public static String getQueryString(Session session) {
        return session.getQueryString();
    }

    public static Map<String, List<String>> getParameterMap(Session session) {
        return session.getRequestParameterMap();
    }

    public static URI getRequestURI(Session session) {
        return session.getRequestURI();
    }

}
