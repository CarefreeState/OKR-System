package com.macaku.center.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.OkrOperateDTO;
import com.macaku.center.domain.po.TeamOkr;
import com.macaku.center.domain.po.TeamPersonalOkr;
import com.macaku.center.domain.vo.TeamPersonalOkrVO;
import com.macaku.center.mapper.TeamPersonalOkrMapper;
import com.macaku.center.service.OkrOperateService;
import com.macaku.center.service.TeamOkrService;
import com.macaku.center.service.TeamPersonalOkrService;
import com.macaku.center.util.TeamOkrUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import com.macaku.core.service.OkrCoreService;
import com.macaku.user.domain.po.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 马拉圈
* @description 针对表【team_personal_okr(创建团队个人 OKR 表)】的数据库操作Service实现
* @createDate 2024-01-20 02:25:52
*/
@Service
@Slf4j
public class TeamPersonalOkrServiceImpl extends ServiceImpl<TeamPersonalOkrMapper, TeamPersonalOkr>
    implements TeamPersonalOkrService, OkrOperateService {

    private final static String USER_TEAM_MEMBER = "userTeamMember:";

    private final static Long USER_TEAM_MEMBER_TTL = 30L;

    private final static TimeUnit USER_TEAM_MEMBER_TTL_UNIT = TimeUnit.DAYS;

    private final static String SCENE = OkrServiceSelector.TEAM_PERSONAL_OKR_SCENE;

    private final TeamPersonalOkrMapper teamPersonalOkrMapper = SpringUtil.getBean(TeamPersonalOkrMapper.class);

    private final TeamOkrService teamOkrService = SpringUtil.getBean(TeamOkrService.class);

    private final OkrCoreService okrCoreService = SpringUtil.getBean(OkrCoreService.class);

    private final RedisCache redisCache = SpringUtil.getBean(RedisCache.class);

    @Override
    public boolean match(String scene) {
        return SCENE.equals(scene);
    }

    @Override
    public void createOkrCore(User user, OkrOperateDTO okrOperateDTO) {
        // 获取用户 ID（受邀者）
        Long userId = user.getId();
        Long teamId = okrOperateDTO.getTeamOkrId();
        // 判断用户是否是这棵树的成员
        // 获取根节点
        Long rootId = TeamOkrUtil.getTeamRootId(teamId);
        // 获取整棵树
        List<TeamOkr> teamOkrs = teamOkrService.selectChildTeams(rootId);
        // 判断是否可以加入团队
        findExistsInTeam(teamOkrs, userId).ifPresent(x -> {
            String message = String.format("用户 %d 已在团队树 %d 的一个团队 %d 中, 无法加入团队 %d", userId, rootId, x, teamId);
            throw new GlobalServiceException(message, GlobalServiceStatusCode.REPEATED_JOIN_TEAM);
        });
        // 可以加入团队了
        // 创建一个 OKR 内核
        Long coreId = okrCoreService.createOkrCore();
        // 创建一个团队个人 OKR
        TeamPersonalOkr teamPersonalOkr = new TeamPersonalOkr();
        teamPersonalOkr.setCoreId(coreId);
        teamPersonalOkr.setTeamId(teamId);
        teamPersonalOkr.setUserId(userId);
        teamPersonalOkrMapper.insert(teamPersonalOkr);
        log.info("用户 {} 新建团队 {} 的 团队个人 OKR {} 内核 {}", userId, teamId, teamPersonalOkr.getId(), coreId);
    }

    @Override
    public List<TeamPersonalOkrVO> getTeamPersonalOkrList(User user) {
        // 获取当前用户 id
        Long id = user.getId();
        // 获取团队 OKR 列表
        List<TeamPersonalOkrVO> teamPersonalOkrVOList = teamPersonalOkrMapper.getTeamPersonalOkrList(id);
        log.info("查询用户 {} 的团队个人 OKR 列表 : {} 行", id, teamPersonalOkrVOList.size());
        return teamPersonalOkrVOList;
    }

    @Override
    public Optional<Long> findExistsInTeam(List<TeamOkr> teamOkrs, Long userId) {
        List<Long> ids = teamOkrs.stream()
                .parallel()
                .map(TeamOkr::getId)
                .collect(Collectors.toList());
        return teamPersonalOkrMapper.getTeamPersonalOkrList(userId).stream()
                .parallel()
                .map(TeamPersonalOkrVO::getTeamId)
                .filter(ids::contains)
                .findAny();
    }

    @Override
    public void checkExistsInTeam(Long teamId, Long userId) {
        Long rootId = TeamOkrUtil.getTeamRootId(teamId);
        // 查看是否有缓存
        String redisKey = USER_TEAM_MEMBER + rootId;
        Boolean isExists = (Boolean) redisCache.getCacheMapValue(redisKey, userId).orElseGet(() -> {
            List<TeamOkr> teamOkrs = teamOkrService.selectChildTeams(rootId);
            findExistsInTeam(teamOkrs, userId).orElseThrow(() -> {
                redisCache.getCacheObject(redisKey).orElseGet(() -> {
                    Map<Long, Boolean> map = new HashMap<>();
                    map.put(userId, false);
                    redisCache.setCacheMap(redisKey, map, USER_TEAM_MEMBER_TTL, USER_TEAM_MEMBER_TTL_UNIT);
                    return Boolean.FALSE;
                });
                return new GlobalServiceException(GlobalServiceStatusCode.NON_TEAM_MEMBER);
            });
            redisCache.setCacheMapValue(redisKey, userId, true);
            return Boolean.TRUE;
        });
        if(Boolean.FALSE.equals(isExists)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.NON_TEAM_MEMBER);
        }
    }
}




