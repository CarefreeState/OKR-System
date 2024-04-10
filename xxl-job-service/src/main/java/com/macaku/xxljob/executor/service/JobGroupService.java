package com.macaku.xxljob.executor.service;

import com.macaku.xxljob.executor.model.XxlJobGroup;

import java.util.List;

public interface JobGroupService {

    List<XxlJobGroup> getJobGroup();

    Integer getJobGroupId();

    boolean autoRegisterGroup();

    boolean preciselyCheck();

}
