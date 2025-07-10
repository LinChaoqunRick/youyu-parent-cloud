package com.youyu.utils;

import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * JWT工具类
 */
public class JwtUtil {

    //有效期为
    public static final Long JWT_TTL = 24 * 60 * 60 * 1000L;// 60 * 60 *1000  一个小时
    //设置秘钥明文
    public static final String JWT_KEY = "MtR6UDKvOg7IBcpW7o4j0UK2pVAf1geB14VXicSOm92quFmDhtOjo9nDTxajysqyWlfXKIqqGcsTHBGnBeZLZ0aQfHJS8a22P2UgYJ47vrNesKZ7UGSCnLeKELunVt6lSz3KZ5F1rA11XHZgoLXsTjwEtHPylkISG75Q7L9jeKbAoDGRgYEl2r8V8ijvAqmg3OyxOXaMS6IwgLTBFZJfpiQQJ4I1lO5oqpQ5gqK7aLk5SgdU1xPPyfeNyseMaxkY";

    public static String getUUID() {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        return token;
    }

    /**
     * 生成jwt
     *
     * @param subject token中要存放的数据（json格式）
     * @return
     */
    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());// 设置过期时间
        return builder.compact();
    }

    /**
     * 生成jwt
     *
     * @param subject   token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @return
     */
    public static String createJWT(String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID());// 设置过期时间
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if (ttlMillis == null) {
            ttlMillis = JwtUtil.JWT_TTL;
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
                .setId(uuid)              //唯一的ID
                .setSubject(subject)   // 主题  可以是JSON数据
                .setIssuer("lin")     // 签发者
                .setIssuedAt(now)      // 签发时间
                .signWith(signatureAlgorithm, secretKey) //使用HS256对称加密算法签名, 第二个参数为秘钥
                .setExpiration(expDate);
    }

    /**
     * 创建token
     *
     * @param id
     * @param subject
     * @param ttlMillis
     * @return
     */
    public static String createJWT(String id, String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id);// 设置过期时间
        return builder.compact();
    }

    public static void main(String[] args) throws Exception {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3NTIxOTk2MDcsInVzZXJfaWQiOjEwMDAwLCJ1c2VyX25hbWUiOiIxNTg2MDA5NDU0NCIsImp0aSI6IjA5NGVmZjk3LTE2YWMtNGY0YS1iMjEyLTU2ZTI0ZDI3MjQ2MiIsImNsaWVudF9pZCI6InlvdXl1LXdlYiIsInNjb3BlIjpbImFsbCJdfQ.Bx3QyQ7iKjHAAkxr8dtl5iEssaw5AeDoQD91V16AFgk";
        Claims claims = parseJWT(token);
        System.out.println(claims);
        System.out.println(claims.getSubject());
    }

    /**
     * 生成加密后的秘钥 secretKey
     *
     * @return
     */
    public static SecretKey generalKey() {
        return new SecretKeySpec(JWT_KEY.getBytes(), "HmacSHA256");
    }

    /**
     * 解析
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) throws Exception {
        return Jwts.parser()
                .setSigningKey(generalKey())
                .parseClaimsJws(jwt)
                .getBody();
    }

    public static Claims parseJWTException(String jwt) {
        try {
            return parseJWT(jwt);
        } catch (ExpiredJwtException | PrematureJwtException var) {
            return  var.getClaims();
        } catch (Exception var2) {
            return null;
        }
    }


}
