package com.macaku.center.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.OkrOperateDTO;
import com.macaku.center.domain.po.TeamOkr;
import com.macaku.center.domain.po.TeamPersonalOkr;
import com.macaku.center.domain.vo.TeamOkrStatisticVO;
import com.macaku.center.domain.vo.TeamOkrVO;
import com.macaku.center.mapper.TeamOkrMapper;
import com.macaku.center.mapper.TeamPersonalOkrMapper;
import com.macaku.center.redis.config.CoreUserMapConfig;
import com.macaku.center.service.MemberService;
import com.macaku.center.service.OkrOperateService;
import com.macaku.center.service.TeamOkrService;
import com.macaku.center.service.WxQRCodeService;
import com.macaku.center.util.TeamOkrUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import com.macaku.common.util.media.MediaUtils;
import com.macaku.common.web.HttpUtils;
import com.macaku.core.domain.po.inner.KeyResult;
import com.macaku.core.domain.vo.OkrCoreVO;
import com.macaku.core.mapper.quadrant.FirstQuadrantMapper;
import com.macaku.core.service.OkrCoreService;
import com.macaku.user.domain.po.User;
import com.macaku.user.token.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 马拉圈
* @description 针对表【team_okr(团队 OKR 表)】的数据库操作Service实现
* @createDate 2024-01-20 02:25:52
*/
@Service
@Slf4j
public class TeamOkrServiceImpl extends ServiceImpl<TeamOkrMapper, TeamOkr>
    implements TeamOkrService, OkrOperateService {

    private final static String TEAM_QR_CODE_MAP = "teamQRCodeMap:";

    private final static String WX_QR_CORE_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit";

    private final static Long TEAM_QR_MAP_TTL = 30L;

    private final static TimeUnit TEAM_QR_MAP_UNIT = TimeUnit.DAYS;

    private final static String SCENE = OkrServiceSelector.TEAM_OKR_SCENE;

    private final TeamOkrMapper teamOkrMapper = SpringUtil.getBean(TeamOkrMapper.class);

    private final TeamPersonalOkrMapper teamPersonalOkrMapper = SpringUtil.getBean(TeamPersonalOkrMapper.class);

    private final RedisCache redisCache = SpringUtil.getBean(RedisCache.class);

    private final OkrCoreService okrCoreService = SpringUtil.getBean(OkrCoreService.class);

    private final MemberService memberService = SpringUtil.getBean(MemberService.class);

    private final FirstQuadrantMapper firstQuadrantMapper = SpringUtil.getBean(FirstQuadrantMapper.class);

    private final WxQRCodeService wxQRCodeService = SpringUtil.getBean(WxQRCodeService.class);

    @Override
    public boolean match(String scene) {
        return SCENE.equals(scene);
    }

    @Override
    public List<TeamOkr> selectChildTeams(Long id) {
        return teamOkrMapper.selectChildTeams(id);
    }

    @Override
    public TeamOkr findRootTeam(Long id) {
        return teamOkrMapper.findRootTeam(id).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.TEAM_NOT_EXISTS));
    }

    @Override
    public List<TeamOkrVO> getTeamOkrList(User user) {
        // 获取当前用户 id
        Long id = user.getId();
        // 获取团队 OKR 列表
        List<TeamOkrVO> teamOkrList = teamOkrMapper.getTeamOkrList(id);
        log.info("查询用户 {} 的团队 OKR 列表 : {} 行", id, teamOkrList.size());
        return teamOkrList;
    }

    @Override
    public TeamOkr checkManager(Long teamId, Long managerId) {
        TeamOkr teamOkr = teamOkrMapper.selectById(teamId);
        if (Objects.isNull(teamOkr)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.TEAM_NOT_EXISTS);
        }
        if (!teamOkr.getManagerId().equals(managerId)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.NON_TEAM_MANAGER);
        }
        return teamOkr;
    }

    @Override
    public void grantTeamForMember(Long teamId, Long managerId, Long userId) {
        // 判断团队的管理者是不是当前用户
        checkManager(teamId, managerId);
        // 判断授权对象是否有本团队为 teamId 的团队个人 OKR (这里用 Db 防止循环依赖)
        Db.lambdaQuery(TeamPersonalOkr.class)
                .eq(TeamPersonalOkr::getTeamId, teamId)
                .eq(TeamPersonalOkr::getUserId, userId)
                .oneOpt().orElseThrow(() ->
                        new GlobalServiceException(GlobalServiceStatusCode.NON_TEAM_MEMBER));
        // 判断用户是否管理着父亲节点为 teamId 的团队 OKR
        Db.lambdaQuery(TeamOkr.class)
                .eq(TeamOkr::getParentTeamId, teamId)
                .eq(TeamOkr::getManagerId, userId)
                .oneOpt().ifPresent(t -> {
                    throw new GlobalServiceException(GlobalServiceStatusCode.REPEATED_GRANT);
                });
        // 授权成功，构造团队 OKR
        // 构造 OKR 内核
        Long coreId = okrCoreService.createOkrCore();
        // 创建一个团队 OKR
        TeamOkr newTeamOkr = new TeamOkr();
        newTeamOkr.setCoreId(coreId);
        newTeamOkr.setParentTeamId(teamId);
        newTeamOkr.setManagerId(userId);
        teamOkrMapper.insert(newTeamOkr);

        // 本来就有团队个人 OKR，无需再次生成
        log.info("管理员 {} 为成员 {} 授权创建团队原OKR {} 的子 OKR {} 内核 {}", managerId, userId, teamId, newTeamOkr.getId(), coreId);
    }

    @Override
    public List<TeamOkrStatisticVO> countCompletionRate(List<TeamOkr> teamOkrs) {
        // 获取 ids
        List<Long> ids = teamOkrs.stream().parallel().map(TeamOkr::getId).collect(Collectors.toList());
        // 通过 ids 换取第一象限列表，并统计数据
        List<TeamOkrStatisticVO> statisticVOS = teamOkrMapper.selectKeyResultsByTeamId(ids);
        statisticVOS.stream()
                .parallel()
                .sorted(Comparator.comparing(TeamOkrStatisticVO::getId))
                .forEachOrdered(teamOkrStatisticVO -> {
            long sum = teamOkrStatisticVO.getKeyResults().stream()
                    .parallel()
                    .filter(Objects::nonNull)
                    .mapToLong(KeyResult::getProbability)
                    .filter(Objects::nonNull)
                    .reduce(Long::sum)
                    .orElse(0);
            int size = teamOkrStatisticVO.getKeyResults().size();
            Double average = size == 0 ? Double.valueOf(0) : Double.valueOf(sum * 1.0 / size);
            teamOkrStatisticVO.setAverage(average);
            teamOkrStatisticVO.setKeyResults(null);
        });
        return statisticVOS;
    }

    @Override
    public String getQRCode(Long teamId) {
        String redisKey = TEAM_QR_CODE_MAP + teamId;
        return (String)redisCache.getCacheObject(redisKey).orElseGet(() -> {
            String accessToken = TokenUtil.getToken();
            String url = WX_QR_CORE_URL + HttpUtils.getQueryString(new HashMap<String, Object>(){{
                this.put("access_token", accessToken);
            }});
            // 获取 QRCode
            String json = wxQRCodeService.getQRCodeJson(teamId);
            log.info("请求微信（json） -> {}", json);
            byte[] data = HttpUtils.doPostJsonBytes(url, json);
            // 保存一下
            String mapPath = MediaUtils.saveImage(data);
            redisCache.setCacheObject(redisKey, mapPath, TEAM_QR_MAP_TTL, TEAM_QR_MAP_UNIT);
            return mapPath;
        });
    }

    @Override
    public void createOkrCore(User user, OkrOperateDTO okrOperateDTO) {
        Long userId = user.getId();
        String redisKey = TeamOkrUtil.CREATE_CD_FLAG + userId;
        // 判断是否处于冷却状态
        redisCache.getCacheObject(redisKey).ifPresent(o -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.TEAM_CREATE_TOO_FREQUENT);
        });
        // 创建两个 OKR 内核
        Long coreId1 = okrCoreService.createOkrCore();
        Long coreId2 = okrCoreService.createOkrCore();
        // 创建一个团队 OKR
        TeamOkr teamOkr = new TeamOkr();
        teamOkr.setCoreId(coreId1);
        teamOkr.setManagerId(userId);
        teamOkrMapper.insert(teamOkr);
        Long teamId = teamOkr.getId();
        log.info("用户 {} 新建团队 OKR {}  内核 {}", userId, teamId, coreId1);
        // 设置冷却时间
        redisCache.setCacheObject(redisKey, false, TeamOkrUtil.CREATE_CD, TeamOkrUtil.CD_UNIT);// CD 没好的意思
        // 团队的“始祖”有团队个人 OKR
        TeamPersonalOkr teamPersonalOkr = new TeamPersonalOkr();
        teamPersonalOkr.setCoreId(coreId2);
        teamPersonalOkr.setTeamId(teamId);
        teamPersonalOkr.setUserId(userId);
        teamPersonalOkrMapper.insert(teamPersonalOkr);
        log.info("用户 {} 新建团队 {} 的 团队个人 OKR {} 内核 {}", userId, teamId, teamPersonalOkr.getId(), coreId2);
    }

    @Override
    public OkrCoreVO selectAllOfCore(User user, Long coreId) {
        // 检测用户是否是 coreId 所属团队的成员
        Long teamId = Db.lambdaQuery(TeamOkr.class)
                .eq(TeamOkr::getCoreId, coreId)
                .oneOpt().orElseThrow(() ->
                        new GlobalServiceException(GlobalServiceStatusCode.CORE_NOT_EXISTS)
                ).getId();
        memberService.checkExistsInTeam(teamId, user.getId());
        // 到这里就没问题了
        return okrCoreService.searchOkrCore(coreId);
    }

    @Override
    public Long getCoreUser(Long coreId) {
        String redisKey = CoreUserMapConfig.USER_CORE_MAP + coreId;
        return (Long) redisCache.getCacheObject(redisKey).orElseGet(() -> {
            Long managerId = Db.lambdaQuery(TeamOkr.class)
                    .eq(TeamOkr::getCoreId, coreId)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.CORE_NOT_EXISTS)
                    ).getManagerId();
            redisCache.setCacheObject(redisKey, managerId, CoreUserMapConfig.USER_CORE_MAP_TTL, CoreUserMapConfig.USER_CORE_MAP_TTL_UNIT);
            return managerId;
        });
    }

}




