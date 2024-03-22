package com.macaku.center.service.impl;

import com.macaku.center.component.InviteQRCodeServiceSelector;
import com.macaku.center.domain.vo.LoginQRCodeVO;
import com.macaku.center.redis.config.BloomFilterConfig;
import com.macaku.center.service.InviteQRCodeService;
import com.macaku.center.service.OkrQRCodeService;
import com.macaku.center.util.TeamOkrUtil;
import com.macaku.common.redis.RedisCache;
import com.macaku.common.util.ShortCodeUtil;
import com.macaku.common.util.media.ImageUtil;
import com.macaku.common.util.media.MediaUtil;
import com.macaku.common.util.media.config.StaticMapperConfig;
import com.macaku.user.qrcode.config.QRCodeConfig;
import com.macaku.user.service.WxBindingQRCodeService;
import com.macaku.user.service.WxLoginQRCodeService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.*;
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
@ConfigurationProperties(prefix = "font.text")
public class OkrQRCodeServiceImpl implements OkrQRCodeService {

    private final static String BINDING_CODE_MESSAGE = java.lang.String.format("请在 %d %s 内前往微信扫码进行绑定！",
            QRCodeConfig.WX_CHECK_QR_CODE_TTL, QRCodeConfig.WX_CHECK_QR_CODE_UNIT);

    private final static String LOGIN_CODE_MESSAGE = java.lang.String.format("请在 %d %s 内前往微信扫码进行验证！",
            QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);

    private Map<String, Integer> color;

    private String invite;

    private String binding;

    private String login;

    private final RedisCache redisCache;

    private final InviteQRCodeServiceSelector inviteQRCodeServiceSelector;

    private final WxBindingQRCodeService wxBindingQRCodeService;

    private final WxLoginQRCodeService wxLoginQRCodeService;

    private Color textColor;

    @Override
    public String getInviteQRCode(Long teamId, String type) {
        InviteQRCodeService inviteQRCodeService = inviteQRCodeServiceSelector.select(type);
        String redisKey = QRCodeConfig.TEAM_QR_CODE_MAP + teamId;
        return (String)redisCache.getCacheObject(redisKey).orElseGet(() -> {
            // 获取 QRCode
            String mapPath = inviteQRCodeService.getQRCode(teamId);
            // 获取到团队名字
            String teamName = TeamOkrUtil.getTeamName(teamId);
            String savePath = StaticMapperConfig.ROOT + mapPath;
            ImageUtil.mergeSignatureWrite(savePath, teamName,
                    invite, textColor, inviteQRCodeService.getQRCodeColor());
            // todo： 缓存小程序码
            redisCache.setCacheObject(redisKey, mapPath, QRCodeConfig.TEAM_QR_MAP_TTL, QRCodeConfig.TEAM_QR_MAP_UNIT);
            return mapPath;
        });
    }

    @Override
    public String getBindingQRCode(Long userId, String randomCode) {
        String redisKey = QRCodeConfig.WX_CHECK_QR_CODE_MAP + userId;
        String mapPath = wxBindingQRCodeService.getQRCode(userId, randomCode);
        redisCache.setCacheObject(redisKey, randomCode,
                QRCodeConfig.WX_CHECK_QR_CODE_TTL, QRCodeConfig.WX_CHECK_QR_CODE_UNIT);
        // 为图片记录缓存时间，时间一到，在服务器存储的文件应该删除掉！
        String savePath = MediaUtil.getLocalFilePath(mapPath);
        ImageUtil.mergeSignatureWrite(savePath, BINDING_CODE_MESSAGE,
                binding, textColor, wxBindingQRCodeService.getQRCodeColor());
        redisCache.setCacheObject(QRCodeConfig.WX_CHECK_QR_CODE_CACHE + MediaUtil.getLocalFileName(mapPath), 0,
                QRCodeConfig.WX_CHECK_QR_CODE_TTL, QRCodeConfig.WX_CHECK_QR_CODE_UNIT);
        return mapPath;
    }

    @Override
    public LoginQRCodeVO getLoginQRCode() {
        String secret;
        String bloomFilterName = BloomFilterConfig.BLOOM_FILTER_NAME;
        do {
            secret = ShortCodeUtil.getShortCode(ShortCodeUtil.getSalt());
        } while (redisCache.containsInBloomFilter(bloomFilterName, secret));
        redisCache.addToBloomFilter(bloomFilterName, secret);
        String redisKey = QRCodeConfig.WX_LOGIN_QR_CODE_MAP + secret;
        // 设置 为 false
        redisCache.setCacheObject(redisKey, Boolean.FALSE,
                QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);
        // 获取一个小程序码
        String mapPath = wxLoginQRCodeService.getQRCode(secret);
        String savePath = MediaUtil.getLocalFilePath(mapPath);
        ImageUtil.mergeSignatureWrite(savePath, LOGIN_CODE_MESSAGE,
                login, textColor, wxLoginQRCodeService.getQRCodeColor());
        redisCache.setCacheObject(QRCodeConfig.WX_LOGIN_QR_CODE_CACHE + MediaUtil.getLocalFileName(mapPath), 0,
                QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);
        return LoginQRCodeVO.builder()
                .path(mapPath)
                .secret(secret)
                .build();
    }

    @PostConstruct
    public void doPostConstruct() {
        textColor = ImageUtil.getColorByMap(color);
    }

}
