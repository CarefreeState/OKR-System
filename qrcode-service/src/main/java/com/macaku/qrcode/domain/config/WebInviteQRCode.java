package com.macaku.qrcode.domain.config;

import com.macaku.common.util.media.ImageUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-31
 * Time: 1:25
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "web.invite")
public class WebInviteQRCode implements QRCode{

    private String sceneKey;

    private String secret;

    private String page;

    private Integer width;

    private Map<String, Integer> lineColor;

    private Color qrCodeColor;

    @PostConstruct
    public void doPostConstruct() {
        qrCodeColor = ImageUtil.getColorByMap(lineColor);
    }

    @Override
    public Map<String, Object> getQRCodeParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("page", StringUtils.hasText(page) ? page : null);
        params.put("width", width);
        return params;
    }

}
