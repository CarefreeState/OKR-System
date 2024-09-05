package com.macaku.corerecord.service.impl;

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
    @Override
    public Object getEvent(Long coreId, Boolean isCompleted, Boolean oldCompleted) {
        return ActionUpdate.builder().coreId(coreId).isCompleted(isCompleted).oldCompleted(oldCompleted).build();
    }
}
