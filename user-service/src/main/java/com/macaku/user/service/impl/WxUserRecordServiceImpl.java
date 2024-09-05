package com.macaku.user.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.common.util.convert.JwtUtil;
import com.macaku.redis.repository.RedisCache;
import com.macaku.user.domain.dto.detail.LoginUser;
import com.macaku.user.domain.po.User;
import com.macaku.user.service.UserRecordService;
import com.macaku.user.service.UserService;
import com.macaku.user.util.ExtractUtil;
import lombok.RequiredArgsConstructor;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class WxUserRecordServiceImpl implements UserRecordService {

    private final RedisCache redisCache;

    private final UserService userService;

    @Override
    public Optional<LoginUser> getRecord(HttpServletRequest request) {
        if(ExtractUtil.isInTheTokenBlacklist(request)) {
            return Optional.empty();
        }
        String openid = ExtractUtil.getOpenIDFromJWT(request);
        String redisKey = JwtUtil.JWT_LOGIN_WX_USER + openid;
        return Optional.ofNullable((LoginUser) redisCache.getCacheObject(redisKey)
                .orElseGet(() -> {
                    User dbUser = Db.lambdaQuery(User.class).eq(User::getOpenid, openid).one();
                    if(Objects.isNull(dbUser)) {
                        return null;
                    }else {
                        LoginUser loginUser = new LoginUser(dbUser, userService.getPermissions(dbUser.getId()));
                        redisCache.setCacheObject(redisKey, loginUser, JwtUtil.JWT_TTL, JwtUtil.JWT_TTL_UNIT);
                        return loginUser;
                    }
                }));
    }

    @Override
    public void deleteRecord(HttpServletRequest request) {
        String openid = ExtractUtil.getOpenIDFromJWT(request);
        redisCache.deleteObject(JwtUtil.JWT_LOGIN_WX_USER + openid);
    }
}