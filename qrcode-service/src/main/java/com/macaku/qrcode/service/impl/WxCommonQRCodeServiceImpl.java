package com.macaku.qrcode.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.util.convert.JsonUtil;
import com.macaku.common.util.media.MediaUtil;
import com.macaku.common.util.media.config.StaticMapperConfig;
import com.macaku.qrcode.domain.config.WxCommonQRCode;
import com.macaku.qrcode.service.WxCommonQRCodeService;
import com.macaku.qrcode.util.QRCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-04
 * Time: 1:05
 */
@Service
@Slf4j
public class WxCommonQRCodeServiceImpl implements WxCommonQRCodeService {

    private final static String SCENE = "you=nice";

    private final WxCommonQRCode wxCommonQRCode = SpringUtil.getBean(WxCommonQRCode.class);

    @Override
    public Color getQRCodeColor() {
        return wxCommonQRCode.getQrCodeColor();
    }

    @Override
    public String getQRCode() {
        Map<String, Object> params = wxCommonQRCode.getQRCodeParams();
        params.put("scene", SCENE);
        String json = JsonUtil.analyzeData(params);
        return MediaUtil.saveImage(QRCodeUtil.doPostGetQRCodeData(json), StaticMapperConfig.COMMON_PATH);
    }
}
