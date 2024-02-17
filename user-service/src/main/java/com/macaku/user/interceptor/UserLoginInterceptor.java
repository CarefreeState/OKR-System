package com.macaku.user.interceptor;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.user.util.UserRecordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

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
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if(Objects.isNull(UserRecordUtil.getUserRecord(request))) {
                throw new GlobalServiceException(GlobalServiceStatusCode.USER_TOKEN_NOT_VALID);
            }
        } catch (Exception e) {
            throw new GlobalServiceException(e.getMessage(), GlobalServiceStatusCode.USER_NOT_LOGIN);
        }
        return true;
    }
}
