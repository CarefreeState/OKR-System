package com.macaku.center.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.OkrOperateDTO;
import com.macaku.center.domain.po.PersonalOkr;
import com.macaku.center.domain.vo.PersonalOkrVO;
import com.macaku.center.mapper.PersonalOkrMapper;
import com.macaku.center.config.CoreUserMapConfig;
import com.macaku.center.service.OkrOperateService;
import com.macaku.center.service.PersonalOkrService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import com.macaku.core.domain.vo.OkrCoreVO;
import com.macaku.core.service.OkrCoreService;
import com.macaku.user.domain.po.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author 马拉圈
* @description 针对表【personal_okr(个人 OKR 表)】的数据库操作Service实现
* @createDate 2024-01-20 02:25:52
*/
@Service
@Slf4j
public class PersonalOkrServiceImpl extends ServiceImpl<PersonalOkrMapper, PersonalOkr>
    implements PersonalOkrService, OkrOperateService {

    private final static String SCENE = OkrServiceSelector.PERSONAL_OKR_SCENE;

    private final static Long ALLOW_COUNT = 1L; // 允许同时存在多少个未完成的 OKR

    private final OkrCoreService okrCoreService = SpringUtil.getBean(OkrCoreService.class);

    private final PersonalOkrMapper personalOkrMapper = SpringUtil.getBean(PersonalOkrMapper.class);

    private final RedisCache redisCache =SpringUtil.getBean(RedisCache.class);

    @Override
    public boolean match(String scene) {
        return SCENE.equals(scene);
    }

    @Override
    public Map<String, Object> createOkrCore(User user, OkrOperateDTO okrOperateDTO) {
        Long userId = user.getId();
        // 查看当前用户是否有未完成的 OKR
        Long count = personalOkrMapper.getNotCompletedCount(userId);
        if(ALLOW_COUNT.compareTo(count) <= 0) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NO_PERMISSION);
        }
        // 创建 OKR 内核
        Long coreId = okrCoreService.createOkrCore();
        // 创建 个人 OKR
        PersonalOkr personalOkr = new PersonalOkr();
        personalOkr.setCoreId(coreId);
        personalOkr.setUserId(userId);
        personalOkrMapper.insert(personalOkr);
        Long id = personalOkr.getId();
        log.info("用户 {} 个人团队 OKR {}  内核 {}", userId, id, coreId);
        return new HashMap<String, Object>() {{
            this.put("id", id);
            this.put("coreId", coreId);
        }};
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
            Long userId = Db.lambdaQuery(PersonalOkr.class)
                    .eq(PersonalOkr::getCoreId, coreId)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.CORE_NOT_EXISTS)
                    ).getUserId();
            redisCache.setCacheObject(redisKey, userId, CoreUserMapConfig.USER_CORE_MAP_TTL, CoreUserMapConfig.USER_CORE_MAP_TTL_UNIT);
            return userId;
        });
    }


    @Override
    public List<PersonalOkrVO> getPersonalOkrList(User user) {
        // 根据用户 ID 查询
        Long id = user.getId();
        List<PersonalOkrVO> personalOkrList = personalOkrMapper.getPersonalOkrList(id);
        log.info("查询用户 {} 的个人 OKR 列表 : {} 行", id, personalOkrList.size());
        return personalOkrList;
    }
}




