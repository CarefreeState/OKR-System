package com.macaku.core.service.impl.inner;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.core.component.TaskServiceSelector;
import com.macaku.core.domain.po.inner.PriorityNumberTwo;
import com.macaku.core.mapper.inner.PriorityNumberTwoMapper;
import com.macaku.core.service.TaskService;
import com.macaku.core.service.inner.PriorityNumberTwoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* @author 马拉圈
* @description 针对表【priority_number_two(Priority2 表)】的数据库操作Service实现
* @createDate 2024-01-20 02:24:49
*/
@Service
@Slf4j
public class PriorityNumberTwoServiceImpl extends ServiceImpl<PriorityNumberTwoMapper, PriorityNumberTwo>
    implements PriorityNumberTwoService, TaskService {

    private static final Integer OPTION = TaskServiceSelector.PRIORITY_TWO_OPTION;

    private final PriorityNumberTwoMapper priorityNumberTwoMapper = SpringUtil.getBean(PriorityNumberTwoMapper.class);

    @Override
    public boolean match(Integer option) {
        return OPTION.equals(option);
    }

    @Override
    public void addTask(Long quadrantId, String content) {
//         构造对象
        PriorityNumberTwo priorityNumberTwo = new PriorityNumberTwo();
        priorityNumberTwo.setContent(content);
        priorityNumberTwo.setSecondQuadrantId(quadrantId);
//         插入
        priorityNumberTwoMapper.insert(priorityNumberTwo);
        log.info("为第二象限 {} 插入一条 Priority2 任务 {} -- {}", quadrantId, priorityNumberTwo.getId(), content);
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
    public void updateTask(Long id, String content, Boolean isCompleted) {
        PriorityNumberTwo updatePriorityNumberTwo = new PriorityNumberTwo();
        updatePriorityNumberTwo.setId(id);
        updatePriorityNumberTwo.setContent(content);
        updatePriorityNumberTwo.setIsCompleted(isCompleted);
        priorityNumberTwoMapper.updateById(updatePriorityNumberTwo);
        log.info("成功更新一条P2 {} {} {}", id, content, isCompleted);
    }
}




