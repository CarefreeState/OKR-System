package com.macaku.qrcode.token;


import com.macaku.common.util.convert.JsonUtil;
import com.macaku.common.web.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TokenUtil {

    public static String APP_ID;

    public static String APP_SECRET;

    @Value("${wx.appid}")
    private void setAPP_ID(String appId) {
        APP_ID = appId;
    }

    @Value("${wx.secret}")
    private void setAPP_SECRET(String appSecret) {
        APP_SECRET = appSecret;
    }


    public static Map<String, Object> getAccessTokenMap() {
        // 获取token的url
        final String URL = "https://api.weixin.qq.com/cgi-bin/token";
        // 构造参数表
        Map<String, Object> param = new HashMap<String, Object>(){{
            this.put("grant_type", "client_credential");
            this.put("appid", APP_ID);
            this.put("secret", APP_SECRET);
        }};
        // 发起get请求
        String response = HttpUtil.doGet(URL, param);
        // 解析json
        Map<String, Object> result = JsonUtil.analyzeJson(response, Map.class);
        return result;
    }

    public static String getToken() {
        return AccessToken.getAccessToken().getToken();
    }
}
