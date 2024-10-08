package com.macaku.qrcode.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.convert.JsonUtil;
import com.macaku.common.util.media.MediaUtil;
import com.macaku.common.config.StaticMapperConfig;
import com.macaku.qrcode.config.QRCodeConfig;
import com.macaku.qrcode.domain.config.WxBindingQRCode;
import com.macaku.qrcode.service.WxBindingQRCodeService;
import com.macaku.qrcode.util.QRCodeUtil;
import com.macaku.redis.repository.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-10
 * Time: 19:36
 */
@Service
@Slf4j
public class WxBindingQRCodeServiceImpl implements WxBindingQRCodeService {

    private final WxBindingQRCode wxBindingQRCode = SpringUtil.getBean(WxBindingQRCode.class);

    private final RedisCache redisCache = SpringUtil.getBean(RedisCache.class);

    @Override
    public Color getQRCodeColor() {
        return wxBindingQRCode.getQrCodeColor();
    }

    @Override
    public String getQRCode(Long userId, String randomCode) {
        Map<String, Object> params = wxBindingQRCode.getQRCodeParams();
        String userKey = wxBindingQRCode.getUserKey();
        String secret = wxBindingQRCode.getSecret();
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

}
