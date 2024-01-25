package com.macaku.center.service;

import com.macaku.center.domain.dto.unify.OkrOperateDTO;
import com.macaku.user.domain.po.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 18:36
 */
public interface OkrOperateService {
    boolean match(String scope);

    @Transactional
    void createOkrCore(User user, OkrOperateDTO okrOperateDTO);
}
