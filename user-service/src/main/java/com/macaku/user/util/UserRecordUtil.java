package com.macaku.user.util;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.user.component.UserRecordServiceSelector;
import com.macaku.user.domain.po.User;
import com.macaku.user.interceptor.config.VisitConfig;
import com.macaku.user.service.UserRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 19:58
 */
@Component
@Slf4j
public class UserRecordUtil {

    private final static UserRecordServiceSelector USER_RECORD_SERVICE_SELECTOR = SpringUtil.getBean(UserRecordServiceSelector.class);

    public static User getUserRecord(HttpServletRequest request) {
        String type = request.getHeader(VisitConfig.HEADER);
        if(!StringUtils.hasText(type)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_TOKEN_NOT_VALID);
        }
        UserRecordService userRecordService = USER_RECORD_SERVICE_SELECTOR.select(type);
        return userRecordService.getRecord(request).orElse(null);
    }

}
