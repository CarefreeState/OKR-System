package com.macaku.center.websocket.service;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.center.websocket.util.MessageSender;
import com.macaku.center.websocket.util.SessionMapper;
import com.macaku.center.websocket.util.SessionUtil;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.convert.JsonUtil;
import com.macaku.common.util.thread.pool.SchedulerThreadPool;
import com.macaku.qrcode.config.QRCodeConfig;
import com.macaku.qrcode.domain.vo.LoginQRCodeVO;
import com.macaku.qrcode.service.OkrQRCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/web/wxlogin")
@Slf4j
@Component
public class WebSocketUserService {

    private String secret;

    public final static String WEB_SOCKET_USER_SERVICE = "WebSocketUserService:";

    private final static OkrQRCodeService OKR_QR_CODE_SERVICE = SpringUtil.getBean(OkrQRCodeService.class);

    private long getOnlineCount() {
        return SessionMapper.size(WEB_SOCKET_USER_SERVICE);
    }

    @OnOpen
    public void onOpen(Session session) throws DeploymentException {
        // 获得邀请码
        LoginQRCodeVO loginQRCode = OKR_QR_CODE_SERVICE.getLoginQRCode();
        // 获得在 Redis 的键
        secret = loginQRCode.getSecret();
        String sessionKey = WEB_SOCKET_USER_SERVICE + secret;
        if (SessionMapper.containsKey(sessionKey)) {
            SessionMapper.remove(sessionKey);
        }
        SessionMapper.put(sessionKey, session);
        // 发送：path, secret
        MessageSender.sendMessage(session, JsonUtil.analyzeData(loginQRCode));
        SchedulerThreadPool.schedule(() -> {
            SessionUtil.close(session);
        }, QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);
//        throw new DeploymentException("拒绝连接");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到消息： {}", message);
    }

    @OnClose
    public void onClose(Session session) {
        String sessionKey = WEB_SOCKET_USER_SERVICE + secret;
        log.warn("{} 断开连接", sessionKey);
        SessionMapper.remove(sessionKey);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        throw new GlobalServiceException(error.getMessage());
    }

}