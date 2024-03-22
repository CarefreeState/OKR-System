package com.macaku.center.component;

import com.macaku.center.service.InviteQRCodeService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ServiceLoader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-22
 * Time: 18:32
 */
@Component
@RequiredArgsConstructor
public class InviteQRCodeServiceSelector {

    public final static String WEB_TYPE = "web";

    public final static String WX_TYPE = "wx";

    public final List<InviteQRCodeService> inviteQRCodeServices;

    public InviteQRCodeService select(String type) {
        // 选取服务
        for (InviteQRCodeService inviteQRCodeService : inviteQRCodeServices) {
            if (inviteQRCodeService.match(type)) {
                return inviteQRCodeService;
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.HEAD_NOT_VALID);
    }
}
