package com.macaku.core.component;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.service.TaskService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TaskServiceSelector {

    public final static Integer PRIORITY_ONE_OPTION = 1;

    public final static Integer PRIORITY_TWO_OPTION = 2;

    public final static Integer ACTION_OPTION = 0;

    private final ServiceLoader<TaskService> taskServices = ServiceLoader.load(TaskService.class);

    public TaskService select(Integer option) {
        // 选取服务
        for (TaskService taskService : taskServices) {
            if (taskService.match(option)) {
                return taskService;
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.PARAM_TYPE_ERROR);
    }

}
