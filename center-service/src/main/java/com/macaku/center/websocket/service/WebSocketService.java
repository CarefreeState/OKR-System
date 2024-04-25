package com.macaku.center.websocket.service;

import javax.websocket.DeploymentException;
import javax.websocket.Session;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-25
 * Time: 20:43
 */
public interface WebSocketService {

    void onOpen(Session session, String key) throws DeploymentException;

    void onMessage(String message, Session session);

    void onClose();

    void onError(Session session, Throwable error);

    void sendMessage(String message);

}
