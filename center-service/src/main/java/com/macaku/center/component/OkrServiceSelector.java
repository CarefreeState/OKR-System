package com.macaku.center.component;

import com.macaku.center.service.OkrOperateService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import org.springframework.stereotype.Component;

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

    public final static String PERSONAL_OKR_SCENE = "scene-p";

    public final static String TEAM_OKR_SCENE = "scene-t";

    public final static String TEAM_PERSONAL_OKR_SCENE = "scene-tp";

    public OkrOperateService select(String scope) {
        // 选取服务
        ServiceLoader<OkrOperateService> operateServices = ServiceLoader.load(OkrOperateService.class);
        for (OkrOperateService operateService : operateServices) {
            if (operateService.match(scope)) {
                return operateService;
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.HEAD_NOT_VALID);
    }

}
