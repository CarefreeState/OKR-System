package com.macaku.center.websocket.server;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.util.convert.JsonUtil;
import com.macaku.common.util.thread.pool.SchedulerThreadPool;
import com.macaku.qrcode.config.QRCodeConfig;
import com.macaku.qrcode.domain.vo.LoginQRCodeVO;
import com.macaku.qrcode.service.OkrQRCodeService;
import com.macaku.user.websocket.util.MessageSender;
import com.macaku.user.websocket.session.WsSessionMapper;
import com.macaku.user.websocket.util.WsSessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/web/wxlogin")
@Slf4j
@Component
public class WebWxLoginServer {

    private String secret;

    public final static String WEB_SOCKET_USER_SERVER = "WebWxLoginServer:";

    private final static OkrQRCodeService OKR_QR_CODE_SERVICE = SpringUtil.getBean(OkrQRCodeService.class);


    @OnOpen
    public void onOpen(Session session) throws DeploymentException {
        SchedulerThreadPool.schedule(() -> {
            WsSessionUtil.close(session);
        }, QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);
        // 获得邀请码
        LoginQRCodeVO loginQRCode = OKR_QR_CODE_SERVICE.getLoginQRCode();
        // 获得在 Redis 的键
        secret = loginQRCode.getSecret();
        String sessionKey = WEB_SOCKET_USER_SERVER + secret;
        if (WsSessionMapper.containsKey(sessionKey)) {
            WsSessionMapper.remove(sessionKey);
        }
        WsSessionMapper.put(sessionKey, session);
        // 发送：path, secret
        MessageSender.sendMessage(session, JsonUtil.analyzeData(loginQRCode));
//        SessionUtil.refuse("拒绝连接");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到消息： {}", message);
    }

    // 成功或者失败的断开都会调用这个方法
    @OnClose
    public void onClose(Session session) {
        String sessionKey = WEB_SOCKET_USER_SERVER + secret;
        log.warn("{} 断开连接", sessionKey);
        WsSessionMapper.remove(sessionKey);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        // 抛异常会在这里被捕获，或者再次抛出，都不会是全局处理器处理
        log.warn("{} 连接出现错误 {}", WEB_SOCKET_USER_SERVER + secret, error.getMessage());
    }

}