package com.macaku.xxljob.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.convert.JsonUtil;
import com.macaku.common.util.thread.pool.CPUThreadPool;
import com.macaku.xxljob.config.Admin;
import com.macaku.xxljob.config.Executor;
import com.macaku.xxljob.config.XxlUrl;
import com.macaku.xxljob.cookie.CookieUtil;
import com.macaku.xxljob.model.XxlJobInfo;
import com.macaku.xxljob.service.JobInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobInfoServiceImpl implements JobInfoService {

    private final Admin admin;

    private final XxlUrl xxlUrl;

    private final Executor executor;

    @Override
    public List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler) {
        String url = admin.getAddresses() + xxlUrl.getInfoPageList();
        HttpResponse response = HttpRequest.post(url)
                .form("jobGroup", jobGroupId)
                .form("executorHandler", executorHandler)
                .form("triggerStatus", -1)
                .cookie(CookieUtil.getCookie())
                .execute();

        String body = response.body();
        JSONArray array = JsonUtil.analyzeJsonField(body, "data", JSONArray.class);
        List<XxlJobInfo> list = array.stream()
                .map(o -> JSONUtil.toBean((JSONObject) o, XxlJobInfo.class))
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public Integer addJob(XxlJobInfo xxlJobInfo) {
        String url = admin.getAddresses() + xxlUrl.getInfoAdd();
        Map<String, Object> paramMap = BeanUtil.beanToMap(xxlJobInfo);
        HttpResponse response = HttpRequest.post(url)
                .form(paramMap)
                .cookie(CookieUtil.getCookie())
                .execute();
        String body = response.body();
        Integer code = JsonUtil.analyzeJsonField(body, "code", Integer.class);
        if (code.equals(200)) {
            return JsonUtil.analyzeJsonField(body, "content", Integer.class);
        }else {
            throw new GlobalServiceException("add jobInfo error!");
        }
    }

    @Override
    public void startJob(Integer jobId) {
            HttpRequest.post(admin.getAddresses() + xxlUrl.getInfoStart())
                    .form("id", jobId)
                    .cookie(CookieUtil.getCookie())
                    .execute();
    }

    private void remove(List<Object> ids) {
        String url = admin.getAddresses() + xxlUrl.getInfoRemove();
        String cookie = CookieUtil.getCookie();
        CPUThreadPool.operateBatch(ids, object -> {
            HttpRequest.post(url)
                    .form("id", object)
                    .cookie(cookie)
                    .execute();
        });
    }

    @Override
    public void removeAll(String executorHandler) {
        String body = HttpRequest.post(admin.getAddresses() + xxlUrl.getInfoIds())
                .form("executorHandler", executorHandler)
                .form("title", executor.getTitle())
                .form("appName", executor.getAppname())
                .cookie(CookieUtil.getCookie())
                .execute().body();
        List<Object> ids = JsonUtil.analyzeJsonField(body, "content", List.class);
        log.info("删除任务 {}", ids);
        remove(ids);
    }

    @Override
    public void removeStoppedJob(String executorHandler) {
        String body = HttpRequest.post(admin.getAddresses() + xxlUrl.getInfoStopIds())
                .form("executorHandler", executorHandler)
                .form("title", executor.getTitle())
                .form("appName", executor.getAppname())
                .cookie(CookieUtil.getCookie())
                .execute().body();
        List<Object> ids = JsonUtil.analyzeJsonField(body, "content", List.class);
        log.info("删除任务 {}", ids);
        remove(ids);
    }

}