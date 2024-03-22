package com.macaku.center.service;

import java.awt.*;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-22
 * Time: 18:36
 */
public interface InviteQRCodeService {

    boolean match(String type);

    Color getQRCodeColor();

    Map<String, Object> getQRCodeParams();

    String getQRCode(Long teamId);

    void checkParams(Long teamId, String secret);
}
