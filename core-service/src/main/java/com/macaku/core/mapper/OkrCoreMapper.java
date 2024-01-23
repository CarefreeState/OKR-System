package com.macaku.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macaku.core.domain.po.OkrCore;
import com.macaku.core.domain.po.event.DeadlineEvent;

import java.util.List;

/**
 * @author 马拉圈
 * @description 针对表【okr_core(OKR 内核表)】的数据库操作Mapper
 * @createDate 2024-01-19 21:19:05
 * @Entity com.macaku.core.domain.po.OkrCore
 */
public interface OkrCoreMapper extends BaseMapper<OkrCore> {

    List<DeadlineEvent> getDeadlineEvents();

}



