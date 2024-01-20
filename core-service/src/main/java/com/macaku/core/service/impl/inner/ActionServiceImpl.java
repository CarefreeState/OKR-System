package com.macaku.core.service.impl.inner;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.core.domain.po.inner.Action;
import com.macaku.core.service.inner.ActionService;
import com.macaku.core.mapper.inner.ActionMapper;
import org.springframework.stereotype.Service;

/**
* @author 马拉圈
* @description 针对表【action(行动表)】的数据库操作Service实现
* @createDate 2024-01-20 02:24:49
*/
@Service
public class ActionServiceImpl extends ServiceImpl<ActionMapper, Action>
    implements ActionService{

}




