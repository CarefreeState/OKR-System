package com.macaku.center.sse.server;

import com.macaku.common.util.convert.JsonUtil;
import com.macaku.qrcode.config.QRCodeConfig;
import com.macaku.qrcode.service.OkrQRCodeService;
import com.macaku.user.sse.util.SseSessionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Api(tags = "SSE 接口")
@RequestMapping("/events")
@RequiredArgsConstructor
public class SseUserServer {

    public final static String SSE_USER_SERVER = "SseUserServer:";

    private final static long timeout = QRCodeConfig.WX_LOGIN_QR_CODE_UNIT.toMillis(QRCodeConfig.WX_LOGIN_QR_CODE_TTL);

    private final OkrQRCodeService okrQRCodeService;

    @ApiOperation("网页端微信登录")
    @GetMapping("/web/wxlogin")
    public SseEmitter connect() {
        // 获得邀请码的密钥
        String secret = okrQRCodeService.getSecretCode();
        // 连接并发送一条信息
        return SseSessionUtil.createConnect(timeout, SSE_USER_SERVER + secret,
                () -> JsonUtil.analyzeData(okrQRCodeService.getLoginQRCode(secret)));
    }

}
