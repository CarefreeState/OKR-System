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

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 12:13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailUserInterceptor implements HandlerInterceptor {

    private static final String TYPE = JwtUtil.EMAIL_LOGIN_TYPE;

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
        String id = ExtractUtil.getUserIdFromJWT(request);
        redisCache.getCacheObject(JwtUtil.EMAIL_LOGIN_TYPE + id)
                .orElseThrow(() -> new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_LOGIN));
        return true;
    }

}
