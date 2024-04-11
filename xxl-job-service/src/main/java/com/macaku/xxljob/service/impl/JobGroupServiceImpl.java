package com.macaku.xxljob.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.macaku.common.util.convert.JsonUtil;
import com.macaku.redis.repository.RedisCache;
import com.macaku.redis.repository.RedisLock;
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
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobGroupServiceImpl implements JobGroupService {

    private final static String XXL_JOB_GROUP = "xxlJobGroup:%s:%s";

    private final static String XXL_JOB_GROUP_LOCK = "xxlJobGroupLock:%s:%s";

    private final static Long XXL_JOB_GROUP_TTL = 1L;

    private final static TimeUnit XXL_JOB_GROUP_TIMEUNIT = TimeUnit.DAYS;

    private final Admin admin;

    private final Executor executor;

    private final XxlUrl xxlUrl;

    private final RedisCache redisCache;

    private final RedisLock redisLock;

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
    public XxlJobGroup getJobGroupOne(int index) {
        addJobGroup();
        return getJobGroup().get(0);
    }

    @Override
    public void addJobGroup() {
        String lock = String.format(XXL_JOB_GROUP_LOCK, executor.getAppname(), executor.getTitle());
        redisLock.tryLockDoSomething(lock, () -> {
            if (Boolean.FALSE.equals(preciselyCheck())) {
                autoRegisterGroup();
            }
        }, () -> {});
    }

    @Override
    public void autoRegisterGroup() {
        String url = admin.getAddresses() + xxlUrl.getGroupSave();
        String appname = executor.getAppname();
        String title = executor.getTitle();
        HttpRequest.post(url)
                .form("appname", appname)
                .form("title", title)
                .form("addressType", executor.getAddressType())
                .form("addressList",  executor.getAddressList())
                .cookie(CookieUtil.getCookie())
                .execute();
        String redisKey = String.format(XXL_JOB_GROUP, appname, title);
        redisCache.deleteObject(redisKey);
    }

    @Override
    public boolean preciselyCheck() {
        String appname = executor.getAppname();
        String title = executor.getTitle();
        String redisKey = String.format(XXL_JOB_GROUP, appname, title);
        return (Boolean) redisCache.getCacheObject(redisKey).orElseGet(() -> {
            boolean flag = getJobGroup().stream().anyMatch(Objects::nonNull);
            redisCache.setCacheObject(redisKey, flag, XXL_JOB_GROUP_TTL, XXL_JOB_GROUP_TIMEUNIT);
            return flag;
        });
    }

    @Override
    public Integer getJobGroupId() {
        return getJobGroupOne(0).getId();
    }
}