package com.macaku.user.service;

import java.awt.*;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-20
 * Time: 22:34
 */
public interface WxLoginQRCodeService {

    Color getQRCodeColor();

    Map<String, Object> getQRCodeParams();

    String getQRCode(String secret);

}
