package com.macaku.user.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.ExtractUtil;
import com.macaku.common.util.JsonUtil;
import com.macaku.common.util.JwtUtil;
import com.macaku.common.util.ShortCodeUtil;
import com.macaku.user.component.LoginServiceSelector;
import com.macaku.user.domain.dto.WxLoginDTO;
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
 * Date: 2024-01-21
 * Time: 12:49
 */
@Service
@Slf4j
public class LoginServiceWxImpl implements LoginService {

    private final static String TYPE = LoginServiceSelector.WX_LOGIN_TYPE;

    private final static String DEFAULT_NICKNAME = "微信用户";

    private final static String DEFAULT_PHOTO = "https://mmbiz.qpic.cn/mmbiz/icTdbqWNOwNRna42FI242Lcia07jQodd2FJGIYQfG0LAJGFxM4FbnQP6yfMxBgJ0F3YRqJCJ1aPAK2dQagdusBZg/0";

    private final UserService userService = SpringUtil.getBean(UserService.class);

    @Override
    public boolean match(String type) {
        return ShortCodeUtil.getShortCode(TYPE).equals(type);
    }

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        WxLoginDTO wxLoginDTO = loginDTO.createWxLoginDTO();
        if(Objects.isNull(wxLoginDTO)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        wxLoginDTO.validate();
        // 1. 构造请求 + 发起请求 -> code2Session
        String code = wxLoginDTO.getCode();
        String resultJson = userService.getUserFlag(code);
        // 2.  解析
        Map<String, Object> response = JsonUtil.analyzeJson(resultJson, Map.class);
        String openid = (String) response.get("openid");
        String unionid = (String) response.get("unionid");
        String sessionKey = (String) response.get("session_key");
        if(Objects.isNull(openid)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.WX_CODE_NOT_VALID);
        }
        // 3. 构造用户对象
        User user = wxLoginDTO.transToUser();
        user.setOpenid(openid);
        user.setUnionid(unionid);
        // 4. 尝试插入数据库
        // todo: 多个 openid 用 unionid 去判断是否是同一个用户（需要的时候再去写）
        User dbUser = userService.lambdaQuery().eq(User::getOpenid, openid).one();
        if(Objects.isNull(dbUser)) {
            user.setNickname(DEFAULT_NICKNAME);
            user.setPhoto(DEFAULT_PHOTO);
            userService.save(user);
            log.info("新用户注册 -> {}", user);
        }else {
            user.setId(dbUser.getId());
            // 更新一下数据
            userService.lambdaUpdate().eq(User::getOpenid, openid).update(user);
        }
        // 5. 构造 token
        Map<String, Object> tokenData = new HashMap<String, Object>(){{
            this.put(ExtractUtil.OPENID, openid);
            this.put(ExtractUtil.UNIONID, unionid);
//            this.put(ExtractUtil.SESSION_KEY, sessionKey);
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
