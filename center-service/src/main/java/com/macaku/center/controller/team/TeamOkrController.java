package com.macaku.center.controller.team;

import com.macaku.center.domain.dto.team.GrantDTO;
import com.macaku.center.domain.dto.team.TeamUpdateDTO;
import com.macaku.center.domain.po.TeamOkr;
import com.macaku.center.domain.vo.TeamOkrStatisticVO;
import com.macaku.center.domain.vo.TeamOkrVO;
import com.macaku.center.service.MemberService;
import com.macaku.center.service.TeamOkrService;
import com.macaku.center.util.TeamOkrUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.qrcode.component.InviteQRCodeServiceFactory;
import com.macaku.qrcode.service.OkrQRCodeService;
import com.macaku.user.domain.po.User;
import com.macaku.user.util.UserRecordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 22:19
 */
@RestController
@Api(tags = "团队 OKR 测试接口")
@RequestMapping("/team")
@RequiredArgsConstructor
@Slf4j
public class TeamOkrController {

    private final TeamOkrService teamOkrService;

    private final MemberService memberService;

    private final OkrQRCodeService okrQRCodeService;

    @GetMapping("/list")
    @ApiOperation("获取管理的团队 OKR 列表")
    public SystemJsonResponse<List<TeamOkrVO>> getTeamOkrs() {
        // 获取当前登录的用户
        User user = UserRecordUtil.getUserRecord();
        // 调用方法
        List<TeamOkrVO> teamOkrVOS = teamOkrService.getTeamOkrList(user);
        return SystemJsonResponse.SYSTEM_SUCCESS(teamOkrVOS);
    }

    @PostMapping("/rename")
    @ApiOperation("修改团队的名字")
    public SystemJsonResponse updateName(@RequestBody TeamUpdateDTO teamUpdateDTO) {
        // 获取当前登录用户
        User user = UserRecordUtil.getUserRecord();
        // 判断是不是管理员
        Long managerId = user.getId();
        teamUpdateDTO.validate();
        Long teamId = teamUpdateDTO.getId();
        String teamName = teamUpdateDTO.getTeamName();
        // 检测管理者身份
        teamOkrService.checkManager(teamId, managerId);
        // 更新
        TeamOkr updateTeam = new TeamOkr();
        updateTeam.setId(teamId);
        updateTeam.setTeamName(teamName);
        teamOkrService.lambdaUpdate().eq(TeamOkr::getId, teamId).update(updateTeam);
        // 删除缓存
        teamOkrService.deleteTeamNameCache(teamId);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/tree/{id}")
    @ApiOperation("获取一个团队所在的树")
    public SystemJsonResponse<List<TeamOkrStatisticVO>> getCompleteTree(@PathVariable("id") @NonNull @ApiParam("团队 OKR ID") Long id) {
        // 获取当前团队的祖先 ID
        Long rootId = TeamOkrUtil.getTeamRootId(id);
        User user = UserRecordUtil.getUserRecord();
        Long userId = user.getId();
        // 判断是否是其中的成员
        memberService.checkExistsInTeam(rootId, userId);
        // 获取根团队的所有孩子节点
        List<Long> ids = TeamOkrUtil.getChildIds(rootId);
        if(ids.isEmpty()) {
            throw new GlobalServiceException(GlobalServiceStatusCode.TEAM_NOT_EXISTS);
        }
        // 计算完成度
        List<TeamOkrStatisticVO> statisticVOS = teamOkrService.countCompletionRate(ids);
        log.info("查询团队 {} 在 根团队 {} 中， 树的总节点数为 {}", id, rootId, statisticVOS.size());
        return SystemJsonResponse.SYSTEM_SUCCESS(statisticVOS);
    }

    @PostMapping("/tree/child/{id}")
    @ApiOperation("获取一个团队的子树")
    public SystemJsonResponse<List<TeamOkrStatisticVO>> getChildTree(@PathVariable("id") @NonNull @ApiParam("团队 OKR ID") Long id) {
        // 获取当前团队的祖先 ID
        User user = UserRecordUtil.getUserRecord();
        Long userId = user.getId();
        // 判断是否是其中的成员
        memberService.checkExistsInTeam(id, userId);
        // 获取根团队的所有孩子节点
        List<Long> ids = TeamOkrUtil.getChildIds(id);
        if(ids.isEmpty()) {
            throw new GlobalServiceException(GlobalServiceStatusCode.TEAM_NOT_EXISTS);
        }
        log.info("查询团队 {} 的子树， 树的总节点数为 {}", id, ids.size());
        // 计算完成度
        List<TeamOkrStatisticVO> statisticVOS = teamOkrService.countCompletionRate(ids);
        return SystemJsonResponse.SYSTEM_SUCCESS(statisticVOS);
    }

    @PostMapping("/grant")
    @ApiOperation("给成员授权，使其可以扩展一个子团队")
    public SystemJsonResponse<Map<String, Object>> grantTeamForMember(@RequestBody GrantDTO grantDTO) {
        // 检测
        grantDTO.validate();
        // 获取当前管理员 ID
        User user = UserRecordUtil.getUserRecord();
        Long managerId = user.getId();
        Long userId = grantDTO.getUserId();
        Long teamId = grantDTO.getTeamId();
        String teamName = grantDTO.getTeamName();
        Map<String, Object> ret = teamOkrService.grantTeamForMember(teamId, managerId, userId, teamName);
        return SystemJsonResponse.SYSTEM_SUCCESS(ret);
    }

    @PostMapping("/qrcode/{teamId}")
    @ApiOperation("获取邀请码")
    public SystemJsonResponse<String> getQRCode(@PathVariable("teamId") @NonNull @ApiParam("团队 OKR ID") Long teamId,
                                                @RequestParam(value = "type", required = false, defaultValue = InviteQRCodeServiceFactory.WX_TYPE) @ApiParam("邀请码类型") String type) {
        // 检测
        User user = UserRecordUtil.getUserRecord();
        Long managerId = user.getId();
        // 检测管理者身份
        teamOkrService.checkManager(teamId, managerId);
        // 进行操作
        String path = okrQRCodeService.getInviteQRCodeLock(teamId, TeamOkrUtil.getTeamName(teamId), type);
        return SystemJsonResponse.SYSTEM_SUCCESS(path);
    }

    @GetMapping("/describe/{teamId}")
    @ApiOperation("了解团队")
    public SystemJsonResponse<String> getTeamName(@PathVariable("teamId") @NonNull @ApiParam("团队 OKR ID") Long teamId) throws IOException {
        String teamName = TeamOkrUtil.getTeamName(teamId);
        return SystemJsonResponse.SYSTEM_SUCCESS(teamName);
    }
}
