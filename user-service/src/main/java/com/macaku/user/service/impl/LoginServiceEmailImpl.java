package com.macaku.user.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.redis.RedisCache;
import com.macaku.common.util.ExtractUtil;
import com.macaku.common.util.JsonUtil;
import com.macaku.common.util.JwtUtil;
import com.macaku.common.util.ShortCodeUtil;
import com.macaku.user.domain.dto.EmailLoginDTO;
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
 * Date: 2024-01-24
 * Time: 13:18
 */
@Service
@Slf4j
public class LoginServiceEmailImpl implements LoginService {

    private static final String TYPE = JwtUtil.EMAIL_LOGIN_TYPE;

    private UserService userService = SpringUtil.getBean(UserService.class);

    private RedisCache redisCache = SpringUtil.getBean(RedisCache.class);

    @Override
    public boolean match(String type) {
        return ShortCodeUtil.getShortCode(TYPE).equals(type);
    }

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        EmailLoginDTO emailLoginDTO = EmailLoginDTO.create(loginDTO);
        emailLoginDTO.validate();
        // todo: （这里邮箱登录只是方便测试，所以原本的验证码验证的工作在这里省略了）
        String email = emailLoginDTO.getEmail();
        String code = emailLoginDTO.getCode();
        User user = emailLoginDTO.transToUser();
        // 如果用户未不存在（邮箱未注册），则注册
        User dbUser = userService.lambdaQuery().eq(User::getEmail, email).one();
        if(Objects.isNull(dbUser)) {
            userService.save(user);
            log.info("新用户注册 -> {}", user);
            dbUser = user;
        }else {
            user.setId(dbUser.getId());
        }
        // 记录一下
        redisCache.setCacheObject(JwtUtil.JWT_LOGIN_EMAIL_USER + user.getId(), dbUser,
                JwtUtil.JWT_TTL, JwtUtil.JWT_TTL_UNIT);
        // 构造 token
        Map<String, Object> tokenData = new HashMap<String, Object>(){{
            this.put(ExtractUtil.ID, user.getId());
        }};
        String jsonData = JsonUtil.analyzeData(tokenData);
        String token = JwtUtil.createJWT(jsonData);
        return new HashMap<String, Object>(){{
            this.put(JwtUtil.JWT_HEADER, token);
        }};
    }
}
