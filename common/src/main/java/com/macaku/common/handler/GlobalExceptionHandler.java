package com.macaku.common.handler;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public static SystemJsonResponse getGlobalServiceExceptionResult(GlobalServiceException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String message = e.getMessage();
        GlobalServiceStatusCode statusCode = e.getStatusCode();
        log.error("请求地址'{}', {}: {}", requestURI, statusCode, message);
        return SystemJsonResponse.CUSTOMIZE_MSG_ERROR(statusCode, message);
    }

    @ExceptionHandler(GlobalServiceException.class)
    public SystemJsonResponse handleGlobalServiceException(GlobalServiceException e, HttpServletRequest request) {
        return getGlobalServiceExceptionResult(e, request);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public SystemJsonResponse handleExpiredJwtException(ExpiredJwtException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String message = e.getMessage();
        log.error("请求地址'{}' {}", requestURI, message);
        e.printStackTrace();
        return SystemJsonResponse.CUSTOMIZE_MSG_ERROR(GlobalServiceStatusCode.USER_TOKEN_NOT_VALID, GlobalServiceStatusCode.USER_TOKEN_NOT_VALID.getMessage());
    }

    @ExceptionHandler(SignatureException.class)
    public SystemJsonResponse handleSignatureException(SignatureException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String message = e.getMessage();
        log.error("请求地址'{}' {}", requestURI, message);
        e.printStackTrace();
        return SystemJsonResponse.CUSTOMIZE_MSG_ERROR(GlobalServiceStatusCode.USER_TOKEN_NOT_VALID, GlobalServiceStatusCode.USER_TOKEN_NOT_VALID.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public SystemJsonResponse handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String message = e.getMessage();
        log.error("请求地址'{}' {}", requestURI, message);
        e.printStackTrace();
        return SystemJsonResponse.CUSTOMIZE_MSG_ERROR(GlobalServiceStatusCode.SYSTEM_SERVICE_FAIL, message);
    }

}
