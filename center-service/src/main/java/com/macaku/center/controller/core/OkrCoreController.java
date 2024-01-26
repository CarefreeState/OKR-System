package com.macaku.center.controller.core;

import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.OkrOperateDTO;
import com.macaku.center.domain.dto.unify.OkrSearchDTO;
import com.macaku.center.service.OkrOperateService;
import com.macaku.center.service.TeamPersonalOkrService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.core.domain.vo.OkrCoreVO;
import com.macaku.core.service.OkrCoreService;
import com.macaku.user.domain.po.User;
import com.macaku.user.util.UserRecordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-19
 * Time: 23:57
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/core")
@Api(tags = "OKR 内核")
public class OkrCoreController {

    private final OkrCoreService okrCoreService;

    private final OkrServiceSelector okrServiceSelector;

    private final TeamPersonalOkrService teamPersonalOkrService;

    @GetMapping("/create")
    @ApiOperation("创建一个core")
    public SystemJsonResponse createOkr(HttpServletRequest request,
                                              OkrOperateDTO okrOperateDTO) {
        // 检测
        okrOperateDTO.validate();
        User user = UserRecordUtil.getUserRecord(request);
        OkrOperateService okrOperateService = okrServiceSelector.select(okrOperateDTO.getScene());
        okrOperateService.createOkrCore(user, okrOperateDTO);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @GetMapping("/search")
    @ApiOperation("查看一个OKR内核")
    public SystemJsonResponse<OkrCoreVO> searchOkrCore(HttpServletRequest request,
                                                       OkrSearchDTO okrSearchDTO) {
        okrSearchDTO.validate();
        User user = UserRecordUtil.getUserRecord(request);
        OkrOperateService okrOperateService = okrServiceSelector.select(okrSearchDTO.getScene());
        OkrCoreVO okrCoreVO = okrOperateService.selectAllOfCore(user, okrSearchDTO.getCoreId());
        return SystemJsonResponse.SYSTEM_SUCCESS(okrCoreVO);
    }

    @PostMapping("/celebrate")
    @ApiOperation("确定庆祝日")
    public SystemJsonResponse confirmCelebrateDay(HttpServletRequest request,
                                                  OkrSearchDTO okrSearchDTO,
                                                  @RequestParam("celebrateDay") @NonNull @ApiParam("庆祝日（星期）") Integer celebrateDay) {
        if(celebrateDay.compareTo(1) < 0 || celebrateDay.compareTo(7) > 0) {
            throw new GlobalServiceException(GlobalServiceStatusCode.INVALID_CELEBRATE_DAY);
        }
        okrSearchDTO.validate();
        User user = UserRecordUtil.getUserRecord(request);
        Long coreId = okrSearchDTO.getCoreId();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrSearchDTO.getScene());
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)){
            okrCoreService.confirmCelebrateDate(coreId, celebrateDay);
            log.info("成功为 OKR {} 确定庆祝日 星期{}", coreId, celebrateDay);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/summary/{id}")
    @ApiOperation("总结 OKR")
    public SystemJsonResponse summaryOKR(@PathVariable("id") @NonNull @ApiParam("OKR 内核 ID") Long id,
                                         @RequestParam("summary") @NonNull @ApiParam("总结") String summary,
                                         @RequestParam("degree") @NonNull @ApiParam("完成度") Integer degree) {
        if(!StringUtils.hasText(summary)) {
            throw new GlobalServiceException("总结内容为空", GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        if(Objects.isNull(degree)) {
            throw new GlobalServiceException("完成度缺失", GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        okrCoreService.summaryOKR(id, summary, degree);
        log.info("成功为 OKR {} 总结 {} 完成度 {}%", id, summary, degree);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }
    @PostMapping("/complete/{id}")
    @ApiOperation("结束 OKR")
    public SystemJsonResponse complete(@PathVariable("id") @NonNull @ApiParam("OKR 内核 ID") Long id) {
        okrCoreService.complete(id);
        log.info("成功结束 OKR {}", id);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
