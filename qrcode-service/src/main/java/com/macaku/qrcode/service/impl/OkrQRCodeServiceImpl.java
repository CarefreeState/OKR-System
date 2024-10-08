package com.macaku.qrcode.service.impl;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.convert.ShortCodeUtil;
import com.macaku.common.util.media.ImageUtil;
import com.macaku.common.util.media.MediaUtil;
import com.macaku.qrcode.component.InviteQRCodeServiceFactory;
import com.macaku.qrcode.config.BloomFilterConfig;
import com.macaku.qrcode.config.QRCodeConfig;
import com.macaku.qrcode.domain.config.OkrQRCode;
import com.macaku.qrcode.domain.vo.LoginQRCodeVO;
import com.macaku.qrcode.service.*;
import com.macaku.redis.repository.RedisCache;
import com.macaku.redis.repository.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
public class OkrQRCodeServiceImpl implements OkrQRCodeService {

    private final static String BINDING_CODE_MESSAGE = java.lang.String.format("请在 %d %s 内前往微信扫码进行绑定！",
            QRCodeConfig.WX_CHECK_QR_CODE_TTL, QRCodeConfig.WX_CHECK_QR_CODE_UNIT);

    private final static String LOGIN_CODE_MESSAGE = java.lang.String.format("请在 %d %s 内前往微信扫码进行验证！",
            QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);

    private final static String COMMON_CODE_MESSAGE = "OKR 目标与规划管理";

    private final OkrQRCode okrQRCode;

    private final RedisCache redisCache;

    private final RedisLock redisLock;

    private final InviteQRCodeServiceFactory inviteQRCodeServiceFactory;

    private final WxBindingQRCodeService wxBindingQRCodeService;

    private final WxLoginQRCodeService wxLoginQRCodeService;

    private final WxCommonQRCodeService wxCommonQRCodeService;

    public String getInviteQRCode(Long teamId, String teamName, String type) {
        InviteQRCodeService inviteQRCodeService = inviteQRCodeServiceFactory.getService(type);
        String redisKey = String.format(QRCodeConfig.TEAM_QR_CODE_MAP, type, teamId);
        return (String)redisCache.getCacheObject(redisKey).orElseGet(() -> {
            // 获取 QRCode
            String mapPath = inviteQRCodeService.getQRCode(teamId);
            // 获取到团队名字
            String savePath = MediaUtil.getLocalFilePath(mapPath);
            ImageUtil.mergeSignatureWrite(savePath, teamName,
                    okrQRCode.getInvite(), okrQRCode.getTextColor(), inviteQRCodeService.getQRCodeColor());
            // todo： 缓存小程序码
            redisCache.setCacheObject(redisKey, mapPath, QRCodeConfig.TEAM_QR_MAP_TTL, QRCodeConfig.TEAM_QR_MAP_UNIT);
            return mapPath;
        });
    }

    @Override
    public String getInviteQRCodeLock(Long teamId, String teamName, String type) {
        String lockKey = QRCodeConfig.OKR_INVITE_QR_CODE_LOCK + teamId;
        return redisLock.tryLockGetSomething(lockKey, () -> getInviteQRCode(teamId, teamName, type), () -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.REDIS_LOCK_FAIL);
        });
    }

    @Override
    public void deleteTeamNameCache(Long teamId) {
        String redisKey1 = String.format(QRCodeConfig.TEAM_QR_CODE_MAP, InviteQRCodeServiceFactory.WEB_TYPE, teamId);
        String redisKey2 = String.format(QRCodeConfig.TEAM_QR_CODE_MAP, InviteQRCodeServiceFactory.WX_TYPE, teamId);
        redisCache.getCacheObject(redisKey1).ifPresent(mapPath -> {
            redisCache.deleteObject(redisKey1);
            String originPath = MediaUtil.getLocalFilePath((String) mapPath);
            MediaUtil.deleteFile(originPath);
        });
        redisCache.getCacheObject(redisKey2).ifPresent(mapPath -> {
            redisCache.deleteObject(redisKey2);
            String originPath = MediaUtil.getLocalFilePath((String) mapPath);
            MediaUtil.deleteFile(originPath);
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
                okrQRCode.getBinding(), okrQRCode.getTextColor(), wxBindingQRCodeService.getQRCodeColor());
        redisCache.setCacheObject(QRCodeConfig.WX_CHECK_QR_CODE_CACHE + MediaUtil.getLocalFileName(mapPath), 0,
                QRCodeConfig.WX_CHECK_QR_CODE_TTL, QRCodeConfig.WX_CHECK_QR_CODE_UNIT);
        return mapPath;
    }

    @Override
    public String getSecretCode() {
        String secret;
        String bloomFilterName = BloomFilterConfig.BLOOM_FILTER_NAME;
        do {
            secret = ShortCodeUtil.getShortCode(ShortCodeUtil.getSalt());
        } while (redisCache.containsInBloomFilter(bloomFilterName, secret));
        redisCache.addToBloomFilter(bloomFilterName, secret);
        return secret;
    }

    @Override
    public LoginQRCodeVO getLoginQRCode() {
        return getLoginQRCode(getSecretCode());
    }

    @Override
    public LoginQRCodeVO getLoginQRCode(String secret) {
        // 设置 为 false
        redisCache.setCacheObject(QRCodeConfig.WX_LOGIN_QR_CODE_MAP + secret, Boolean.FALSE,
                QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);
        // 获取一个小程序码
        String mapPath = wxLoginQRCodeService.getQRCode(secret);
        String savePath = MediaUtil.getLocalFilePath(mapPath);
        ImageUtil.mergeSignatureWrite(savePath, LOGIN_CODE_MESSAGE,
                okrQRCode.getLogin(), okrQRCode.getTextColor(), wxLoginQRCodeService.getQRCodeColor());
        redisCache.setCacheObject(QRCodeConfig.WX_LOGIN_QR_CODE_CACHE + MediaUtil.getLocalFileName(mapPath), 0,
                QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);
        return LoginQRCodeVO.builder()
                .path(mapPath)
                .secret(secret)
                .build();
    }

    @Override
    public String getCommonQRCode() {
        String redisKey = QRCodeConfig.WX_COMMON_QR_CODE_KEY;
        return (String) redisLock.tryLockGetSomething(QRCodeConfig.OKR_COMMON_QR_CODE_LOCK, () ->
            redisCache.getCacheObject(redisKey).orElseGet(() -> {
                // 获取 QRCode
                String mapPath = wxCommonQRCodeService.getQRCode();
                String savePath = MediaUtil.getLocalFilePath(mapPath);
                ImageUtil.mergeSignatureWrite(savePath, COMMON_CODE_MESSAGE,
                        okrQRCode.getCommon(), okrQRCode.getTextColor(), wxCommonQRCodeService.getQRCodeColor());
                redisCache.setCacheObject(redisKey, mapPath, QRCodeConfig.WX_COMMON_QR_CODE_TTL, QRCodeConfig.WX_COMMON_QR_CODE_UNIT);
                return mapPath;
            }), () -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.REDIS_LOCK_FAIL);
        });
    }

}
