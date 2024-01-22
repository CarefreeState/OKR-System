package com.macaku.core.service.impl.inner;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.core.domain.po.inner.Action;
import com.macaku.core.mapper.inner.ActionMapper;
import com.macaku.core.service.TaskService;
import com.macaku.core.service.inner.ActionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* @author 马拉圈
* @description 针对表【action(行动表)】的数据库操作Service实现
* @createDate 2024-01-20 02:24:49
*/
@Service
@Slf4j
public class ActionServiceImpl extends ServiceImpl<ActionMapper, Action>
    implements ActionService, TaskService {

    private static final Integer TYPE = 0;

    private ActionMapper actionMapper = SpringUtil.getBean(ActionMapper.class);


    @Override
    public boolean match(Integer type) {
        return TYPE.equals(type);
    }

    @Override
    public void addTask(Long quadrantId, String content) {
        // 构造对象
        Action action = new Action();
        action.setContent(content);
        action.setThirdQuadrantId(quadrantId);
        // 插入
        actionMapper.insert(action);
        log.info("为第三象限 {} 插入一条行动 {} -- {}", quadrantId, action.getId(), content);
    }

    @Override
    public void removeTask(Long id) {
        // 删除
        boolean ret = Db.lambdaUpdate(Action.class)
                .eq(Action::getId, id)
                .remove();
        if(ret) {
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

}




