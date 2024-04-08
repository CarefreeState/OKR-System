package com.macaku.user.component;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.user.service.UserRecordService;
import org.springframework.stereotype.Component;

import java.util.ServiceLoader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-27
 * Time: 13:33
 */
@Component
public class UserRecordServiceSelector {

    public static final String WX_LOGIN_TYPE = LoginServiceSelector.WX_LOGIN_TYPE; // valEFs

    public static final String EMAIL_LOGIN_TYPE = LoginServiceSelector.EMAIL_LOGIN_TYPE; // 2CxcDX

    private final ServiceLoader<UserRecordService> userRecordServices = ServiceLoader.load(UserRecordService.class);

    public UserRecordService select(String type) {
        // 选取服务
        for (UserRecordService userRecordService : userRecordServices) {
            if (userRecordService.match(type)) {
                return userRecordService;
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.REQUEST_NOT_VALID);
    }

}
