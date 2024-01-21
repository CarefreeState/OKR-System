package com.macaku.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 16:11
 */
public class EncryptUtil {

    public static String sha1(String... strings) {
        // 排序
        Arrays.sort(strings);
        // 将三个字符串以此顺序进行拼接
        StringBuilder builder = new StringBuilder();
        for(String s : strings) {
            builder.append(s);
        }
        // 加密
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("sha1");
            byte[] digest = messageDigest.digest(builder.toString().getBytes(StandardCharsets.UTF_8));
            // 2.2) 将加密后的byte数组转换为signature一样的格式（每个字节都转换为十六进制进行拼接）
            builder = new StringBuilder();
            for(byte b : digest) {
                // builder.append(Integer.toHexString(b));不能这么弄因为这样弄b如果是负，那么就凉凉
                // 这样写保证两位十六进制都存在并且正确
                builder.append(Integer.toHexString((b >> 4) & 15));//前四个字节转换为十六进制
                builder.append(Integer.toHexString(b & 15));//后四个字节转换为十六进制
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return builder.toString();
    }

}
