package com.macaku.qrcode.component;

import com.macaku.common.locator.ServiceFactory;
import com.macaku.qrcode.service.InviteQRCodeService;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 15:22
 */
public interface InviteQRCodeServiceFactory extends ServiceFactory<InviteQRCodeService> {

    String WX_TYPE = "wx";

    String WEB_TYPE = "web";

}
