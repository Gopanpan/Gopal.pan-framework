package com.github.sylphlike.framework.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>  time 17:56 2010/10/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class JWTToken {

    private static final String ISSUER = "Gopal.pan";

    private static final Algorithm algorithm = Algorithm.HMAC256("secret");



    /**
     * 签发JWT 默认token失效时间为30分钟
     * @param subject  内容
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String create(String subject) {

        return create(subject,30L, TimeUnit.MINUTES);

    }



    /**
     * 签发指定过期时间的token
     * @param subject   内容
     * @param expires   过期时间
     * @param timeUnit  时间单位
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String create(String subject, Long expires, TimeUnit timeUnit) {
        long millisExpires;
        if (!TimeUnit.MILLISECONDS.equals(timeUnit)){
            millisExpires = timeUnit.toMillis(expires);
        }else {
            millisExpires = expires;
        }

        return JWT.create()
                .withKeyId(EncryptHelper.sixUuid())
                .withJWTId(EncryptHelper.sixUuid())
                .withIssuer(ISSUER)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(new Date().getTime() +  millisExpires ))
                .withSubject(subject)
                .sign(algorithm);
    }



    /**
     * 验证token
     * @param jwtStr  待验证token
     * @return  boolean
     * @author  Gopal.pan
     */
    public static boolean validate(String jwtStr) {

        JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
        verifier.verify(jwtStr);
        return true;


    }



    /**
     * 验证token
     * @param jwtStr  待验证token
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String validateSubject(String jwtStr) {
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
        DecodedJWT verify = verifier.verify(jwtStr);
        return verify.getSubject();
    }







}
