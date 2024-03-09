package com.macaku.center.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.center.service.WxQRCodeService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import com.macaku.common.util.JsonUtil;
import com.macaku.common.util.ShortCodeUtil;
import com.macaku.common.util.media.MediaUtil;
import com.macaku.common.web.HttpUtil;
import com.macaku.user.qrcode.config.QRCodeConfig;
import com.macaku.user.token.TokenUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-29
 * Time: 0:42
 */
@Service
@Setter
@Slf4j
@ConfigurationProperties(prefix = "wx.invite")
public class WxQRCodeServiceImpl implements WxQRCodeService {

    private String userKey;

    private String sceneKey;

    private String secret;

    private String page;

    private Boolean checkPath;

    private String envVersion;

    private Integer width;

    private Boolean autoColor;

    private Map<String, Integer> lineColor;

    private Boolean isHyaline;

    private final RedisCache redisCache = SpringUtil.getBean(RedisCache.class);

    @Override
    public void checkParams(Long teamId, String secret) {
        if(Objects.isNull(teamId)) {
            throw new GlobalServiceException("团队 OKR ID 为 null", GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        String raw = sceneKey + "=" + teamId;
        String inviteSecret = ShortCodeUtil.getShortCode(raw);
        boolean isInvited = inviteSecret.equals(secret);
        log.info("用户想要加入团队 {}, 校验：{} -> {} 与 {} 比较 -> {}", teamId, raw, inviteSecret, secret, isInvited);
        if(Boolean.FALSE.equals(isInvited)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_CANNOT_JOIN_TEAM);
        }
    }

    private Map<String, Object> getQRCodeParams() {
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
    public String getQRCodeJson(Long teamId) {
        Map<String, Object> params = getQRCodeParams();
        StringBuilder sceneBuilder = new StringBuilder();
        // 记录一下 teamId 与 inviteSecret 关系，携带这个密钥才行
        sceneBuilder
                .append(sceneKey)
                .append("=")
                .append(teamId);
        // 短码虽然无法保证绝对的唯一，但是 teamId 能确定短码即可
        String inviteSecret = ShortCodeUtil.getShortCode(sceneBuilder.toString());
        sceneBuilder
                .append("&")
                .append(secret)
                .append("=")
                .append(inviteSecret);
        params.put("scene", sceneBuilder.toString());
        return JsonUtil.analyzeData(params);
    }

    @Override
    public String getQRCodeJson(Long userId, String randomCode) {
        Map<String, Object> params = getQRCodeParams();
        String scene = String.format("%s=%d&%s=%s", userKey, userId, secret, randomCode);
        params.put("scene", scene);
        return JsonUtil.analyzeData(params);
    }

    @Override
    public String doPostGetQRCode(String json) {
        String accessToken = TokenUtil.getToken();
        String url = QRCodeConfig.WX_QR_CORE_URL + HttpUtil.getQueryString(new HashMap<String, Object>(){{
            this.put("access_token", accessToken);
        }});
        log.info("请求微信（json） -> {}", json);
        byte[] data = HttpUtil.doPostJsonBytes(url, json);
        if(!MediaUtil.isImage(data)) {
            throw new GlobalServiceException(new String(data), GlobalServiceStatusCode.QR_CODE_GENERATE_FAIL);
        }
        // 保存一下
        return MediaUtil.saveImage(data);
    }

    @Override
    public String getCheckQRCode(Long userId, String randomCode) {
        String redisKey = QRCodeConfig.WX_CHECK_QR_CODE_MAP + userId;
        String json = getQRCodeJson(userId, randomCode);
        String mapPath = doPostGetQRCode(json);
        redisCache.setCacheObject(redisKey, randomCode, QRCodeConfig.WX_CHECK_QR_CODE_TTL, QRCodeConfig.WX_CHECK_QR_CODE_UNIT);
        return mapPath;
    }

}
