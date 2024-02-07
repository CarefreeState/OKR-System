package com.macaku.common.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 16:11
 */
public class EncryptUtil {

    // md5加密
    public static String md5(String normal) {
        return DigestUtils.md5Hex(normal);
    }

    public static String sha1(String... strings) {
        StringBuilder builder = new StringBuilder();
        for(String s : strings) {
            builder.append(s);
        }
        // 加密
        return DigestUtils.sha1Hex(builder.toString());
    }
}
