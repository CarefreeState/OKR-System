package com.macaku.xxljob.service;

import com.macaku.xxljob.model.XxlJobInfo;

import java.util.List;

public interface JobInfoService {

    List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler);

    void addJob(XxlJobInfo xxlJobInfo);

    void startJob(Integer jobId);

    void removeAll(String executorHandler);

    void removeStopJob(String executorHandler);

}
