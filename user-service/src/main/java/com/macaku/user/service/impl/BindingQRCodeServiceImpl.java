package com.macaku.user.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import com.macaku.common.util.JsonUtil;
import com.macaku.user.domain.po.User;
import com.macaku.user.qrcode.config.QRCodeConfig;
import com.macaku.user.service.BindingQRCodeService;
import com.macaku.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-10
 * Time: 19:36
 */
@Service
@Setter
@Slf4j
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "wx.binding")
public class BindingQRCodeServiceImpl implements BindingQRCodeService {

    private String userKey;

    private String secret;

    private String page;

    private Boolean checkPath;

    private String envVersion;

    private Integer width;

    private Boolean autoColor;

    private Map<String, Integer> lineColor;

    private Boolean isHyaline;

    private final RedisCache redisCache = SpringUtil.getBean(RedisCache.class);

    private final UserService userService = SpringUtil.getBean(UserService.class);

    @Override
    public Map<String, Object> getQRCodeParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("page", StringUtils.hasText(page) ? page : null);
        params.put("check_path", checkPath);
        params.put("env_version", envVersion);
        params.put("width", width);
        params.put("auto_color", autoColor);
        params.put("line_color", lineColor);
        params.put("is_hyaline", isHyaline);
        return params;
    }

    @Override
    public String getQRCodeJson(Long userId, String randomCode) {
        Map<String, Object> params = getQRCodeParams();
        String scene = String.format("%s=%d&%s=%s", userKey, userId, secret, randomCode);
        params.put("scene", scene);
        return JsonUtil.analyzeData(params);
    }

    @Override
    public void bindingWx(Long userId, String randomCode, String code) {
        // 验证以下验证码
        checkIdentifyingCode(userId, randomCode);
        String resultJson = userService.getUserFlag(code);
        Map<String, Object> response = JsonUtil.analyzeJson(resultJson, Map.class);
        String openid = (String) response.get("openid");
        String unionid = (String) response.get("unionid");
        // 查询 openid 是否被注册过
        User userByOpenid = userService.getUserByOpenid(openid);
        if(Objects.nonNull(userByOpenid)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.WX_USER_BE_BOUND);
        }
        // 判断当前用户是否绑定了微信
        // todo: 避免混乱所以现在暂且不支持微信重新绑定，之后需要再说
        String openidByUserId = userService.getOpenidByUserId(userId);
        if(Objects.nonNull(openidByUserId)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_BOUND_WX);
        }
        userService.lambdaUpdate()
                .eq(User::getId, userId)
                .set(User::getOpenid, openid)
                .update();
        userService.deleteUserIdOpenIdCache(userId);
        userService.deleteUserOpenidCache(openid);
        log.info("用户 {} 成功绑定 微信 {}", userId, openid);
    }

    @Override
    public void checkIdentifyingCode(Long userId, String randomCode) {
        String redisKey = QRCodeConfig.WX_CHECK_QR_CODE_MAP + userId;
        String code = (String) redisCache.getCacheObject(redisKey).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.WX_NOT_EXIST_RECORD));
        if(!randomCode.equals(code)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.WX_CODE_NOT_CONSISTENT);
        }
        redisCache.deleteObject(redisKey);
    }

}
