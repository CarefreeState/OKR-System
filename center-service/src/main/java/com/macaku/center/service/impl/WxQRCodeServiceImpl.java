package com.macaku.center.service.impl;

import com.macaku.center.service.WxInviteQRCodeService;
import com.macaku.center.service.WxQRCodeService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import com.macaku.common.util.media.MediaUtil;
import com.macaku.common.web.HttpUtil;
import com.macaku.user.qrcode.config.QRCodeConfig;
import com.macaku.user.service.WxBindingQRCodeService;
import com.macaku.user.token.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-10
 * Time: 21:06
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WxQRCodeServiceImpl implements WxQRCodeService {

    private final RedisCache redisCache;

    private final WxBindingQRCodeService wxBindingQRCodeService;

    private final WxInviteQRCodeService wxInviteQRCodeService;

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
    public String getInviteQRCode(Long teamId) {
        String redisKey = QRCodeConfig.TEAM_QR_CODE_MAP + teamId;
        return (String)redisCache.getCacheObject(redisKey).orElseGet(() -> {
            // 获取 QRCode
            String json = wxInviteQRCodeService.getQRCodeJson(teamId);
            String mapPath = doPostGetQRCode(json);
            redisCache.setCacheObject(redisKey, mapPath, QRCodeConfig.TEAM_QR_MAP_TTL, QRCodeConfig.TEAM_QR_MAP_UNIT);
            return mapPath;
        });
    }

    @Override
    public String getBindingQRCode(Long userId, String randomCode) {
        String redisKey = QRCodeConfig.WX_CHECK_QR_CODE_MAP + userId;
        String json = wxBindingQRCodeService.getQRCodeJson(userId, randomCode);
        String mapPath = doPostGetQRCode(json);
        redisCache.setCacheObject(redisKey, randomCode, QRCodeConfig.WX_CHECK_QR_CODE_TTL, QRCodeConfig.WX_CHECK_QR_CODE_UNIT);
        return mapPath;
    }


}
