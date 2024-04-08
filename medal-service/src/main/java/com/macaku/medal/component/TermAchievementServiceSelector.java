package com.macaku.medal.component;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.component.TaskServiceSelector;
import com.macaku.medal.service.TermAchievementService;
import org.springframework.stereotype.Component;

import java.util.ServiceLoader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 18:52
 */
@Component
public class TermAchievementServiceSelector {

    public final static Integer PRIORITY_ONE_OPTION = TaskServiceSelector.PRIORITY_ONE_OPTION;

    public final static Integer PRIORITY_TWO_OPTION = TaskServiceSelector.PRIORITY_TWO_OPTION;

    public final static Integer ACTION_OPTION = TaskServiceSelector.ACTION_OPTION;

    public TermAchievementService select(Integer option) {
        ServiceLoader<TermAchievementService> termAchievementServices = ServiceLoader.load(TermAchievementService.class);
        // 选取服务
        for (TermAchievementService termAchievementService : termAchievementServices) {
            if (termAchievementService.match(option)) {
                return termAchievementService;
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.PARAM_TYPE_ERROR);
    }

}
