package com.macaku.user.sse.util;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.user.sse.session.SseSessionMapper;
import com.macaku.user.websocket.session.WsSessionMapper;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 9:53
 */
@Slf4j
public class SseMessageSender {

    private final static MediaType MEDIA_TYPE = MediaType.APPLICATION_JSON;

    private static Consumer<IOException> handleException(String sessionKey) {
        return e -> {
            log.error("{} 发送消息异常 {}", sessionKey, e.getMessage());
            WsSessionMapper.remove(sessionKey);
        };
    }

    private static void sendMessage(SseEmitter sseEmitter, String message, Consumer<IOException> handleException) {
        if(Objects.isNull(sseEmitter)) {
            log.warn(GlobalServiceStatusCode.SSE_CONNECTION_NOT_EXIST.toString());
            return;
        }
        try {
            sseEmitter.send(message, MEDIA_TYPE);
        } catch (IOException e) {
            handleException.accept(e);
        }
    }

    public static void sendMessage(String sessionKey, String message) {
        log.info("服务器 -> [{}] text: {}", sessionKey, message);
        SseSessionMapper.consumeKey(sessionKey, sseEmitter -> {
            sendMessage(sseEmitter, message, handleException(sessionKey));
        });
    }

    public static void sendAllMessage(String prefix, Function<String, String> function) {
        SseSessionUtil.getSessionKeys(prefix).stream().parallel().forEach(sessionKey -> {
            sendMessage(sessionKey, function.apply(sessionKey));
        });
    }

    public static void sendAllMessage(List<String> sessionKeys, Function<String, String> function) {
        if (Collections.isEmpty(sessionKeys)) {
            return;
        }
        sessionKeys.stream().parallel().distinct().forEach(sessionKey -> {
            sendMessage(sessionKey, function.apply(sessionKey));
        });
    }

}
