package com.macaku.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.core.domain.po.inner.StatusFlag;
import com.macaku.core.service.StatusFlagService;
import com.macaku.core.mapper.StatusFlagMapper;
import org.springframework.stereotype.Service;

/**
* @author 马拉圈
* @description 针对表【status_flag(指标表)】的数据库操作Service实现
* @createDate 2024-01-20 02:24:49
*/
@Service
public class StatusFlagServiceImpl extends ServiceImpl<StatusFlagMapper, StatusFlag>
    implements StatusFlagService{

}




