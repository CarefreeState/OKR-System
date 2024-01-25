package com.macaku.common.util;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONObject;
import com.macaku.common.exception.GlobalServiceException;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;

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


    public static JSONObject getUserInfoByEncryptedData(String encryptedData, String sessionKey, String iv){
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);

        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + 1;
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, StandardCharsets.UTF_8);
                return JSONObject.parseObject(result);
            }
        } catch (Exception e) {
            throw new GlobalServiceException(e.getMessage());
        }
        return null;
    }

}
