package com.macaku.center.controller.teampersonal;

import com.macaku.center.domain.vo.TeamPersonalOkrVO;
import com.macaku.center.service.TeamPersonalOkrService;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.user.domain.po.User;
import com.macaku.user.util.UserRecordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 22:57
 */
@RestController
@Api(tags = "团队个人 OKR 测试接口")
@RequestMapping("/teampersonal")
@RequiredArgsConstructor
public class TeamPersonalOkrController {

    private final TeamPersonalOkrService teamPersonalOkrService;

    @GetMapping("/list")
    @ApiOperation("获取团队个人 OKR 列表")
    public SystemJsonResponse<List<TeamPersonalOkrVO>> getTeamOkrs(HttpServletRequest request) {
        // 获取当前登录的用户
        User user = UserRecordUtil.getUserRecord(request);
        // 调用方法
        List<TeamPersonalOkrVO> teamPersonalOkrVOS = teamPersonalOkrService.getTeamPersonalOkrList(user);
        return SystemJsonResponse.SYSTEM_SUCCESS(teamPersonalOkrVOS);
    }

}
