package com.macaku.common.email.service;

public interface EmailService {

    boolean match(String type);

    /**
     * 向用户邮箱发送验证码
     *
     * @param email 用户的邮箱
     * @param code  验证码
     */
    void sendIdentifyingCode(String email, String code);

    /**
     * 校验当前邮箱用户输入的验证码是否正确
     *
     * @param email 用户的邮箱
     * @param code  验证码
     */
    void checkIdentifyingCode(String email, String code);
}
