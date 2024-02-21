package com.macaku.common.util;

import cn.hutool.extra.spring.SpringUtil;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * JWT工具类
 */
@Slf4j
public class JwtUtil {

    public static final String JWT_HEADER = "Token";

    //设置秘钥明文
    private static final String JWT_KEY = SpringUtil.getProperty("key.jwt");

    private static final String applicationName = SpringUtil.getProperty("spring.application.name");

    public static final Long JWT_TTL = 1L; // 一天有效期

    public static final Long JWT_MAP_TTL = 6L; // 六小时

    public static final TimeUnit JWT_TTL_UNIT = TimeUnit.DAYS;

    public  static final TimeUnit JWT_MAP_TTL_UNIT = TimeUnit.HOURS;

    public static final String JWT_LOGIN_WX_USER = "jwtLoginWxUser:";

    public static final String JWT_LOGIN_EMAIL_USER = "jwtLoginEmailUser:";

    public static final String JWT_RAW_DATA_MAP = "jwtRawDataMap:";

    public static String getUUID(){
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        return token;
    }

    /**
     * 生成jwt
     * @param subject token中要存放的数据（json格式）
     * @return
     */
    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID(), null);// 设置过期时间
        return builder.compact();
    }
 
    /**
     * 生成jwt
     * @param subject token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @return
     */
    public static String createJWT(String subject, Long ttlMillis, TimeUnit timeUnit) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID(), timeUnit);// 设置过期时间
        return builder.compact();
    }
 
    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid, TimeUnit timeUnit) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if (Objects.isNull(ttlMillis) || Objects.isNull(timeUnit)) { // 只有其中一个也等于没有
            ttlMillis = JWT_TTL_UNIT.toMillis(JwtUtil.JWT_TTL);
        } else {
            ttlMillis = timeUnit.toMillis(ttlMillis);
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
//                .setClaims() // 设置自定义载荷
                .setId(uuid)              //唯一的ID
                .setSubject(subject)   // 主题  可以是JSON数据
                .setIssuer(applicationName)     // 签发者
                .setIssuedAt(now)      // 签发时间
                .signWith(signatureAlgorithm, secretKey) //使用HS256对称加密算法签名, 第二个参数为秘钥
                .setExpiration(expDate); // 失效时间
    }
 
    /**
     * 创建token
     * @param id
     * @param subject
     * @param ttlMillis
     * @return
     */
    public static String createJWT(String id, String subject, Long ttlMillis, TimeUnit timeUnit) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id, timeUnit);// 设置过期时间
        return builder.compact();
    }
 
    /**
     * 生成加密后的秘钥 secretKey
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }
    /**
     * 解析
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody(); // 获得载荷
    }
    /**
     * 解析
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static String parseJWTRawData(String jwt) {
        return parseJWT(jwt).getSubject();
    }

    public static <T> T getJwtKeyValue(String jwt, String key, Class<T> clazz) {
        return parseJWT(jwt).get(key, clazz);
    }

    public static Date getExpiredDate(String jwt) {
        Date result = null;
        try {
            result = parseJWT(jwt).getExpiration();
        } catch (ExpiredJwtException e) {
            result = e.getClaims().getExpiration();
        }
        return result;
    }

    public static String refreshToken(String jwt) {
        return createJWT(parseJWTRawData(jwt));
    }

    // 过期的话 parseJWT(jwt)会抛异常，也就是 “没抛异常能解析成功就是校验成功”,但是异常 ExpiredJwtException 的 getClaims 方法能获取到 payload
    public static boolean isTokenExpired(String jwt) {
        return getExpiredDate(jwt).before(new Date());
    }

    public static boolean validateToken(String jwt) {
        try {
            parseJWT(jwt);
        } catch (ExpiredJwtException e) {
            return false;
        }
        return true;
    }

}