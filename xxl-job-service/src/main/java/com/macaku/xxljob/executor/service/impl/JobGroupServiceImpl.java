package com.macaku.xxljob.executor.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.macaku.xxljob.config.Admin;
import com.macaku.xxljob.config.Executor;
import com.macaku.xxljob.config.XxlUrl;
import com.macaku.xxljob.executor.model.XxlJobGroup;
import com.macaku.xxljob.executor.service.JobGroupService;
import com.macaku.xxljob.executor.service.JobLoginService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : Hydra
 * @date: 2022/9/19 17:34
 * @version: 1.0
 */
@Service
@RequiredArgsConstructor
public class JobGroupServiceImpl implements JobGroupService {

    private final Admin admin;

    private final Executor executor;

    private final XxlUrl xxlUrl;

    private final JobLoginService jobLoginService;

    @Override
    public List<XxlJobGroup> getJobGroup() {
        String url = admin.getAddresses() + xxlUrl.getGroupPageList();
        HttpResponse response = HttpRequest.post(url)
                .form("appname", executor.getAppname())
                .form("title", executor.getTitle())
                .cookie(jobLoginService.getCookie())
                .execute();

        String body = response.body();
        JSONArray array = JSONUtil.parse(body).getByPath("data", JSONArray.class);
        List<XxlJobGroup> list = array.stream()
                .map(o -> JSONUtil.toBean((JSONObject) o, XxlJobGroup.class))
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public Integer getJobGroupId() {
        return getJobGroup().get(0).getId();
    }

    @Override
    public boolean autoRegisterGroup() {

        String url = admin.getAddresses() + xxlUrl.getGroupSave();
        HttpRequest httpRequest = HttpRequest.post(url)
                .form("appname", executor.getAppname())
                .form("title", executor.getTitle());
        String addressType = executor.getAddressType();
        String addressList = executor.getAddressList();
        httpRequest.form("addressType", addressType);
        if (addressType.equals(1)) {
            if (Strings.isBlank(addressList)) {
                throw new RuntimeException("手动录入模式下,执行器地址列表不能为空");
            }
            httpRequest.form("addressList", addressList);
        }
        HttpResponse response = httpRequest.cookie(jobLoginService.getCookie())
                .execute();
        Object code = JSONUtil.parse(response.body()).getByPath("code");
        return code.equals(200);
    }

    @Override
    public boolean preciselyCheck() {
        List<XxlJobGroup> jobGroup = getJobGroup();
        Optional<XxlJobGroup> has = jobGroup.stream()
                .filter(xxlJobGroup -> xxlJobGroup.getAppname().equals(executor.getAppname())
                        && xxlJobGroup.getTitle().equals(executor.getTitle()))
                .findAny();
        return has.isPresent();
    }

}