package com.macaku.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.domain.po.OkrCore;
import com.macaku.core.domain.po.inner.PriorityNumberOne;
import com.macaku.core.domain.po.inner.PriorityNumberTwo;
import com.macaku.core.domain.po.quadrant.FirstQuadrant;
import com.macaku.core.domain.po.quadrant.FourthQuadrant;
import com.macaku.core.domain.po.quadrant.SecondQuadrant;
import com.macaku.core.domain.po.quadrant.ThirdQuadrant;
import com.macaku.core.mapper.OkrCoreMapper;
import com.macaku.core.service.*;
import com.macaku.core.service.inner.PriorityNumberOneService;
import com.macaku.core.service.inner.PriorityNumberTwoService;
import com.macaku.core.service.quadrant.FirstQuadrantService;
import com.macaku.core.service.quadrant.FourthQuadrantService;
import com.macaku.core.service.quadrant.SecondQuadrantService;
import com.macaku.core.service.quadrant.ThirdQuadrantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
* @author 马拉圈
* @description 针对表【okr_core(OKR 内核表)】的数据库操作Service实现
* @createDate 2024-01-19 21:19:05
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class OkrCoreServiceImpl extends ServiceImpl<OkrCoreMapper, OkrCore>
    implements OkrCoreService{


    private final FirstQuadrantService firstQuadrantService;

    private final SecondQuadrantService secondQuadrantService;

    private final ThirdQuadrantService thirdQuadrantService;

    private final FourthQuadrantService fourthQuadrantService;

    private final PriorityNumberOneService priorityNumberOneService;

    private final PriorityNumberTwoService priorityNumberTwoService;

    public void test() {
        throw  new GlobalServiceException();
    }

    @Override
    public Optional<Long> createOkrCore() {
        // 1. 创建一个内核
        OkrCore okrCore = new OkrCore();
        this.save(okrCore);
        Long coreID = okrCore.getId();
        log.info("新增 OKR 内核：  okr core id : {}", coreID);
        // 2. 创建一二三四象限（datetime对象）
        // 第一象限
        FirstQuadrant firstQuadrant = new FirstQuadrant();
        firstQuadrant.setCoreId(coreID);
        firstQuadrantService.save(firstQuadrant);
        // 第二象限
        SecondQuadrant secondQuadrant = new SecondQuadrant();
        secondQuadrant.setCoreId(coreID);
        secondQuadrantService.save(secondQuadrant);
        Long secondQuadrantID = secondQuadrant.getId();
        PriorityNumberOne priorityNumberOne = new PriorityNumberOne();
        priorityNumberOne.setSecondQuadrantId(secondQuadrantID);
        priorityNumberOneService.save(priorityNumberOne);
        PriorityNumberTwo priorityNumberTwo = new PriorityNumberTwo();
        priorityNumberTwo.setSecondQuadrantId(secondQuadrantID);
        priorityNumberTwoService.save(priorityNumberTwo);
        // 第三象限
        ThirdQuadrant thirdQuadrant = new ThirdQuadrant();
        thirdQuadrant.setCoreId(coreID);
        thirdQuadrantService.save(thirdQuadrant);
        // 第四象限
        FourthQuadrant fourthQuadrant = new FourthQuadrant();
        fourthQuadrant.setCoreId(coreID);
        fourthQuadrantService.save(fourthQuadrant);
        // 3. 返回
        return Optional.ofNullable(coreID);
    }

}




