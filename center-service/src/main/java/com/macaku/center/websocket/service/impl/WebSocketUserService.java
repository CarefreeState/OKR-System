package com.macaku.center.websocket.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.center.websocket.service.WebSocketService;
import com.macaku.center.websocket.session.SessionMap;
import com.macaku.center.websocket.session.impl.SessionRedisMap;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.convert.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ServerEndpoint("/test/{userId}")
@Slf4j
@Component
public class WebSocketUserService implements WebSocketService {

    private Session session;

    private HandshakeRequest handshakeRequest;

    private final static String WEB_SOCKET_USER_SERVICE = "WebSocketUserService:";

    private final static SessionMap SESSION_MAP = SpringUtil.getBean(SessionRedisMap.class);

    @Override
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) throws DeploymentException {
        this.session = session;
        this.handshakeRequest = (HandshakeRequest) session.getUserProperties().get("javax.websocket.server.HandshakeRequest");
        String redisKey = WEB_SOCKET_USER_SERVICE + userId;
        if (SESSION_MAP.containsKey(redisKey)) {
            SESSION_MAP.remove(redisKey);
        }
        SESSION_MAP.put(userId, this.session);
        log.info("用户连接:" + userId + ",当前在线人数为:" + getOnlineCount());
        sendMessage(userId, "连接成功");
//        throw new DeploymentException("拒绝连接");
    }

    @Override
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") String userId) {
        log.info("用户消息:" + userId + ",报文:" + message);
        //可以群发消息
        //消息保存到数据库、redis
        String redisKey = WEB_SOCKET_USER_SERVICE + userId;
        if (StringUtils.hasText(message)) {
            //解析发送的报文
            //追加发送人
            String returnJson = JsonUtil.jsonBuilder(message).put("fromUserId", userId).buildJson();
            String toUserId = JsonUtil.analyzeJsonField(message, "toUserId", String.class);
            //传送给对应toUserId用户的websocket
            if (StringUtils.hasText(toUserId) && SESSION_MAP.containsKey(redisKey)) {
                sendMessage(toUserId, returnJson);
            } else {
                log.error("请求的userId:" + toUserId + "不在该服务器上");
                //否则不在这个服务器上，发送到mysql或者redis
            }
        }
    }

    @Override
    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        String redisKey = WEB_SOCKET_USER_SERVICE + userId;
        if (SESSION_MAP.containsKey(redisKey)) {
            SESSION_MAP.remove(redisKey);
        }
        log.info("用户退出:" + userId + ",当前在线人数为:" + getOnlineCount());
    }

    @Override
    @OnError
    public void onError(Session session, @PathParam("userId") String userId, Throwable error) {
        log.error("用户错误:" + userId + ",原因:" + error.getMessage());
        throw new GlobalServiceException(error.getMessage());
    }

    /**
     * 实现服务器主动推送
     */
    public static void sendMessage(String userId, String message) {
        try {
            String redisKey = WEB_SOCKET_USER_SERVICE + userId;
            SESSION_MAP.get(redisKey).getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("{},网络异常!!!!!!", userId);
            throw new GlobalServiceException(e.getMessage());
        }
    }

    /**
     * 实现服务器主动推送
     */
    public static void sendAllMessage(String message) {
        try {
            Set<String> keys = SESSION_MAP.keysPrefix(WEB_SOCKET_USER_SERVICE);
            for(String key : keys) {
                SESSION_MAP.get(key).getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    // ============================================ 数据记录 ============================================

    private static synchronized long getOnlineCount() {
        return SESSION_MAP.size(WEB_SOCKET_USER_SERVICE);
    }

    // ============================================ 解析请求 ============================================

    // （原本我们习惯的请求 Body 通过 onMessage 来传输）
    public Map<String, String> getPathParameter() {
        return this.session.getPathParameters();
    }

    public Map<String, List<String>> getHeaders() {
        return this.handshakeRequest.getHeaders();
    }

    public String getQueryString() {
        return this.handshakeRequest.getQueryString();
    }

    public Map<String, List<String>> getParameterMap() {
        return this.handshakeRequest.getParameterMap();
    }

    public URI getRequestURI() {
        return this.handshakeRequest.getRequestURI();
    }

}