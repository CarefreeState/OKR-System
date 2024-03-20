package com.macaku.center.service.impl;

import com.macaku.center.redis.config.BloomFilterConfig;
import com.macaku.center.service.WxInviteQRCodeService;
import com.macaku.user.service.WxLoginQRCodeService;
import com.macaku.center.service.WxQRCodeService;
import com.macaku.center.util.TeamOkrUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import com.macaku.common.util.ShortCodeUtil;
import com.macaku.common.util.media.ImageUtil;
import com.macaku.common.util.media.MediaUtil;
import com.macaku.common.util.media.config.StaticMapperConfig;
import com.macaku.common.web.HttpUtil;
import com.macaku.user.qrcode.config.QRCodeConfig;
import com.macaku.user.service.WxBindingQRCodeService;
import com.macaku.user.token.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-10
 * Time: 21:06
 */
@Service
@Slf4j
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "font.color")
public class WxQRCodeServiceImpl implements WxQRCodeService, BeanNameAware {

    private Color textColor;

    private final static String INVITE_FLAG = "[invite]";

    private final static String BINDING_FLAG = "[binding]";

    private final static String LOGIN_FLAG = "[login]";

    private final static String BINDING_CODE_MESSAGE = String .format("请在 %d %s 内前往微信扫码进行绑定！",
            QRCodeConfig.WX_CHECK_QR_CODE_TTL, QRCodeConfig.WX_CHECK_QR_CODE_UNIT);

    private final static String LOGIN_CODE_MESSAGE = String .format("请在 %d %s 内前往微信扫码进行验证！",
            QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);

    private Map<String, Integer> text;

    private final RedisCache redisCache;

    private final WxBindingQRCodeService wxBindingQRCodeService;

    private final WxInviteQRCodeService wxInviteQRCodeService;

    private final WxLoginQRCodeService wxLoginQRCodeService;

    @Override
    public byte[] doPostGetQRCodeData(String json) {
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
        return data;
    }

    @Override
    public String getInviteQRCode(Long teamId) {
        String redisKey = QRCodeConfig.TEAM_QR_CODE_MAP + teamId;
        return (String)redisCache.getCacheObject(redisKey).orElseGet(() -> {
            // 获取 QRCode
            String json = wxInviteQRCodeService.getQRCodeJson(teamId);
            String mapPath = MediaUtil.saveImage(doPostGetQRCodeData(json), StaticMapperConfig.INVITE_PATH);
            // 获取到团队名字
            String teamName = TeamOkrUtil.getTeamName(teamId);
            String savePath = StaticMapperConfig.ROOT + mapPath;
            ImageUtil.mergeSignatureWrite(savePath, teamName,
                    INVITE_FLAG, this.textColor, wxInviteQRCodeService.getQRCodeColor());
            // todo： 缓存小程序码
            redisCache.setCacheObject(redisKey, mapPath, QRCodeConfig.TEAM_QR_MAP_TTL, QRCodeConfig.TEAM_QR_MAP_UNIT);
            return mapPath;
        });
    }

    @Override
    public String getBindingQRCode(Long userId, String randomCode) {
        String redisKey = QRCodeConfig.WX_CHECK_QR_CODE_MAP + userId;
        String json = wxBindingQRCodeService.getQRCodeJson(userId, randomCode);
        String mapPath = MediaUtil.saveImage(doPostGetQRCodeData(json), StaticMapperConfig.BINDING_PATH);
        redisCache.setCacheObject(redisKey, randomCode,
                QRCodeConfig.WX_CHECK_QR_CODE_TTL, QRCodeConfig.WX_CHECK_QR_CODE_UNIT);
        // 为图片记录缓存时间，时间一到，在服务器存储的文件应该删除掉！
        String savePath = MediaUtil.getLocalFilePath(mapPath);
        ImageUtil.mergeSignatureWrite(savePath, BINDING_CODE_MESSAGE,
                BINDING_FLAG, this.textColor, wxBindingQRCodeService.getQRCodeColor());
        redisCache.setCacheObject(QRCodeConfig.WX_CHECK_QR_CODE_CACHE + mapPath.substring(mapPath.lastIndexOf("/") + 1), 0,
                QRCodeConfig.WX_CHECK_QR_CODE_TTL, QRCodeConfig.WX_CHECK_QR_CODE_UNIT);
        return mapPath;
    }

    @Override
    public String getLoginQRCode() {
        String secret;
        String bloomFilterName = BloomFilterConfig.BLOOM_FILTER_NAME;
        do {
            secret = ShortCodeUtil.getShortCode(ShortCodeUtil.getSalt());
        } while (redisCache.containsInBloomFilter(bloomFilterName, secret));
        redisCache.addToBloomFilter(bloomFilterName, secret);
        String redisKey = QRCodeConfig.WX_LOGIN_QR_CODE_MAP + secret;
        // 设置 为 false
        redisCache.setCacheObject(redisKey, Boolean.FALSE);
        // 获取一个小程序码
        String json = wxLoginQRCodeService.getQRCodeJson(secret);
        String mapPath = MediaUtil.saveImage(doPostGetQRCodeData(json), StaticMapperConfig.LOGIN_PATH);
        String savePath = MediaUtil.getLocalFilePath(mapPath);
        ImageUtil.mergeSignatureWrite(savePath, LOGIN_CODE_MESSAGE,
                LOGIN_FLAG, this.textColor, wxBindingQRCodeService.getQRCodeColor());
        redisCache.setCacheObject(QRCodeConfig.WX_LOGIN_QR_CODE_CACHE + mapPath.substring(mapPath.lastIndexOf("/") + 1), 0,
                QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);
        return mapPath;
    }

    @PostConstruct
    public void doPostConstruct() {
        textColor = ImageUtil.getColorByMap(text);
    }

    @Override
    public void setBeanName(String s) {

    }
}
