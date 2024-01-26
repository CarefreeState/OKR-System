package com.macaku.center.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.OkrOperateDTO;
import com.macaku.center.domain.po.TeamOkr;
import com.macaku.center.domain.po.TeamPersonalOkr;
import com.macaku.center.domain.vo.TeamPersonalOkrVO;
import com.macaku.center.mapper.TeamPersonalOkrMapper;
import com.macaku.center.redis.config.CoreUserMapConfig;
import com.macaku.center.service.MemberService;
import com.macaku.center.service.OkrOperateService;
import com.macaku.center.service.TeamOkrService;
import com.macaku.center.service.TeamPersonalOkrService;
import com.macaku.center.util.TeamOkrUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import com.macaku.core.domain.vo.OkrCoreVO;
import com.macaku.core.service.OkrCoreService;
import com.macaku.user.domain.po.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 马拉圈
* @description 针对表【team_personal_okr(创建团队个人 OKR 表)】的数据库操作Service实现
* @createDate 2024-01-20 02:25:52
*/
@Service
@Slf4j
public class TeamPersonalOkrServiceImpl extends ServiceImpl<TeamPersonalOkrMapper, TeamPersonalOkr>
    implements TeamPersonalOkrService, OkrOperateService {

    private final static String SCENE = OkrServiceSelector.TEAM_PERSONAL_OKR_SCENE;

    private final TeamPersonalOkrMapper teamPersonalOkrMapper = SpringUtil.getBean(TeamPersonalOkrMapper.class);

    private final TeamOkrService teamOkrService = SpringUtil.getBean(TeamOkrService.class);

    private final OkrCoreService okrCoreService = SpringUtil.getBean(OkrCoreService.class);

    private final MemberService memberService = SpringUtil.getBean(MemberService.class);

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
        memberService.findExistsInTeam(teamOkrs, userId).ifPresent(x -> {
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
    public OkrCoreVO selectAllOfCore(User user, Long coreId) {
        // 根据 coreId 获取 coreId 使用者（团队个人 OKR 只能由使用者观看）
        Long userId = getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            // 调用服务查询详细信息
            return okrCoreService.searchOkrCore(coreId);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
    }

    @Override
    public Long getCoreUser(Long coreId) {
        String redisKey = CoreUserMapConfig.USER_CORE_MAP + coreId;
        return (Long) redisCache.getCacheObject(redisKey).orElseGet(() -> {
                Long userId = Db.lambdaQuery(TeamPersonalOkr.class)
                    .eq(TeamPersonalOkr::getCoreId, coreId)
                    .select(TeamPersonalOkr::getUserId)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.CORE_NOT_EXISTS)
                    ).getUserId();
                redisCache.setCacheObject(redisKey, userId, CoreUserMapConfig.USER_CORE_MAP_TTL, CoreUserMapConfig.USER_CORE_MAP_TTL_UNIT);
                return userId;
        });
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


}




