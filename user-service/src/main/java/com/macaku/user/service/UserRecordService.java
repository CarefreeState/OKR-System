package com.macaku.user.service;

import com.macaku.user.domain.dto.detail.LoginUser;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 19:02
 */
public interface UserRecordService {

    boolean match(String type);

    Optional<LoginUser> getRecord(HttpServletRequest request);

    void deleteRecord(HttpServletRequest request);
}
