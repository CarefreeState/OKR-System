package com.macaku.center.controller.team;

import com.macaku.center.domain.dto.GrantDTO;
import com.macaku.center.domain.po.TeamOkr;
import com.macaku.center.domain.vo.TeamOkrVO;
import com.macaku.center.service.TeamOkrService;
import com.macaku.center.service.TeamPersonalOkrService;
import com.macaku.center.util.TeamOkrUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.user.domain.po.User;
import com.macaku.user.util.UserRecordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    private final RedisCache redisCache;

    private final TeamPersonalOkrService teamPersonalOkrService;

    @GetMapping("/list")
    @ApiOperation("获取团队 OKR 列表")
    public SystemJsonResponse<List<TeamOkrVO>> getTeamOkrs(HttpServletRequest request) {
        // 获取当前登录的用户
        User user = UserRecordUtil.getUserRecord(request);
        // 调用方法
        List<TeamOkrVO> personalOkrVOS = teamOkrService.getTeamOkrList(user);
        return SystemJsonResponse.SYSTEM_SUCCESS(personalOkrVOS);
    }

    @PostMapping("/tree/{id}")
    @ApiOperation("获取一个团队所在的树")
    public SystemJsonResponse<List<TeamOkr>> getCompleteTree(HttpServletRequest request,
                                                             @PathVariable("id") @NonNull @ApiParam("团队 OKR ID") Long id) {
        // 获取当前团队的祖先 ID
        Long rootId = TeamOkrUtil.getTeamRootId(id);
        // 获取根团队的所有孩子节点
        List<TeamOkr> teamOkrs = teamOkrService.selectChildTeams(rootId);
        if(teamOkrs.isEmpty()) {
            throw new GlobalServiceException(GlobalServiceStatusCode.TEAM_NOT_EXISTS);
        }
        log.info("查询团队 {} 在 根团队 {} 中， 树的总节点数为 {}", id, rootId, teamOkrs.size());
        // 判断是否是其中的成员
        User user = UserRecordUtil.getUserRecord(request);
        teamPersonalOkrService.findExistsInTeam(teamOkrs, user.getId()).orElseThrow(() ->
            new GlobalServiceException(GlobalServiceStatusCode.NON_TEAM_MEMBER)
        );
        return SystemJsonResponse.SYSTEM_SUCCESS(teamOkrs);
    }

    @PostMapping("/tree/child/{id}")
    @ApiOperation("获取一个团队的子树")
    public SystemJsonResponse<List<TeamOkr>> getChildTree(HttpServletRequest request,
                                                             @PathVariable("id") @NonNull @ApiParam("团队 OKR ID") Long id) {
        // 获取根团队的所有孩子节点
        List<TeamOkr> teamOkrs = teamOkrService.selectChildTeams(id);
        if(teamOkrs.isEmpty()) {
            throw new GlobalServiceException(GlobalServiceStatusCode.TEAM_NOT_EXISTS);
        }
        log.info("查询团队 {} 的子树， 树的总节点数为 {}", id, teamOkrs.size());
        // 判断是否是其中的成员
        User user = UserRecordUtil.getUserRecord(request);
        teamPersonalOkrService.findExistsInTeam(teamOkrs, user.getId()).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.NON_TEAM_MEMBER)
        );
        return SystemJsonResponse.SYSTEM_SUCCESS(teamOkrs);
    }

    @PostMapping("/grant")
    @ApiOperation("给成员授权，使其可以扩展一个子团队")
    public SystemJsonResponse grantTeamForMember(HttpServletRequest request, GrantDTO grantDTO) {
        // 检测
        grantDTO.validate();
        // 获取当前管理员 ID
        User user = UserRecordUtil.getUserRecord(request);
        Long managerId = user.getId();
        Long userId = grantDTO.getUserId();
        Long teamId = grantDTO.getTeamId();
        teamOkrService.grantTeamForMember(teamId, managerId, userId);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }




}
