package com.macaku.xxljob.service;

import com.macaku.xxljob.model.XxlJobGroup;

import java.util.List;

public interface JobGroupService {

    List<XxlJobGroup> getJobGroup();

    Integer getJobGroupId();

    boolean autoRegisterGroup();

    boolean preciselyCheck();

}
