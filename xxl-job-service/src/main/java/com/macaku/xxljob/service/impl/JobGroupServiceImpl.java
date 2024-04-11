package com.macaku.xxljob.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.macaku.common.util.convert.JsonUtil;
import com.macaku.xxljob.config.Admin;
import com.macaku.xxljob.config.Executor;
import com.macaku.xxljob.config.XxlUrl;
import com.macaku.xxljob.cookie.CookieUtil;
import com.macaku.xxljob.model.XxlJobGroup;
import com.macaku.xxljob.service.JobGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobGroupServiceImpl implements JobGroupService {

    private final Admin admin;

    private final Executor executor;

    private final XxlUrl xxlUrl;

    @Override
    public List<XxlJobGroup> getJobGroup() {
        String url = admin.getAddresses() + xxlUrl.getGroupPageList();
        HttpResponse response = HttpRequest.post(url)
                .form("appname", executor.getAppname())
                .form("title", executor.getTitle())
                .cookie(CookieUtil.getCookie())
                .execute();
        String body = response.body();
        JSONArray array = JsonUtil.analyzeJsonField(body, "data", JSONArray.class);
        List<XxlJobGroup> list = array.stream()
                .map(o -> JSONUtil.toBean((JSONObject) o, XxlJobGroup.class))
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public void addJobGroup() {
        if (Boolean.FALSE.equals(preciselyCheck())) {
            autoRegisterGroup();
        }
    }

    @Override
    public void autoRegisterGroup() {
        String url = admin.getAddresses() + xxlUrl.getGroupSave();
        HttpRequest.post(url)
                .form("appname", executor.getAppname())
                .form("title", executor.getTitle())
                .form("addressType", executor.getAddressType())
                .form("addressList",  executor.getAddressList())
                .cookie(CookieUtil.getCookie()).execute();
    }

    @Override
    public boolean preciselyCheck() {
        return getJobGroup()
                .stream()
                .anyMatch(xxlJobGroup -> xxlJobGroup.getAppname().equals(executor.getAppname())
                        && xxlJobGroup.getTitle().equals(executor.getTitle()));
    }

    @Override
    public Integer getJobGroupId() {
        addJobGroup();
        return getJobGroup().get(0).getId();
    }
}