package com.macaku.xxljob.service;

import com.macaku.xxljob.model.XxlJobGroup;

import java.util.List;

public interface JobGroupService {

    List<XxlJobGroup> getJobGroup();

    void addJobGroup();

    void autoRegisterGroup();

    boolean preciselyCheck();

    Integer getJobGroupId();
}
