package com.macaku.user.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import com.macaku.common.util.*;
import com.macaku.user.component.LoginServiceSelector;
import com.macaku.user.domain.dto.WxLoginDTO;
import com.macaku.user.domain.dto.unify.LoginDTO;
import com.macaku.user.domain.po.User;
import com.macaku.user.service.LoginService;
import com.macaku.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 12:49
 */
@Service
@Slf4j
public class LoginServiceWxImpl implements LoginService {

    private static final String TYPE = LoginServiceSelector.WX_LOGIN_TYPE;

    private final UserService userService = SpringUtil.getBean(UserService.class);

    private final RedisCache redisCache = SpringUtil.getBean(RedisCache.class);

    @Override
    public boolean match(String type) {
        return ShortCodeUtil.getShortCode(TYPE).equals(type);
    }


    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        WxLoginDTO wxLoginDTO = WxLoginDTO.create(loginDTO);
        wxLoginDTO.validate();
        String iv = wxLoginDTO.getIv();
        // 1. 构造请求 + 2. 发起请求 -> code2Session
        String code = wxLoginDTO.getCode();
        String resultJson = userService.getUserFlag(code);
        // 3.  解析
        Map<String, Object> response = JsonUtil.analyzeJson(resultJson, Map.class);
        String sessionKey = (String) response.get("session_key");
        // 4. 检查
        String signature = wxLoginDTO.getSignature();
        String rawData = wxLoginDTO.getRawData();
        if(!signature.equals(EncryptUtil.sha1(rawData, sessionKey))) {
            throw new GlobalServiceException(GlobalServiceStatusCode.DATA_NOT_SECURITY);
        }
        // 5. 构造用户对象
        User user = wxLoginDTO.transToUser();
        String unionid = (String) response.get("unionid");
        String openid = (String) response.get("openid");
        user.setUnionid(unionid);
        user.setOpenid(openid);
        // 6. 插入数据库
        User dbUser = userService.lambdaQuery().eq(User::getOpenid, openid).one();
        if(Objects.isNull(dbUser)) {
            userService.save(user);
            log.info("新用户注册 -> {}", user);
            dbUser = user;
        }else {
            user.setId(dbUser.getId());
            // 更新一下数据
            userService.lambdaUpdate().eq(User::getOpenid, openid).update(user);
        }
        redisCache.setCacheObject(JwtUtil.JWT_LOGIN_WX_USER + openid, dbUser, JwtUtil.JWT_TTL, JwtUtil.JWT_TTL_UNIT);
        // 7. 构造 token
        Map<String, Object> tokenData = new HashMap<String, Object>(){{
            this.put(ExtractUtil.OPENID, openid);
            this.put(ExtractUtil.SESSION_KEY, sessionKey);
        }};
        String jsonData = JsonUtil.analyzeData(tokenData);
        String token = JwtUtil.createJWT(jsonData);
        redisCache.setCacheObject(JwtUtil.JWT_RAW_DATA_MAP + token, jsonData, JwtUtil.JWT_MAP_TTL, JwtUtil.JWT_MAP_TTL_UNIT);
        return new HashMap<String, Object>(){{
            this.put(JwtUtil.JWT_HEADER, token);
        }};
    }


}
