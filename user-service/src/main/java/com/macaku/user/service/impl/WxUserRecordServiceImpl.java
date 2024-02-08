package com.macaku.user.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
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
import java.util.Objects;
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
public class WxUserRecordServiceImpl implements UserRecordService {
    private final static String TYPE = LoginServiceSelector.WX_LOGIN_TYPE;

    private final RedisCache redisCache = SpringUtil.getBean(RedisCache.class);

    @Override
    public boolean match(String type) {
        return ShortCodeUtil.getShortCode(TYPE).equals(type);
    }

    @Override
    public Optional<User> getRecord(HttpServletRequest request) {
        String openid = ExtractUtil.getOpenIDFromJWT(request);
        String redisKey = JwtUtil.JWT_LOGIN_WX_USER + openid;
        return Optional.ofNullable((User) redisCache.getCacheObject(redisKey)
                .orElseGet(() -> {
                    User dbUser = Db.lambdaQuery(User.class).eq(User::getOpenid, openid).one();
                    if(Objects.isNull(dbUser)) {
                        return null;
                    }else {
                        redisCache.setCacheObject(redisKey, dbUser, JwtUtil.JWT_TTL, JwtUtil.JWT_TTL_UNIT);
                        return dbUser;
                    }
                }));
    }

    @Override
    public void deleteRecord(HttpServletRequest request) {
        String openid = ExtractUtil.getOpenIDFromJWT(request);
        redisCache.deleteObject(JwtUtil.JWT_LOGIN_WX_USER + openid);
    }
}
