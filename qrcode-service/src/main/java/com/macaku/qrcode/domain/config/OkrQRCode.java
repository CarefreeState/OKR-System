package com.macaku.qrcode.domain.config;

import com.macaku.common.util.media.ImageUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-31
 * Time: 1:26
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "font.text")
public class OkrQRCode {

    private Map<String, Integer> color;

    private String invite;

    private String binding;

    private String login;

    private Color textColor;

    @PostConstruct
    public void doPostConstruct() {
        textColor = ImageUtil.getColorByMap(color);
    }

}
