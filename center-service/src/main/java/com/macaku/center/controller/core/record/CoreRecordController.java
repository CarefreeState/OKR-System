package com.macaku.center.controller.core.record;

import cn.hutool.core.bean.BeanUtil;
import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.OkrCoreDTO;
import com.macaku.center.service.OkrOperateService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.corerecord.domain.po.ext.Record;
import com.macaku.corerecord.domain.vo.DayRecordVO;
import com.macaku.corerecord.service.DayRecordService;
import com.macaku.user.domain.po.User;
import com.macaku.user.util.UserRecordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-21
 * Time: 12:58
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/corerecord")
@Api(tags = "OKR 记录")
public class CoreRecordController {

    private final OkrServiceSelector okrServiceSelector;

    private final DayRecordService dayRecordService;

    @PostMapping("/search/dayrecord")
    @ApiOperation("查看一个 OKR 的日记录")
    public SystemJsonResponse<List<DayRecordVO>> searchOkrCoreDayRecord(@RequestBody OkrCoreDTO okrCoreDTO) {
        okrCoreDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        Long coreId = okrCoreDTO.getCoreId();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrCoreDTO.getScene());
        if(Boolean.TRUE.equals(okrOperateService.canVisit(user, coreId))) {
            List<Record> dayRecords = dayRecordService.getRecords(coreId);
            return SystemJsonResponse.SYSTEM_SUCCESS(BeanUtil.copyToList(dayRecords, DayRecordVO.class));
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
    }
}
