package com.macaku.qrcode.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.util.convert.JsonUtil;
import com.macaku.common.util.media.MediaUtil;
import com.macaku.common.config.StaticMapperConfig;
import com.macaku.qrcode.domain.config.WxLoginQRCode;
import com.macaku.qrcode.service.WxLoginQRCodeService;
import com.macaku.qrcode.util.QRCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-20
 * Time: 22:34
 */
@Service
@Slf4j
public class WxLoginQRCodeServiceImpl implements WxLoginQRCodeService {

    private final WxLoginQRCode wxLoginQRCode = SpringUtil.getBean(WxLoginQRCode.class);

    @Override
    public Color getQRCodeColor() {
        return wxLoginQRCode.getQrCodeColor();
    }

    @Override
    public String getQRCode(String secret) {
        Map<String, Object> params = wxLoginQRCode.getQRCodeParams();
        String scene = String.format("%s=%s", wxLoginQRCode.getSecret(), secret);
        params.put("scene", scene);
        String json = JsonUtil.analyzeData(params);
        return MediaUtil.saveImage(QRCodeUtil.doPostGetQRCodeData(json), StaticMapperConfig.LOGIN_PATH);
    }

}
