package com.macaku.common.interceptor;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.interceptor.config.VisitConfig;
import com.macaku.common.redis.RedisCache;
import com.macaku.common.util.ExtractUtil;
import com.macaku.common.util.JwtUtil;
import com.macaku.common.util.ShortCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@Component
@RequiredArgsConstructor
public class WxUserInterceptor implements HandlerInterceptor {

    private static final String TYPE = JwtUtil.WX_LOGIN_TYPE;

    private final RedisCache redisCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String type = request.getHeader(VisitConfig.HEADER);
        if(!StringUtils.hasText(type)) {
            throw new GlobalServiceException("拦截路径：" + request.getRequestURI(), GlobalServiceStatusCode.PARAM_NOT_COMPLETE);
        }
        if(!ShortCodeUtil.getShortCode(TYPE).equals(type)) {
            return true;
        }
        //业务逻辑（Redis或者Token过期了，都算登录失效）
        String openid = ExtractUtil.getOpenIDFromJWT(request);
        redisCache.getCacheObject(JwtUtil.JWT_LOGIN_WX_USER + openid)
                .orElseThrow(() -> new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_LOGIN));
        return true;
    }
}
