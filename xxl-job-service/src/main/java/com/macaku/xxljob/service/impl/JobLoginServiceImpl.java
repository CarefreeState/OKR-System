package com.macaku.xxljob.service.impl;

import cn.hutool.http.HttpRequest;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.xxljob.config.Admin;
import com.macaku.xxljob.config.XxlUrl;
import com.macaku.xxljob.service.JobLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;

@Service
@RequiredArgsConstructor
public class JobLoginServiceImpl implements JobLoginService {

    private final static String XXL_JOB_LOGIN_IDENTITY = "XXL_JOB_LOGIN_IDENTITY";

    private final Admin admin;

    private final XxlUrl xxlUrl;

    @Override
    public String login() {
        return HttpRequest.post(admin.getAddresses() + xxlUrl.getLogin())
                .form("userName", admin.getUsername())
                .form("password", admin.getPassword())
                .execute()
                .getCookies()
                .stream()
                .filter(cookie -> cookie.getName().equals(XXL_JOB_LOGIN_IDENTITY))
                .findFirst()
                .map(HttpCookie::getValue)
                .map(cookie -> String.format("%s=%s", XXL_JOB_LOGIN_IDENTITY, cookie))
                .orElseThrow(() ->
                        new GlobalServiceException("get xxl-job cookie error!")
                );
    }

}
