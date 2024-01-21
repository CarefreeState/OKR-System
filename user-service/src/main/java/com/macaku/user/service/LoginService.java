package com.macaku.user.service;

import com.macaku.user.domain.dto.LoginDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 12:48
 */
public interface LoginService {

    boolean match(String type);


    @Transactional
    Map<String, Object> login(LoginDTO loginDTO);
}
