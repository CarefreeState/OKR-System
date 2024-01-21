package com.macaku.core.service.inner;

import com.macaku.core.domain.po.inner.StatusFlag;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 马拉圈
* @description 针对表【status_flag(指标表)】的数据库操作Service
* @createDate 2024-01-20 02:24:49
*/
public interface StatusFlagService extends IService<StatusFlag> {

    void addStatusFlag(StatusFlag statusFlag);
}
