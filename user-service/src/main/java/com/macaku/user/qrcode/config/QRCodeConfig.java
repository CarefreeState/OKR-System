package com.macaku.user.qrcode.config;

import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-09
 * Time: 17:49
 */
public class QRCodeConfig {
    public final static String WX_QR_CORE_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit";

    public final static String WX_CHECK_QR_CODE_MAP = "wxCheckQRCodeMap:";

    public final static Long WX_CHECK_QR_CODE_TTL = 5L;

    public final static TimeUnit WX_CHECK_QR_CODE_UNIT = TimeUnit.MINUTES;
}
