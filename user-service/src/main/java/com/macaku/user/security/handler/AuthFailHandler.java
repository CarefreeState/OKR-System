package com.macaku.user.security.handler;

import com.macaku.common.util.thread.local.ThreadLocalUtil;
import com.macaku.common.web.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-02-22
 * Time: 2:42
 */
@Component
@Slf4j
public class AuthFailHandler implements AuthenticationEntryPoint {

    public final static String REDIRECT_URL = "/unlisted";

    public final static String EXCEPTION_MESSAGE = "exceptionMessage";

    public final static String LOCATION_HEADER = "Location";

    @Value("${spring.domain}")
    private String domain;

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        String requestURI = httpServletRequest.getRequestURI();
        String message = e.getMessage() + String.format("%s    (%s)", e.getMessage(), ThreadLocalUtil.get());
        String redirect = domain + REDIRECT_URL + HttpUtil.getQueryString(new HashMap<String, Object>(){{
            this.put(EXCEPTION_MESSAGE, message);
        }});
        log.warn("'{}' 重定向 --> '{}'", requestURI, redirect);
        httpServletResponse.setStatus(HttpStatus.FOUND.value());
        httpServletResponse.setHeader(LOCATION_HEADER, redirect);
    }

}
