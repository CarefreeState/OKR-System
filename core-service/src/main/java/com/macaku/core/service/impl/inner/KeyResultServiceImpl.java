package com.macaku.core.service.impl.inner;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.core.domain.po.inner.KeyResult;
import com.macaku.core.service.inner.KeyResultService;
import com.macaku.core.mapper.inner.KeyResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* @author 马拉圈
* @description 针对表【key_result(关键结果表)】的数据库操作Service实现
* @createDate 2024-01-20 02:24:49
*/
@Service
@Slf4j
public class KeyResultServiceImpl extends ServiceImpl<KeyResultMapper, KeyResult>
    implements KeyResultService{

    @Override
    public void addResultService(KeyResult keyResult) {
        // 1. 提取需要的数据
        KeyResult newKeyResult = new KeyResult();
        newKeyResult.setFirstQuadrantId(keyResult.getFirstQuadrantId());
        newKeyResult.setContent(keyResult.getContent());
        newKeyResult.setProbability(keyResult.getProbability());
        // 插入
        this.save(newKeyResult);
        log.info("新增关键结果： key result id : {}", newKeyResult.getId());
    }
}




