package com.macaku.core.service.impl.inner;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

    private static final Integer TYPE = 1;

    private PriorityNumberOneMapper priorityNumberOneMapper = SpringUtil.getBean(PriorityNumberOneMapper.class);

    @Override
    public boolean match(Integer type) {
        return TYPE.equals(type);
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

}



