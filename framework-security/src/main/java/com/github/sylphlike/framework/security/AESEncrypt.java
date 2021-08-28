package com.github.sylphlike.framework.security;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


/**
 * AES对称加密工具类
 * <p>  time 17:56 2017/03/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class AESEncrypt {

    private static final String PASSWORD            = "Gopal.pan";
    private static final String ALGORITHM           = "AES/ECB/PKCS5Padding";
    private static final String CHAR_SET            = "utf-8";
    private static final String KEY_ALGORITHM       = "AES";
    private static final int    ENCRYPT_KEY_LENGTH  =  256;



    public static String createStringKey() {

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
            //AES 要求密钥长度为 128
            keyGenerator.init(ENCRYPT_KEY_LENGTH, new SecureRandom(PASSWORD.getBytes()));
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        return null;
    }



    public static SecretKey createSecretKey() {

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
            //AES 要求密钥长度为 128
            keyGenerator.init(ENCRYPT_KEY_LENGTH, new SecureRandom(PASSWORD.getBytes()));
            return  keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        return null;
    }



    /**
     * 转换密钥
     * @param key  二进制密钥
     * @return  java.security.Key
     * @author  Gopal.pan
     */
    private static Key toKey(byte[] key) {
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }



    public static String encrypt(String data,String stringKey){
        try {
            Key key = toKey(Base64.getDecoder().decode(stringKey.getBytes(StandardCharsets.UTF_8)));
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(CHAR_SET)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static String decrypt(String data,String stringKey){

        try {
            Key key = toKey(Base64.getDecoder().decode(stringKey.getBytes(StandardCharsets.UTF_8)));

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(data));

            return new String(bytes,CHAR_SET);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }







}