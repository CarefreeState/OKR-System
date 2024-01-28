package com.macaku.core.component;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.service.TaskService;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 18:52
 */
@Component
public class TaskServiceSelector {

    public final static Integer PRIORITY_ONE_OPTION = 1;

    public final static Integer PRIORITY_TWO_OPTION = 2;

    public final static Integer ACTION_OPTION = 0;

    public TaskService select(Integer option) {
        // 选取服务
        ServiceLoader<TaskService> taskServices = ServiceLoader.load(TaskService.class);
        Iterator<TaskService> serviceIterator = taskServices.iterator();
        while (serviceIterator.hasNext()) {
            TaskService taskService =  serviceIterator.next();
            if(taskService.match(option)) {
                return taskService;
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.PARAM_TYPE_ERROR);
    }


}
