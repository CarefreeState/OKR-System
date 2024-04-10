package com.macaku.xxljob.executor.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.macaku.xxljob.config.Admin;
import com.macaku.xxljob.config.XxlUrl;
import com.macaku.xxljob.executor.service.JobLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author : Hydra
 * @date: 2022/9/19 17:49
 * @version: 1.0
 */
@Service
@RequiredArgsConstructor
public class JobLoginServiceImpl implements JobLoginService {

    private final static String XXL_JOB_LOGIN_IDENTITY = "XXL_JOB_LOGIN_IDENTITY";

    private final Admin admin;

    private final XxlUrl xxlUrl;

    private final Map<String, String> loginCookie = new HashMap<>();

    @Override
    public void login() {
        String url = admin.getAddresses() + xxlUrl.getLogin();
        HttpResponse response = HttpRequest.post(url)
                .form("userName", admin.getUsername())
                .form("password", admin.getPassword())
                .execute();
        List<HttpCookie> cookies = response.getCookies();
        Optional<HttpCookie> cookieOpt = cookies.stream()
                .filter(cookie -> cookie.getName().equals(XXL_JOB_LOGIN_IDENTITY)).findFirst();
        if (!cookieOpt.isPresent())
            throw new RuntimeException("get xxl-job cookie error!");

        String value = cookieOpt.get().getValue();
        loginCookie.put(XXL_JOB_LOGIN_IDENTITY, value);
    }

    @Override
    public String getCookie() {
        for (int i = 0; i < 3; i++) {
            String cookieStr = loginCookie.get(XXL_JOB_LOGIN_IDENTITY);
            if (cookieStr != null) {
                return String.format("%s=%s", XXL_JOB_LOGIN_IDENTITY, cookieStr);
            }
            login();
        }
        throw new RuntimeException("get xxl-job cookie error!");
    }


}
