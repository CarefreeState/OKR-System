package com.macaku.core.service.impl.inner;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.domain.po.inner.PriorityNumberOne;
import com.macaku.core.mapper.inner.PriorityNumberOneMapper;
import com.macaku.core.service.TaskService;
import com.macaku.core.service.inner.PriorityNumberOneService;
import com.macaku.core.service.quadrant.SecondQuadrantService;
import com.macaku.redis.repository.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
* @author 马拉圈
* @description 针对表【priority_number_one(Priority1 表)】的数据库操作Service实现
* @createDate 2024-01-20 02:24:49
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class PriorityNumberOneServiceImpl extends ServiceImpl<PriorityNumberOneMapper, PriorityNumberOne>
    implements PriorityNumberOneService, TaskService {

    private final static String P1_QUADRANT_MAP = "p1QuadrantMap:";

    private final static Long P1_QUADRANT_TTL = 6L;

    private final static TimeUnit P1_QUADRANT_UNIT = TimeUnit.HOURS;

    private final PriorityNumberOneMapper priorityNumberOneMapper;

    private final RedisCache redisCache;

    private final SecondQuadrantService secondQuadrantService;

    @Override
    public Long addTask(Long quadrantId, String content) {
        // 构造对象
        PriorityNumberOne priorityNumberOne = new PriorityNumberOne();
        priorityNumberOne.setContent(content);
        priorityNumberOne.setSecondQuadrantId(quadrantId);
        // 插入
        priorityNumberOneMapper.insert(priorityNumberOne);
        Long id = priorityNumberOne.getId();
        log.info("为第二象限 {} 插入一条 Priority1 任务 {} -- {}", quadrantId, id, content);
        return id;
    }

    @Override
    public void removeTask(Long id) {
        // 删除
        boolean ret = Db.lambdaUpdate(PriorityNumberOne.class)
                .eq(PriorityNumberOne::getId, id)
                .remove();
        if(Boolean.TRUE.equals(ret)) {
            log.info("成功为第二象限删除一条 P1 {}", id);
        }
    }

    @Override
    public Boolean updateTask(Long id, String content, Boolean isCompleted) {
        Boolean oldCompleted = Optional.ofNullable(priorityNumberOneMapper.selectById(id)).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.TASK_NOT_EXISTS)).getIsCompleted();
        PriorityNumberOne updatePriorityNumberOne = new PriorityNumberOne();
        updatePriorityNumberOne.setId(id);
        updatePriorityNumberOne.setContent(content);
        updatePriorityNumberOne.setIsCompleted(isCompleted);
        priorityNumberOneMapper.updateById(updatePriorityNumberOne);
        log.info("成功更新一条 P1 {} {} {}", id, content, isCompleted);
        return oldCompleted;
    }

    @Override
    public Long getTaskQuadrantId(Long id) {
        String redisKey = P1_QUADRANT_MAP + id;
        return (Long) redisCache.getCacheObject(redisKey).orElseGet(() -> {
            // 查询数据库
            Long secondQuadrantId = this.lambdaQuery()
                    .eq(PriorityNumberOne::getId, id)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.TASK_NOT_EXISTS)
                    ).getSecondQuadrantId();
            redisCache.setCacheObject(redisKey, secondQuadrantId, P1_QUADRANT_TTL, P1_QUADRANT_UNIT);
            return secondQuadrantId;
        });
    }

    @Override
    public Long getTaskCoreId(Long quadrantId) {
        return secondQuadrantService.getSecondQuadrantCoreId(quadrantId);
    }

}




