package com.macaku.user.websocket.util;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 9:53
 */
@Slf4j
public class MessageSender {

    public static void sendMessage(Session session, String message) {
        if(Objects.isNull(session)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_ONLINE);
        }
        try {
            synchronized (session) {
                if(session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                }
            }
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    /**
     * 实现服务器主动推送
     */
    public static void sendMessageToOne(String sessionKey, String message) {
        log.info("服务器 -> [{}] text: {}", sessionKey, message);
        SessionMapper.consumeKey(sessionKey, session -> {
            sendMessage(session, message);
        });
    }

    /**
     * 实现服务器主动推送（群发）(希望消息之间抛异常不影响)
     */
    public static void sendMessageToAll(String prefix, String message) {
        log.info("服务器 -> [{}*] text: {}", prefix, message);
        SessionMapper.consumePrefix(prefix, session -> {
            sendMessage(session, message);
        });
    }

}
