package com.macaku.center.service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-10
 * Time: 21:05
 */
public interface WxQRCodeService {

    byte[] doPostGetQRCodeData(String json);

    String getInviteQRCode(Long teamId);

    String getBindingQRCode(Long userId, String randomCode);

}
