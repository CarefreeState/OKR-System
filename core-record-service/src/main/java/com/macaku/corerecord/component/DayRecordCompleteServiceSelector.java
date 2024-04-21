package com.macaku.corerecord.component;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.component.TaskServiceSelector;
import com.macaku.corerecord.service.DayRecordCompleteService;
import org.springframework.stereotype.Component;

import java.util.ServiceLoader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-21
 * Time: 12:38
 */
@Component
public class DayRecordCompleteServiceSelector {

    public final static Integer PRIORITY_ONE_OPTION = TaskServiceSelector.PRIORITY_ONE_OPTION;

    public final static Integer PRIORITY_TWO_OPTION = TaskServiceSelector.PRIORITY_TWO_OPTION;

    public final static Integer ACTION_OPTION = TaskServiceSelector.ACTION_OPTION;

    private final ServiceLoader<DayRecordCompleteService> dayRecordCompleteServices = ServiceLoader.load(DayRecordCompleteService.class);

    public DayRecordCompleteService select(Integer option) {
        // 选取服务
        for (DayRecordCompleteService dayRecordCompleteService : dayRecordCompleteServices) {
            if (dayRecordCompleteService.match(option)) {
                return dayRecordCompleteService;
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.PARAM_TYPE_ERROR);
    }

}
