package com.macaku.user.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.user.util.ExtractUtil;
import com.macaku.common.util.JsonUtil;
import com.macaku.common.util.JwtUtil;
import com.macaku.common.util.ShortCodeUtil;
import com.macaku.email.component.EmailServiceSelector;
import com.macaku.user.component.LoginServiceSelector;
import com.macaku.user.domain.dto.EmailLoginDTO;
import com.macaku.user.domain.dto.unify.LoginDTO;
import com.macaku.user.domain.po.User;
import com.macaku.user.service.LoginService;
import com.macaku.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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

    private final static String TYPE = LoginServiceSelector.EMAIL_LOGIN_TYPE;

    private final static String DEFAULT_NICKNAME = "邮箱用户";

    private final static String DEFAULT_PHOTO = "https://mmbiz.qpic.cn/mmbiz/icTdbqWNOwNRna42FI242Lcia07jQodd2FJGIYQfG0LAJGFxM4FbnQP6yfMxBgJ0F3YRqJCJ1aPAK2dQagdusBZg/0";

    private final UserService userService = SpringUtil.getBean(UserService.class);

    private final EmailServiceSelector emailServiceSelector = SpringUtil.getBean(EmailServiceSelector.class);

    @Override
    public boolean match(String type) {
        return ShortCodeUtil.getShortCode(TYPE).equals(type);
    }

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        EmailLoginDTO emailLoginDTO = loginDTO.createEmailLoginDTO();
        if(Objects.isNull(emailLoginDTO)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        emailLoginDTO.validate();
        String email = emailLoginDTO.getEmail();
        String code = emailLoginDTO.getCode();
        // 验证码验证
        emailServiceSelector.
                select(EmailServiceSelector.EMAIL_LOGIN).
                checkIdentifyingCode(email, code);
        User user = emailLoginDTO.transToUser();
        // 如果用户未不存在（邮箱未注册），则注册
        User dbUser = userService.lambdaQuery().eq(User::getEmail, email).one();
        if(Objects.isNull(dbUser)) {
            user.setNickname(DEFAULT_NICKNAME);
            user.setPhoto(DEFAULT_PHOTO);
            userService.save(user);
            log.info("新用户注册 -> {}", user);
        }else {
            user.setId(dbUser.getId());
        }
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

    @Override
    public void logout(HttpServletRequest request) {
        ExtractUtil.joinTheTokenBlacklist(request);
    }

}
