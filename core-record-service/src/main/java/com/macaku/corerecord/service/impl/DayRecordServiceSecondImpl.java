package com.macaku.corerecord.service.impl;

import com.macaku.corerecord.component.DayRecordCompleteServiceSelector;
import com.macaku.corerecord.domain.entry.PrioritiesUpdate;
import com.macaku.corerecord.service.DayRecordCompleteService;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-21
 * Time: 12:46
 */
public class DayRecordServiceSecondImpl implements DayRecordCompleteService {

    private final static Integer OPTION1 = DayRecordCompleteServiceSelector.PRIORITY_ONE_OPTION;

    private final static Integer OPTION2 = DayRecordCompleteServiceSelector.PRIORITY_TWO_OPTION;

    @Override
    public boolean match(Integer option) {
        return OPTION1.equals(option) || OPTION2.equals(option);
    }

    @Override
    public Object getEvent(Long coreId, Boolean isCompleted, Boolean oldCompleted) {
        return PrioritiesUpdate.builder().coreId(coreId).isCompleted(isCompleted).oldCompleted(oldCompleted).build();
    }
}
