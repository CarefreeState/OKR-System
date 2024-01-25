package com.macaku.center.component;

import com.macaku.center.service.OkrOperateService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 18:35
 */
@Component
public class OkrServiceSelector {

    public final static String PERSONAL_OKR_SCOPE = "scope-p";

    public final static String TEAM_OKR_SCOPE = "scope-t";

    public final static String TEAM_PERSONAL_OKR_SCOPE = "scope-tp";

    public OkrOperateService select(String scope) {
        // 选取服务
        ServiceLoader<OkrOperateService> operateServices = ServiceLoader.load(OkrOperateService.class);
        Iterator<OkrOperateService> serviceIterator = operateServices.iterator();
        while (serviceIterator.hasNext()) {
            OkrOperateService operateService =  serviceIterator.next();
            if(operateService.match(scope)) {
                return operateService;
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.HEAD_NOT_VALID);
    }


}
