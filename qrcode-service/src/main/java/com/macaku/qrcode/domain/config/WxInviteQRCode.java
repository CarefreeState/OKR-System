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
@ConfigurationProperties(prefix = "wx.invite")
public class WxInviteQRCode implements QRCode {

    private String sceneKey;

    private String secret;

    private String page;

    private Boolean checkPath;

    private String envVersion;

    private Integer width;

    private Boolean autoColor;

    private Map<String, Integer> lineColor;

    private Boolean isHyaline;

    private Color qrCodeColor;

    @PostConstruct
    public void doPostConstruct() {
        qrCodeColor = ImageUtil.getColorByMap(lineColor);
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

}
