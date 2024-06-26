package com.macaku.xxljob.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.xxljob.config.Admin;
import com.macaku.xxljob.config.XxlUrl;
import com.macaku.xxljob.cookie.CookieUtil;
import com.macaku.xxljob.service.JobLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobLoginServiceImpl implements JobLoginService {

    private final Admin admin;

    private final XxlUrl xxlUrl;

    @Override
    public String login() {
        String url = admin.getAddresses() + xxlUrl.getLogin();
        HttpResponse response = HttpRequest.post(url)
                .form("userName", admin.getUsername())
                .form("password", admin.getPassword())
                .execute();
        List<HttpCookie> cookies = response.getCookies();
        Optional<HttpCookie> cookieOpt = cookies.stream()
                .filter(cookie -> cookie.getName().equals(CookieUtil.XXL_JOB_LOGIN_IDENTITY)).findFirst();
        return cookieOpt.orElseThrow(() ->
                    new GlobalServiceException("get xxl-job cookie error!")
                ).getValue();
    }

}
