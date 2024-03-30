package com.macaku.qrcode.service;

import java.awt.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-20
 * Time: 22:34
 */
public interface WxLoginQRCodeService {

    Color getQRCodeColor();

    String getQRCode(String secret);

}
