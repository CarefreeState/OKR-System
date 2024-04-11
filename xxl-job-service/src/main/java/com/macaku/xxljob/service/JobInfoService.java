package com.macaku.xxljob.service;

import com.macaku.xxljob.model.XxlJobInfo;

import java.util.List;

public interface JobInfoService {

    List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler);

    Integer addJob(XxlJobInfo xxlJobInfo);

    void startJob(Integer jobId);

    void removeAll(String executorHandler);

    void removeStoppedJob(String executorHandler);

}
