package com.macaku.qrcode.token;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class AccessToken {

    private String token;

    private long expireIn;//有效期限

    volatile private static AccessToken accessToken = null;

    private void setExpireIn(int expireIn) {
        // 设置有效期限的时候的时间戳
        this.expireIn = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expireIn);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > this.getExpireIn();
    }

    private static void setAccessToken() {
        if(accessToken == null) {
            accessToken = new AccessToken();
        }
        Map<String, Object> map = TokenUtil.getAccessTokenMap();
        accessToken.setToken((String) map.get("access_token"));
        accessToken.setExpireIn((Integer) map.get("expires_in"));
    }

    public static AccessToken getAccessToken() {
        if(accessToken == null || accessToken.isExpired()) {
            synchronized (AccessToken.class) {
                if(accessToken == null || accessToken.isExpired()) {
                    setAccessToken();
                }
            }
        }
        return accessToken;
    }
}
