package com.macaku.medal.component;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.component.TaskServiceSelector;
import com.macaku.medal.service.TermAchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 18:52
 */
@Component
@RequiredArgsConstructor
public class TermAchievementServiceSelector {

    public final static Integer PRIORITY_ONE_OPTION = TaskServiceSelector.PRIORITY_ONE_OPTION;

    public final static Integer PRIORITY_TWO_OPTION = TaskServiceSelector.PRIORITY_TWO_OPTION;

    public final static Integer ACTION_OPTION = TaskServiceSelector.ACTION_OPTION;

    public final List<TermAchievementService> termAchievementServices;

    public TermAchievementService select(Integer option) {
        // 选取服务
        System.out.println("hello?");
        for (TermAchievementService termAchievementService : termAchievementServices) {
            if (termAchievementService.match(option)) {
                return termAchievementService;
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.PARAM_TYPE_ERROR);
    }


}
