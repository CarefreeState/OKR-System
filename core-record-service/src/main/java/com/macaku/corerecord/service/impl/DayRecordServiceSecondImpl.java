package com.macaku.corerecord.service.impl;

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

    @Override
    public Object getEvent(Long coreId, Boolean isCompleted, Boolean oldCompleted) {
        return PrioritiesUpdate.builder().coreId(coreId).isCompleted(isCompleted).oldCompleted(oldCompleted).build();
    }
}
