package com.macaku.core.service.impl.inner;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.core.component.TaskServiceSelector;
import com.macaku.core.domain.po.inner.PriorityNumberOne;
import com.macaku.core.mapper.inner.PriorityNumberOneMapper;
import com.macaku.core.service.TaskService;
import com.macaku.core.service.inner.PriorityNumberOneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* @author 马拉圈
* @description 针对表【priority_number_one(Priority1 表)】的数据库操作Service实现
* @createDate 2024-01-20 02:24:49
*/
@Service
@Slf4j
public class PriorityNumberOneServiceImpl extends ServiceImpl<PriorityNumberOneMapper, PriorityNumberOne>
    implements PriorityNumberOneService, TaskService {

    private static final Integer OPTION = TaskServiceSelector.PRIORITY_ONE_OPTION;

    private final PriorityNumberOneMapper priorityNumberOneMapper = SpringUtil.getBean(PriorityNumberOneMapper.class);

    @Override
    public boolean match(Integer option) {
        return OPTION.equals(option);
    }

    @Override
    public void addTask(Long quadrantId, String content) {
        // 构造对象
        PriorityNumberOne priorityNumberOne = new PriorityNumberOne();
        priorityNumberOne.setContent(content);
        priorityNumberOne.setSecondQuadrantId(quadrantId);
        // 插入
        priorityNumberOneMapper.insert(priorityNumberOne);
        log.info("为第二象限 {} 插入一条 Priority1 任务 {} -- {}", quadrantId, priorityNumberOne.getId(), content);
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
    public void updateTask(Long id, String content, Boolean isCompleted) {
        PriorityNumberOne updatePriorityNumberOne = new PriorityNumberOne();
        updatePriorityNumberOne.setId(id);
        updatePriorityNumberOne.setContent(content);
        updatePriorityNumberOne.setIsCompleted(isCompleted);
        priorityNumberOneMapper.updateById(updatePriorityNumberOne);
        log.info("成功更新一条P1 {} {} {}", id, content, isCompleted);
    }

}




