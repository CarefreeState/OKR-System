package com.macaku.common.interceptor.service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 19:02
 */
public interface LoginInterceptService {

    boolean match(String type);

    boolean intercept(HttpServletRequest request);

}
