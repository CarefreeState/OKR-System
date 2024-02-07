package com.macaku.core.service.impl.inner;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import com.macaku.core.component.TaskServiceSelector;
import com.macaku.core.domain.po.inner.Action;
import com.macaku.core.mapper.inner.ActionMapper;
import com.macaku.core.service.TaskService;
import com.macaku.core.service.inner.ActionService;
import com.macaku.core.service.quadrant.ThirdQuadrantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
* @author 马拉圈
* @description 针对表【action(行动表)】的数据库操作Service实现
* @createDate 2024-01-20 02:24:49
*/
@Service
@Slf4j
public class ActionServiceImpl extends ServiceImpl<ActionMapper, Action>
    implements ActionService, TaskService {

    private static final Integer OPTION = TaskServiceSelector.ACTION_OPTION;

    private final static String ACTION_QUADRANT_MAP = "actionQuadrantMap:";

    private final static Long ACTION_QUADRANT_TTL = 6L;

    private final static TimeUnit ACTION_QUADRANT_UNIT = TimeUnit.HOURS;

    private final ActionMapper actionMapper = SpringUtil.getBean(ActionMapper.class);

    private final RedisCache redisCache = SpringUtil.getBean(RedisCache.class);

    private final ThirdQuadrantService thirdQuadrantService = SpringUtil.getBean(ThirdQuadrantService.class);

    @Override
    public boolean match(Integer option) {
        return OPTION.equals(option);
    }

    @Override
    public Long addTask(Long quadrantId, String content) {
        // 构造对象
        Action action = new Action();
        action.setContent(content);
        action.setThirdQuadrantId(quadrantId);
        // 插入
        actionMapper.insert(action);
        Long id = action.getId();
        log.info("为第三象限 {} 插入一条行动 {} -- {}", quadrantId, id, content);
        return id;
    }

    @Override
    public void removeTask(Long id) {
        // 删除
        boolean ret = Db.lambdaUpdate(Action.class)
                .eq(Action::getId, id)
                .remove();
        if(Boolean.TRUE.equals(ret)) {
            log.info("成功为第三象限删除一条行动 {}", id);
        }
    }

    @Override
    public void updateTask(Long id, String content, Boolean isCompleted) {
        Action updateAction = new Action();
        updateAction.setId(id);
        updateAction.setContent(content);
        updateAction.setIsCompleted(isCompleted);
        actionMapper.updateById(updateAction);
        log.info("成功更新一条行动 {} {} {}", id, content, isCompleted);
    }

    @Override
    public Long getTaskQuadrantId(Long id) {
        String redisKey = ACTION_QUADRANT_MAP + id;
        return (Long) redisCache.getCacheObject(redisKey).orElseGet(() -> {
            // 查询数据库
            Long thirdQuadrantId = this.lambdaQuery()
                    .eq(Action::getId, id)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.TASK_NOT_EXISTS)
                    ).getThirdQuadrantId();
            redisCache.setCacheObject(redisKey, thirdQuadrantId, ACTION_QUADRANT_TTL, ACTION_QUADRANT_UNIT);
            return thirdQuadrantId;
        });
    }

    @Override
    public Long getTaskCoreId(Long quadrantId) {
        return thirdQuadrantService.getThirdQuadrantCoreId(quadrantId);
    }

}




