package com.macaku.core.service;

import com.macaku.core.domain.po.OkrCore;
import com.baomidou.mybatisplus.extension.service.IService;
import com.macaku.core.domain.po.vo.OkrCoreVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
* @author 马拉圈
* @description 针对表【okr_core(OKR 内核表)】的数据库操作Service
* @createDate 2024-01-22 13:42:11
*/
public interface OkrCoreService extends IService<OkrCore> {
    @Transactional
    Optional<Long> createOkrCore();

    OkrCoreVO searchOkrCore(Long id);

    void confirmCelebrateDate(Long id, Integer celebrateDay);

    void summaryOKR(Long id, String summary);

    void complete(Long id);

}
