package com.macaku.user.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import com.macaku.common.util.EncryptUtil;
import com.macaku.common.util.JsonUtil;
import com.macaku.common.util.JwtUtil;
import com.macaku.common.util.ShortCodeUtil;
import com.macaku.common.web.HttpUtils;
import com.macaku.user.domain.dto.WxLoginDTO;
import com.macaku.user.domain.po.User;
import com.macaku.user.service.LoginService;
import com.macaku.user.service.UserService;
import com.macaku.user.token.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 12:49
 */
@Service
@Slf4j
public class LoginServiceJWT implements LoginService {

    private static final String TYPE = JwtUtil.TYPE;

    private UserService userService = SpringUtil.getBean(UserService.class);

    private RedisCache redisCache = SpringUtil.getBean(RedisCache.class);

    @Override
    public boolean match(String type) {
        return ShortCodeUtil.getShortCode(TYPE).equals(type);
    }


    @Override
    public Map<String, Object> login(Map<?, ?> data) {
        WxLoginDTO wxLoginDTO = WxLoginDTO.create(data);
        wxLoginDTO.validate();
        User user = wxLoginDTO.transToUser();
        String code = wxLoginDTO.getCode();
        String iv = wxLoginDTO.getIv();
        String signature = wxLoginDTO.getSignature();
        String rawData = wxLoginDTO.getRawData();
        // 1. 构造请求
        String code2SessionUrl = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, Object> param = new HashMap<String, Object>(){{
            this.put("appid", TokenUtil.APP_ID);
            this.put("secret", TokenUtil.APP_SECRET);
            this.put("js_code", code);
            this.put("grant_type", "authorization_code");
        }};
        // 2. 发起请求 -> code2Session
        String resultJson = HttpUtils.doGet(code2SessionUrl, param);
        // 3.  解析
        Map<String, Object> response = JsonUtil.analyzeJson(resultJson, Map.class);
        String sessionKey = (String) response.get("session_key");
        // 4. 检查
        if(!signature.equals(EncryptUtil.sha1(rawData + sessionKey))) {
            throw new GlobalServiceException(GlobalServiceStatusCode.DATA_NOT_SECURITY);
        }
        // 5. 构造用户对象
        String unionid = (String) response.get("unionid");
        String openid = (String) response.get("openid");
        user.setUnionid(unionid);
        user.setOpenid(openid);
        // 6. 插入数据库
        userService.saveOrUpdate(user); // openid为唯一键，所以如果重复了，会进行更新
        redisCache.setCacheObject(JwtUtil.JWT_LOGIN_USER + openid, user, JwtUtil.JWT_TTL, JwtUtil.JWT_TTL_UNIT);
        // 7. 构造 token
        Map<String, Object> tokenData = new HashMap<String, Object>(){{
            this.put("openid", openid);
            this.put("session_key", sessionKey);
        }};
        String token = JwtUtil.createJWT(JsonUtil.analyzeData(tokenData));
        return new HashMap<String, Object>(){{
            this.put(JwtUtil.JWT_HEADER, token);
        }};
    }


}
