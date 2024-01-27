package com.macaku.user.component;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.user.service.LoginService;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-27
 * Time: 13:24
 */
@Component
public class LoginServiceSelector {

    public static final String WX_LOGIN_TYPE = "WX_JWT"; // valEFs

    public static final String EMAIL_LOGIN_TYPE = "EMAIL_JWT"; // 2CxcDX

    public LoginService select(String type) {
        // 选取服务
        ServiceLoader<LoginService> loginServices = ServiceLoader.load(LoginService.class);
        Iterator<LoginService> serviceIterator = loginServices.iterator();
        while (serviceIterator.hasNext()) {
            LoginService loginService =  serviceIterator.next();
            if(loginService.match(type)) {
                return loginService;
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.HEAD_NOT_VALID);
    }

}
