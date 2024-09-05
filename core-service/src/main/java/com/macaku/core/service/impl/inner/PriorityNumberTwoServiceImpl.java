package com.macaku.core.service.impl.inner;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.domain.po.inner.PriorityNumberTwo;
import com.macaku.core.mapper.inner.PriorityNumberTwoMapper;
import com.macaku.core.service.TaskService;
import com.macaku.core.service.inner.PriorityNumberTwoService;
import com.macaku.core.service.quadrant.SecondQuadrantService;
import com.macaku.redis.repository.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
* @author 马拉圈
* @description 针对表【priority_number_two(Priority2 表)】的数据库操作Service实现
* @createDate 2024-01-20 02:24:49
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class PriorityNumberTwoServiceImpl extends ServiceImpl<PriorityNumberTwoMapper, PriorityNumberTwo>
    implements PriorityNumberTwoService, TaskService {

    private final static String P2_QUADRANT_MAP = "p2QuadrantMap:";

    private final static Long P2_QUADRANT_TTL = 6L;

    private final static TimeUnit P2_QUADRANT_UNIT = TimeUnit.HOURS;

    private final PriorityNumberTwoMapper priorityNumberTwoMapper;

    private final RedisCache redisCache;

    private final SecondQuadrantService secondQuadrantService;

    @Override
    public Long addTask(Long quadrantId, String content) {
        // 构造对象
        PriorityNumberTwo priorityNumberTwo = new PriorityNumberTwo();
        priorityNumberTwo.setContent(content);
        priorityNumberTwo.setSecondQuadrantId(quadrantId);
        // 插入
        priorityNumberTwoMapper.insert(priorityNumberTwo);
        Long id = priorityNumberTwo.getId();
        log.info("为第二象限 {} 插入一条 Priority2 任务 {} -- {}", quadrantId, id, content);
        return id;
    }

    @Override
    public void removeTask(Long id) {
        // 删除
        boolean ret = Db.lambdaUpdate(PriorityNumberTwo.class)
                .eq(PriorityNumberTwo::getId, id)
                .remove();
        if(Boolean.TRUE.equals(ret)) {
            log.info("成功为第二象限删除一条 P2 {}", id);
        }
    }

    @Override
    public Boolean updateTask(Long id, String content, Boolean isCompleted) {
        Boolean oldCompleted = Optional.ofNullable(priorityNumberTwoMapper.selectById(id)).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.TASK_NOT_EXISTS)).getIsCompleted();
        PriorityNumberTwo updatePriorityNumberTwo = new PriorityNumberTwo();
        updatePriorityNumberTwo.setId(id);
        updatePriorityNumberTwo.setContent(content);
        updatePriorityNumberTwo.setIsCompleted(isCompleted);
        priorityNumberTwoMapper.updateById(updatePriorityNumberTwo);
        log.info("成功更新一条 P2 {} {} {}", id, content, isCompleted);
        return oldCompleted;
    }

    @Override
    public Long getTaskQuadrantId(Long id) {
        String redisKey = P2_QUADRANT_MAP + id;
        return (Long) redisCache.getCacheObject(redisKey).orElseGet(() -> {
            // 查询数据库
            Long secondQuadrantId = this.lambdaQuery()
                    .eq(PriorityNumberTwo::getId, id)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.TASK_NOT_EXISTS)
                    ).getSecondQuadrantId();
            redisCache.setCacheObject(redisKey, secondQuadrantId, P2_QUADRANT_TTL, P2_QUADRANT_UNIT);
            return secondQuadrantId;
        });
    }

    @Override
    public Long getTaskCoreId(Long quadrantId) {
        return secondQuadrantService.getSecondQuadrantCoreId(quadrantId);
    }
}




