package com.macaku.center.domain.dto.qrcode;

import com.macaku.common.util.JsonUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 19:07
 */
@Component
@Data // json 转化需要 getter 和 setter！
@ConfigurationProperties(prefix = "wx.invite")
public class QRCode {

    private String sceneKey;

    private String page;

    private Boolean checkPath;

    private String envVersion;

    private Integer width;

    private Boolean autoColor;

    private Map<String, Integer> lineColor;

    private Boolean isHyaline;

    public String toJson(Long teamId) {
        Map<String, Object> params = new HashMap<>();
        params.put("scene", String.format("%s=%d", sceneKey, teamId));
        params.put("page", StringUtils.hasText(page) ? page : null);
        params.put("check_path", checkPath);
        params.put("env_version", envVersion);
        params.put("width", width);
        params.put("auto_color", autoColor);
        params.put("line_color", lineColor);
        params.put("is_hyaline", isHyaline);
        return JsonUtil.analyzeData(params);
    }

}
