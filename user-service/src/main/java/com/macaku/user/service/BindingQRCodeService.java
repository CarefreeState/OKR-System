package com.macaku.user.service;

import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-10
 * Time: 19:36
 */
public interface BindingQRCodeService {

    Map<String, Object> getQRCodeParams();

    String getQRCodeJson(Long userId, String randomCode);

    void bindingWx(Long userId, String randomCode, String code);

    void checkIdentifyingCode(Long userId, String randomCode);

}
