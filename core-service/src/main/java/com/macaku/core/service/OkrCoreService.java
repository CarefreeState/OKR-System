package com.macaku.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macaku.core.domain.po.OkrCore;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
* @author 马拉圈
* @description 针对表【okr_core(OKR 内核表)】的数据库操作Service
* @createDate 2024-01-19 21:19:05
*/
public interface OkrCoreService extends IService<OkrCore> {
    void test();

    @Transactional
    Optional<Long> createOkrCore();

}
