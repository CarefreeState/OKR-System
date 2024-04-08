package com.macaku.qrcode.component;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.qrcode.service.InviteQRCodeService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ServiceLoader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-22
 * Time: 18:32
 */
@Component
public class InviteQRCodeServiceSelector {

    public final static String WEB_TYPE = "web";

    public final static String WX_TYPE = "wx";

    private final ServiceLoader<InviteQRCodeService> inviteQRCodeServices = ServiceLoader.load(InviteQRCodeService.class);

    public String getType(String type) {
        return StringUtils.hasText(type) ? type : WX_TYPE;
    }

    public InviteQRCodeService select(String type) {
        type = getType(type);
        // 选取服务
        for (InviteQRCodeService inviteQRCodeService : inviteQRCodeServices) {
            if (inviteQRCodeService.match(type)) {
                return inviteQRCodeService;
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.REQUEST_NOT_VALID);
    }

}
