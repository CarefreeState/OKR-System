package com.macaku.user.token;

import lombok.Getter;

import java.util.Map;


@Getter
public class AccessToken {


    private String token;

    private long expireIn;//有效期限

    volatile private static AccessToken accessToken = null;

    private void setExpireIn(long expireIn) {
        // 设置有效期限的时候的时间戳
        this.expireIn = System.currentTimeMillis() + expireIn * 1000;
    }
    private void setToken(String token) {
        this.token = token;
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
