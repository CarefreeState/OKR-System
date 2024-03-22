package com.macaku.qrcode.service.impl;

import com.macaku.common.util.JsonUtil;
import com.macaku.common.util.media.ImageUtil;
import com.macaku.common.util.media.MediaUtil;
import com.macaku.common.util.media.config.StaticMapperConfig;
import com.macaku.qrcode.service.WxLoginQRCodeService;
import com.macaku.qrcode.util.QRCodeUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-20
 * Time: 22:34
 */
@Service
@Setter
@Slf4j
@ConfigurationProperties(prefix = "wx.login")
public class WxLoginQRCodeServiceImpl implements WxLoginQRCodeService {

    private String secret;

    private String page;

    private Boolean checkPath;

    private String envVersion;

    private Integer width;

    private Boolean autoColor;

    private Map<String, Integer> lineColor;

    private Boolean isHyaline;

    private Color qrCodeColor;

    @Override
    public Color getQRCodeColor() {
        return this.qrCodeColor;
    }

    @Override
    public Map<String, Object> getQRCodeParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("page", StringUtils.hasText(page) ? page : null);
        params.put("check_path", checkPath);
        params.put("env_version", envVersion);
        params.put("width", width);
        params.put("auto_color", autoColor);
        params.put("line_color", lineColor);
        params.put("is_hyaline", isHyaline);
        return params;
    }

    @Override
    public String getQRCode(String secret) {
        Map<String, Object> params = getQRCodeParams();
        String scene = String.format("%s=%s", this.secret, secret);
        params.put("scene", scene);
        String json = JsonUtil.analyzeData(params);
        return MediaUtil.saveImage(QRCodeUtil.doPostGetQRCodeData(json), StaticMapperConfig.LOGIN_PATH);
    }

    @PostConstruct
    public void doPostConstruct() {
        qrCodeColor = ImageUtil.getColorByMap(lineColor);
    }

}
