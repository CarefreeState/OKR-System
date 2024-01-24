package com.macaku.core.service.impl.inner;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.core.domain.po.inner.StatusFlag;
import com.macaku.core.service.inner.StatusFlagService;
import com.macaku.core.mapper.inner.StatusFlagMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* @author 马拉圈
* @description 针对表【status_flag(指标表)】的数据库操作Service实现
* @createDate 2024-01-20 02:24:49
*/
@Service
@Slf4j
public class StatusFlagServiceImpl extends ServiceImpl<StatusFlagMapper, StatusFlag>
    implements StatusFlagService{

    @Override
    public void addStatusFlag(StatusFlag statusFlag) {
        StatusFlag newFlag = new StatusFlag();
        String color = statusFlag.getColor();
        newFlag.setColor(color);
        String label = statusFlag.getLabel();
        newFlag.setLabel(label);
        Long fourthQuadrantId = statusFlag.getFourthQuadrantId();
        newFlag.setFourthQuadrantId(fourthQuadrantId);
        this.save(newFlag);
        log.info("成功为第四象限 {} 新增一条指标 {} -- {}", fourthQuadrantId, label, color);
    }

    @Override
    public void removeStatusFlag(Long id) {
        // 逻辑删除
        boolean ret = this.lambdaUpdate().eq(StatusFlag::getId, id).remove();
        if(Boolean.TRUE.equals(ret)) {
            log.info("成功为第四象限删除一条指标 {}", id);
        }
    }

    @Override
    public void updateStatusFlag(StatusFlag statusFlag) {
        // 提取数据
        StatusFlag updateFlag = new StatusFlag();
        updateFlag.setId(statusFlag.getId());
        updateFlag.setColor(statusFlag.getColor());
        updateFlag.setLabel(statusFlag.getLabel());
        // 修改
        this.updateById(updateFlag);
        log.info("成功为第四象限修改一条指标为 -> {}", updateFlag);
    }
}




