package com.macaku.user.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.redis.RedisCache;
import com.macaku.common.util.ExtractUtil;
import com.macaku.common.util.JwtUtil;
import com.macaku.common.util.ShortCodeUtil;
import com.macaku.user.component.LoginServiceSelector;
import com.macaku.user.domain.po.User;
import com.macaku.user.service.UserRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 19:04
 */
@Service
@Slf4j
public class EmailUserRecordServiceImpl implements UserRecordService {

    private static final String TYPE = LoginServiceSelector.EMAIL_LOGIN_TYPE;

    private final RedisCache redisCache = SpringUtil.getBean(RedisCache.class);

    @Override
    public boolean match(String type) {
//        return false;
        return ShortCodeUtil.getShortCode(TYPE).equals(type);
    }

    @Override
    public Optional<User> getRecord(HttpServletRequest request) {
        //业务逻辑（Redis或者Token过期了，都算登录失效）
        Long id = ExtractUtil.getUserIdFromJWT(request);
        return Optional.ofNullable((User) redisCache.getCacheObject(JwtUtil.JWT_LOGIN_EMAIL_USER + id)
                .orElse(null));
    }
}
