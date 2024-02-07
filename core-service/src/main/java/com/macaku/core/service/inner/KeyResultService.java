package com.macaku.core.service.inner;

import com.macaku.core.domain.po.inner.KeyResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 马拉圈
* @description 针对表【key_result(关键结果表)】的数据库操作Service
* @createDate 2024-01-20 02:24:49
*/
public interface KeyResultService extends IService<KeyResult> {

    Long addResultService(KeyResult keyResult);

    void updateProbability(KeyResult keyResult);

    Long getFirstQuadrantId(Long id);

}
