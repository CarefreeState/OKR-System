package com.macaku.corerecord.service.impl;

import com.macaku.corerecord.component.DayRecordCompleteServiceSelector;
import com.macaku.corerecord.domain.entry.ActionUpdate;
import com.macaku.corerecord.service.DayRecordCompleteService;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-21
 * Time: 12:46
 */
public class DayRecordServiceThirdImpl implements DayRecordCompleteService {
    private final static Integer OPTION = DayRecordCompleteServiceSelector.ACTION_OPTION;

    @Override
    public boolean match(Integer option) {
        return OPTION.equals(option);
    }

    @Override
    public Object getEvent(Long coreId, Boolean isCompleted, Boolean oldCompleted) {
        return ActionUpdate.builder().coreId(coreId).isCompleted(isCompleted).oldCompleted(oldCompleted).build();
    }
}
