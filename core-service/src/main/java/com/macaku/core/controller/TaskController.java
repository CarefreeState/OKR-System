package com.macaku.core.controller;

import com.macaku.common.response.SystemJsonResponse;
import com.macaku.core.domain.po.inner.dto.TaskDTO;
import com.macaku.core.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 1:53
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/task")
@Api(tags = "任务管理")
public class TaskController {


    @PostMapping("/{type}/add")
    @ApiOperation("增加一条任务")
    public SystemJsonResponse addTask(@PathVariable("type") @NonNull @ApiParam("类型") Integer type, TaskDTO taskDTO) {
        // 检查
        taskDTO.validate();
        Long quadrantId = taskDTO.getQuadrantId();
        String content = taskDTO.getContent();
        // 选择服务
        ServiceLoader<TaskService> taskServices = ServiceLoader.load(TaskService.class);
        Iterator<TaskService> serviceIterator = taskServices.iterator();
        while(serviceIterator.hasNext()) {
            TaskService taskService = serviceIterator.next();
            if(taskService.match(type)) {
                taskService.addTask(quadrantId, content);
                break;
            }
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }




}
