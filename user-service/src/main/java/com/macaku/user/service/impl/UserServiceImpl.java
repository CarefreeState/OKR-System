package com.macaku.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.email.component.EmailServiceSelector;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import com.macaku.common.web.HttpUtil;
import com.macaku.user.domain.dto.UserinfoDTO;
import com.macaku.user.domain.po.User;
import com.macaku.user.mapper.UserMapper;
import com.macaku.user.service.UserService;
import com.macaku.user.token.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
* @author 马拉圈
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-01-22 14:18:10
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    private final static String EMAIL_USER_MAP = "emailUserMap:";

    private final static String WX_USER_MAP = "wxUserMap:";

    private final static String USERID_OPENID_MAP = "useridOpenidMap:";

    private final static Long EMAIL_USER_TTL = 2L;

    private final static Long WX_USER_TTL = 2L;

    private final static Long USERID_OPENID_TTL = 2L;

    private final static TimeUnit EMAIL_USER_UNIT = TimeUnit.HOURS;

    private final static TimeUnit WX_USER_UNIT = TimeUnit.HOURS;

    private final static TimeUnit USERID_OPENID_UNIT = TimeUnit.HOURS;

    private final RedisCache redisCache;

    private final EmailServiceSelector emailServiceSelector;


    @Override
    public String getUserFlag(String code) {
        String code2SessionUrl = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, Object> param = new HashMap<String, Object>(){{
            this.put("appid", TokenUtil.APP_ID);
            this.put("secret", TokenUtil.APP_SECRET);
            this.put("js_code", code);
            this.put("grant_type", "authorization_code");
        }};
        return HttpUtil.doGet(code2SessionUrl, param);
    }

    @Override
    public void improveUserinfo(UserinfoDTO userinfoDTO, Long userId) {
        userinfoDTO.validate();
        User user = BeanUtil.copyProperties(userinfoDTO, User.class);
        user.setId(userId);
        // 修改
        this.lambdaUpdate().eq(User::getId, userId).update(user);
    }

    @Override
    public List<String> getPermissions(Long userId) {
        return Collections.emptyList();
    }

    @Override
    public User getUserByEmail(String email) {
        String redisKey = EMAIL_USER_MAP + email;
        return (User) redisCache.getCacheObject(redisKey).orElseGet(() -> {
            User user = this.lambdaQuery().eq(User::getEmail, email).one();
            redisCache.setCacheObject(redisKey, user, EMAIL_USER_TTL, EMAIL_USER_UNIT);
            return user;
        });
    }

    @Override
    public User getUserByOpenid(String openid) {
        String redisKey = WX_USER_MAP + openid;
        return (User) redisCache.getCacheObject(redisKey).orElseGet(() -> {
            User user = this.lambdaQuery().eq(User::getOpenid, openid).one();
            redisCache.setCacheObject(redisKey, user, WX_USER_TTL, WX_USER_UNIT);
            return user;
        });
    }

    @Override
    public String getOpenidByUserId(Long userId) {
        String redisKey = USERID_OPENID_MAP + userId;
        return (String) redisCache.getCacheObject(redisKey).orElseGet(() -> {
            User user = this.lambdaQuery().eq(User::getId, userId).one();
            String openid = Objects.isNull(user) ? null : user.getOpenid();
            redisCache.setCacheObject(redisKey, openid, USERID_OPENID_TTL, USERID_OPENID_UNIT);
            return openid;
        });
    }

    @Override
    public void deleteUserEmailCache(String email) {
        redisCache.deleteObject(EMAIL_USER_MAP + email);
    }

    @Override
    public void deleteUserOpenidCache(String openid) {
        redisCache.deleteObject(WX_USER_MAP + openid);

    }

    @Override
    public void deleteUserIdOpenIdCache(Long userId) {
        redisCache.deleteObject(USERID_OPENID_MAP + userId);

    }

    @Override
    public void bindingEmail(Long userId, String email, String code, String recordEmail) {
        // 检查验证码
        emailServiceSelector
                .select(EmailServiceSelector.EMAIL_BINDING)
                .checkIdentifyingCode(email, code);
        // 判断邮箱用户是否存在
        User userByEmail = getUserByEmail(email);
        if(Objects.nonNull(userByEmail)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.EMAIL_USER_BE_BOUND);
        }
        this.lambdaUpdate()
                .eq(User::getId, userId)
                .set(User::getEmail, email)
                .update();
        deleteUserEmailCache(email);
        if(StringUtils.hasText(recordEmail)) {
            deleteUserEmailCache(recordEmail);
        }
        log.info("用户 {} 成功绑定 邮箱 {}", userId, email);
    }

}




