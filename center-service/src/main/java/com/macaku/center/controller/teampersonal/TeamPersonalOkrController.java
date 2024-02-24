package com.macaku.center.controller.teampersonal;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.center.domain.po.TeamPersonalOkr;
import com.macaku.center.domain.vo.TeamMemberVO;
import com.macaku.center.domain.vo.TeamPersonalOkrVO;
import com.macaku.center.service.MemberService;
import com.macaku.center.service.TeamOkrService;
import com.macaku.center.service.TeamPersonalOkrService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.user.domain.po.User;
import com.macaku.user.util.UserRecordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    private final MemberService memberService;

    private final TeamPersonalOkrService teamPersonalOkrService;

    private final TeamOkrService teamOkrService;

    @GetMapping("/list")
    @ApiOperation("获取团队个人 OKR 列表")
    public SystemJsonResponse<List<TeamPersonalOkrVO>> getTeamOkrs(HttpServletRequest request) {
        // 获取当前登录的用户
        User user = UserRecordUtil.getUserRecord();
        // 调用方法
        List<TeamPersonalOkrVO> teamPersonalOkrVOS = teamPersonalOkrService.getTeamPersonalOkrList(user);
        return SystemJsonResponse.SYSTEM_SUCCESS(teamPersonalOkrVOS);
    }

    @PostMapping("/members/{teamId}")
    @ApiOperation("获取团队成员列表")
    public SystemJsonResponse<List<TeamMemberVO>> getTeamMember(HttpServletRequest request,
                                                                @PathVariable("teamId") @NonNull @ApiParam("团队 OKR ID") Long teamId) {
        // 获取当前登录用户
        User user = UserRecordUtil.getUserRecord();
        // 判断是不是团队成员
        memberService.checkExistsInTeam(teamId, user.getId());
        // 查询
        List<TeamMemberVO> teamMembers = teamPersonalOkrService.getTeamMembers(teamId);
        return SystemJsonResponse.SYSTEM_SUCCESS(teamMembers);
    }

    @GetMapping("/remove/{id}")
    @ApiOperation("移除成员")
    public SystemJsonResponse removeMember(HttpServletRequest request,
                                           @PathVariable("id") @NonNull @ApiParam("团队个人 OKR ID") Long id) {
        // 查询团队个人 Okr
        TeamPersonalOkr teamPersonalOkr = Db.lambdaQuery(TeamPersonalOkr.class)
                .eq(TeamPersonalOkr::getId, id)
                .oneOpt()
                .orElseThrow(() ->
                        new GlobalServiceException(GlobalServiceStatusCode.MEMBER_NOT_EXISTS)
                );
        Long teamId = teamPersonalOkr.getTeamId();
        Long useId = teamPersonalOkr.getUserId();
        // 获取当前登录用户
        User user = UserRecordUtil.getUserRecord();
        // 判断是不是团队成员
        memberService.checkExistsInTeam(teamId, useId);
        teamOkrService.checkManager(teamId, user.getId());
        // 尝试删除
        memberService.removeMember(teamId, id, useId);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
