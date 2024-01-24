package com.macaku.common.interceptor.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.interceptor.service.LoginInterceptService;
import com.macaku.common.redis.RedisCache;
import com.macaku.common.util.ExtractUtil;
import com.macaku.common.util.JwtUtil;
import com.macaku.common.util.ShortCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 19:04
 */
@Service
@Slf4j
public class EmailInterceptServiceImpl implements LoginInterceptService {

    private static final String TYPE = JwtUtil.EMAIL_LOGIN_TYPE;

    private final RedisCache redisCache = SpringUtil.getBean(RedisCache.class);

    @Override
    public boolean match(String type) {
        return ShortCodeUtil.getShortCode(TYPE).equals(type);
    }

    @Override
    public boolean intercept(HttpServletRequest request) {
        //业务逻辑（Redis或者Token过期了，都算登录失效）
        Long id = ExtractUtil.getUserIdFromJWT(request);
        Object object = redisCache.getCacheObject(JwtUtil.JWT_LOGIN_EMAIL_USER + id).orElse(null);
        if(Objects.isNull(object)) {
            log.warn("拦截路径：" + request.getRequestURI());
            return false;
        }else {
            return true;
        }
    }
}
