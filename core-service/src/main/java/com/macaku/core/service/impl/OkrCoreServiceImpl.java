package com.macaku.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.domain.po.OkrCore;
import com.macaku.core.service.OkrCoreService;
import com.macaku.core.mapper.OkrCoreMapper;
import org.springframework.stereotype.Service;

/**
* @author 马拉圈
* @description 针对表【okr_core(OKR 内核表)】的数据库操作Service实现
* @createDate 2024-01-19 21:19:05
*/
@Service
public class OkrCoreServiceImpl extends ServiceImpl<OkrCoreMapper, OkrCore>
    implements OkrCoreService{

    public void test() {
        throw  new GlobalServiceException();
    }

}




