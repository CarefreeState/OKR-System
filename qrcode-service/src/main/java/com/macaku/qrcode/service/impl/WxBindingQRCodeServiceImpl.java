package com.macaku.qrcode.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import com.macaku.common.util.JsonUtil;
import com.macaku.common.util.media.ImageUtil;
import com.macaku.common.util.media.MediaUtil;
import com.macaku.common.util.media.config.StaticMapperConfig;
import com.macaku.qrcode.service.WxBindingQRCodeService;
import com.macaku.qrcode.config.QRCodeConfig;
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
 * Date: 2024-03-10
 * Time: 19:36
 */
@Service
@Setter
@Slf4j
@ConfigurationProperties(prefix = "wx.binding")
public class WxBindingQRCodeServiceImpl implements WxBindingQRCodeService {

    private String userKey;

    private String secret;

    private String page;

    private Boolean checkPath;

    private String envVersion;

    private Integer width;

    private Boolean autoColor;

    private Map<String, Integer> lineColor;

    private Boolean isHyaline;

    private final RedisCache redisCache = SpringUtil.getBean(RedisCache.class);

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
    public String getQRCode(Long userId, String randomCode) {
        Map<String, Object> params = getQRCodeParams();
        String scene = String.format("%s=%d&%s=%s", userKey, userId, secret, randomCode);
        params.put("scene", scene);
        String json = JsonUtil.analyzeData(params);
        return MediaUtil.saveImage(QRCodeUtil.doPostGetQRCodeData(json), StaticMapperConfig.BINDING_PATH);
    }

    @Override
    public void checkParams(Long userId, String randomCode) {
        String redisKey = QRCodeConfig.WX_CHECK_QR_CODE_MAP + userId;
        String code = (String) redisCache.getCacheObject(redisKey).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.WX_NOT_EXIST_RECORD));
        redisCache.deleteObject(redisKey);
        if(!randomCode.equals(code)) {
            // 这个随机码肯定是伪造的，因为这个请求的参数不是用户手动输入的值
            throw new GlobalServiceException(GlobalServiceStatusCode.WX_CODE_NOT_CONSISTENT);
        }
    }

    @PostConstruct
    public void doPostConstruct() {
        qrCodeColor = ImageUtil.getColorByMap(lineColor);
    }

}
