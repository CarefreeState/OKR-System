package com.macaku.center.service;

import com.macaku.center.domain.vo.LoginQRCodeVO;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-10
 * Time: 21:05
 */
public interface OkrQRCodeService {

    String getInviteQRCode(Long teamId, String type);

    String getBindingQRCode(Long userId, String randomCode);

    LoginQRCodeVO getLoginQRCode();

}
