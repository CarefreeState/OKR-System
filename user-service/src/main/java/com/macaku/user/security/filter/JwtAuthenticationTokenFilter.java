package com.macaku.user.security.filter;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.user.security.handler.AuthFailHandler;
import com.macaku.user.domain.dto.detail.LoginUser;
import com.macaku.user.util.UserRecordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-02-22
 * Time: 1:39
 */
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            LoginUser userRecord = Optional.ofNullable(UserRecordUtil.getUserRecord(httpServletRequest))
                    .orElseThrow(() -> new GlobalServiceException(GlobalServiceStatusCode.USER_TOKEN_NOT_VALID));
            PreAuthenticatedAuthenticationToken authenticationToken =
                    new PreAuthenticatedAuthenticationToken(userRecord, null, userRecord.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e) {
            httpServletResponse.setHeader(AuthFailHandler.EXCEPTION_MESSAGE, e.getMessage());
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);//放行
    }
}
