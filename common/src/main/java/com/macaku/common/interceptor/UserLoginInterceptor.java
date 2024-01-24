package com.macaku.common.interceptor;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.interceptor.config.VisitConfig;
import com.macaku.common.interceptor.service.LoginInterceptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 12:13
 */
@Slf4j
@Component
public class UserLoginInterceptor implements HandlerInterceptor {

    private void forbid(HttpServletRequest request, HttpServletResponse response) {
        log.warn("拦截路径：" + request.getRequestURI());
        throw new GlobalServiceException(GlobalServiceStatusCode.HEAD_NOT_VALID);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String type = request.getHeader(VisitConfig.HEADER);
        if(!StringUtils.hasText(type)) {
            forbid(request, response);
            return false;
        }
        ServiceLoader<LoginInterceptService> interceptServices = ServiceLoader.load(LoginInterceptService.class);
        Iterator<LoginInterceptService> serviceIterator = interceptServices.iterator();
        boolean flag = false;
        while (serviceIterator.hasNext()) {
            LoginInterceptService interceptService = serviceIterator.next();
            if(interceptService.match(type)) {
                flag = interceptService.intercept(request);
                break;
            }
        }
        if(Boolean.FALSE.equals(flag)) {
            forbid(request, response);
            return false;
        }else {
            return true;
        }

    }
}
