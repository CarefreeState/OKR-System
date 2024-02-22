package com.macaku.common.handler;

import com.macaku.common.web.HttpUtil;
import lombok.extern.slf4j.Slf4j;
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

    public final static String REDIRECT_URL = "/";

    public final static String EXCEPTION_MESSAGE = "exceptionMessage";

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        String redirect = REDIRECT_URL + HttpUtil.getQueryString(new HashMap<String, Object>(){{
            this.put(EXCEPTION_MESSAGE, e.getMessage());
        }});
        log.warn("'{}' 重定向 --> '{}'", httpServletRequest.getRequestURI() , redirect);
        httpServletResponse.sendRedirect(redirect);
    }

}
