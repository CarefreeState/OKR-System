package com.macaku.user.util;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.media.MediaUtil;
import com.macaku.common.web.HttpUtil;
import com.macaku.user.qrcode.config.QRCodeConfig;
import com.macaku.user.token.TokenUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-22
 * Time: 21:23
 */
@Slf4j
public class QRCodeUtil {

    public static byte[] doPostGetQRCodeData(String json) {
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

}
