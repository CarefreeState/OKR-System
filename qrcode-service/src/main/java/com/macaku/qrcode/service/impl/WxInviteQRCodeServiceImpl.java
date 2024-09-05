package com.macaku.qrcode.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.config.StaticMapperConfig;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.convert.JsonUtil;
import com.macaku.common.util.convert.ShortCodeUtil;
import com.macaku.common.util.media.MediaUtil;
import com.macaku.qrcode.domain.config.WxInviteQRCode;
import com.macaku.qrcode.service.InviteQRCodeService;
import com.macaku.qrcode.util.QRCodeUtil;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.Map;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-29
 * Time: 0:42
 */
@Slf4j
public class WxInviteQRCodeServiceImpl implements InviteQRCodeService {

    private final WxInviteQRCode wxInviteQRCode = SpringUtil.getBean(WxInviteQRCode.class);


    @Override
    public Color getQRCodeColor() {
        return wxInviteQRCode.getQrCodeColor();
    }

    @Override
    public void checkParams(Long teamId, String secret) {
        if(Objects.isNull(teamId)) {
            throw new GlobalServiceException("团队 OKR ID 为 null", GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        String sceneKey = wxInviteQRCode.getSceneKey();
        String raw = sceneKey + "=" + teamId;
        String inviteSecret = ShortCodeUtil.getShortCode(raw);
        boolean isInvited = inviteSecret.equals(secret);
        log.info("用户想要加入团队 {}, 校验：{} -> {} 与 {} 比较 -> {}", teamId, raw, inviteSecret, secret, isInvited);
        if(Boolean.FALSE.equals(isInvited)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_CANNOT_JOIN_TEAM);
        }
    }

    @Override
    public String getQRCode(Long teamId) {
        Map<String, Object> params = wxInviteQRCode.getQRCodeParams();
        StringBuilder sceneBuilder = new StringBuilder();
        // 记录一下 teamId 与 inviteSecret 关系，携带这个密钥才行
        String sceneKey = wxInviteQRCode.getSceneKey();
        String secret = wxInviteQRCode.getSecret();
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
        String json = JsonUtil.analyzeData(params);
        return MediaUtil.saveImage(QRCodeUtil.doPostGetQRCodeData(json), StaticMapperConfig.INVITE_PATH);
    }

}
