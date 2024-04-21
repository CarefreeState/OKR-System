package com.macaku.corerecord.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.corerecord.component.DayRecordCompleteServiceSelector;
import com.macaku.corerecord.service.DayRecordCompleteService;
import com.macaku.corerecord.service.DayRecordService;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-21
 * Time: 12:46
 */
public class DayRecordServiceThirdImpl implements DayRecordCompleteService {
    private final static Integer OPTION = DayRecordCompleteServiceSelector.ACTION_OPTION;

    private final DayRecordService dayRecordService = SpringUtil.getBean(DayRecordService.class);

    @Override
    public boolean match(Integer option) {
        return OPTION.equals(option);
    }

    @Override
    public void record(Long coreId, Boolean isCompleted, Boolean oldCompleted) {
        dayRecordService.recordThirdQuadrant(coreId, isCompleted, oldCompleted);

    }
}
