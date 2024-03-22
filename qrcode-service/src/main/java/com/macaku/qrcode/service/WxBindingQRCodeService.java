package com.macaku.qrcode.service;

import java.awt.*;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-10
 * Time: 19:36
 */
public interface WxBindingQRCodeService {

    Color getQRCodeColor();

    Map<String, Object> getQRCodeParams();

    String getQRCode(Long userId, String randomCode);

    void checkParams(Long userId, String randomCode);

}
