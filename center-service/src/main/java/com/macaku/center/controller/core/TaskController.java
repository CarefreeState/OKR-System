package com.macaku.center.controller.core;

import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.inner.OkrTaskDTO;
import com.macaku.center.domain.dto.unify.inner.OkrTaskRemoveDTO;
import com.macaku.center.domain.dto.unify.inner.OkrTaskUpdateDTO;
import com.macaku.center.service.OkrOperateService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.common.util.thread.pool.IOThreadPool;
import com.macaku.core.component.TaskServiceSelector;
import com.macaku.core.domain.po.inner.dto.TaskDTO;
import com.macaku.core.domain.po.inner.dto.TaskUpdateDTO;
import com.macaku.core.service.TaskService;
import com.macaku.medal.component.TermAchievementServiceSelector;
import com.macaku.medal.service.TermAchievementService;
import com.macaku.user.domain.po.User;
import com.macaku.user.util.UserRecordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    private final OkrServiceSelector okrServiceSelector;

    private final TaskServiceSelector taskServiceSelector;

    private final TermAchievementServiceSelector termAchievementServiceSelector;

    @PostMapping("/{option}/add")
    @ApiOperation("增加一条任务")
    public SystemJsonResponse addTask(@PathVariable("option") @NonNull @ApiParam("任务选项（0:action, 1:P1, 2:P2）") Integer option,
                                      @RequestBody OkrTaskDTO okrTaskDTO) {
        // 检查
        okrTaskDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        TaskDTO taskDTO = okrTaskDTO.getTaskDTO();
        taskDTO.validate();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrTaskDTO.getScene());
        TaskService taskService = taskServiceSelector.select(option);
        // 检测身份
        Long quadrantId = taskDTO.getQuadrantId();
        Long coreId = taskService.getTaskCoreId(quadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        Long id = null;
        if(user.getId().equals(userId)) {
            String content = taskDTO.getContent();
            id = taskService.addTask(quadrantId, content);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS(id);
    }

    @PostMapping("/{option}/remove")
    @ApiOperation(("删除一个任务"))
    public SystemJsonResponse removeTask(@PathVariable("option") @NonNull @ApiParam("任务选项（0:action, 1:P1, 2:P2）") Integer option,
                                         @RequestBody OkrTaskRemoveDTO okrTaskRemoveDTO) {
        // 检查
        okrTaskRemoveDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        Long taskId = okrTaskRemoveDTO.getId();
        // 选择服务
        OkrOperateService okrOperateService = okrServiceSelector.select(okrTaskRemoveDTO.getScene());
        TaskService taskService = taskServiceSelector.select(option);
        // 检测身份
        Long quadrantId = taskService.getTaskQuadrantId(taskId);
        Long coreId = taskService.getTaskCoreId(quadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            taskService.removeTask(taskId);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/{option}/update")
    @ApiOperation("更新一条任务")
    public SystemJsonResponse updateTask(@PathVariable("option") @NonNull @ApiParam("任务选项（0:Action, 1:P1, 2:P2）") Integer option,
                                         @RequestBody OkrTaskUpdateDTO okrTaskUpdateDTO) {
        // 检查
        okrTaskUpdateDTO.validate();
        TaskUpdateDTO taskUpdateDTO = okrTaskUpdateDTO.getTaskUpdateDTO();
        taskUpdateDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        // 选择服务
        OkrOperateService okrOperateService = okrServiceSelector.select(okrTaskUpdateDTO.getScene());
        TaskService taskService = taskServiceSelector.select(option);
        Long taskId = taskUpdateDTO.getId();
        // 检测身份
        Long quadrantId = taskService.getTaskQuadrantId(taskId);
        Long coreId = taskService.getTaskCoreId(quadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            String content = taskUpdateDTO.getContent();
            Boolean isCompleted = taskUpdateDTO.getIsCompleted();
            taskService.updateTask(taskId, content, isCompleted);
            // 开启一个异步线程
            IOThreadPool.submit(() -> {
                TermAchievementService termAchievementService = termAchievementServiceSelector.select(option);
                termAchievementService.issueTermAchievement(userId, isCompleted);
            });
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }


}
