package com.github.sylphlike.framework.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA 非对称加密工具类
 * <p>  time 13:58 2021/03/03  星期三 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
public class RSAEncrypt {

    /** 加密算法RSA */
    public static final String KEY_ALGORITHM = "RSA";

    /** 签名算法 */
    public static final String SIGNATURE_ALGORITHM = "SHA256WithRSA";

    /** 获取公钥的key */
    public static final String PUBLIC_KEY = "RSAPublicKey";

    /** 获取私钥的key */
    public static final String PRIVATE_KEY = "RSAPrivateKey";

    /** RSA最大加密明文大小 */
    private static final int MAX_ENCRYPT_BLOCK = 245;

    /** RSA最大解密密文大小*/
    private static final int MAX_DECRYPT_BLOCK = 256;


    /**
     * 生成密钥对，默认为2048位证书
     * 1024位的证书，加密时最大支持117个字节，解密时为128；
     * 2048位的证书，加密时最大支持245个字节，解密时为256。
     * 加密时支持的最大字节数：证书位数/8 -11（比如：2048位的证书，支持的最大加密字节数：2048/8 - 11 = 245）
     * @return  java.util.Map<java.lang.String,java.lang.Object>
     * @author  Gopal.pan
     */
    public static Map<String, String> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, String> keyMap = new HashMap<>(2);
        keyMap.put(PUBLIC_KEY, Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        keyMap.put(PRIVATE_KEY, Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        return keyMap;
    }





    /**
     * 用私钥对信息生成数字签名
     * @param data       已加密数据字节数组
     * @param privateKey 私钥(BASE64编码)
     * @return  java.lang.String
     * @throws  Exception ex
     * @author  Gopal.pan
     */
    public static String sign(String data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature.sign());
    }



    /**
     * 校验数字签名
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     * @return  boolean
     * @throws  Exception ex
     * @author  Gopal.pan
     */
    public static boolean verify(String data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return signature.verify(Base64.getDecoder().decode(sign));
    }





    /**
     * 公钥加密
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return  java.lang.String  Base64编码的字符串
     * @throws  Exception ex
     * @author  Gopal.pan
     */
    public static String encryptByPublicKey(String data,String publicKey) throws Exception {
        byte[] bytes = encryptByPublicKey(data.getBytes(StandardCharsets.UTF_8), publicKey);
        return Base64.getEncoder().encodeToString(bytes);

    }


    /**
     * 公钥加密
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return  byte[]  为编码的字节数组
     * @throws  Exception  ex
     * @author  Gopal.pan
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        return getBytes(data, keyFactory, publicK, Cipher.ENCRYPT_MODE, MAX_ENCRYPT_BLOCK);
    }




    /**
     * 私钥加密
     * @param data       源数据
     * @param privateKey 私钥(BASE64编码)
     * @return  java.lang.String
     * @throws  Exception ex
     * @author  Gopal.pan
     */
    public static String encryptByPrivateKey(String data,String privateKey) throws Exception {
        byte[] bytes = encryptByPrivateKey(data.getBytes(StandardCharsets.UTF_8), privateKey);
        return Base64.getEncoder().encodeToString(bytes);
    }


    /**
     * 私钥加密
     * @param data       源数据
     * @param privateKey 私钥(BASE64编码)
     * @return  byte[]
     * @throws  Exception ex
     * @author  Gopal.pan
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        return getBytes(data, keyFactory, privateK, Cipher.ENCRYPT_MODE, MAX_ENCRYPT_BLOCK);
    }




    /**
     * 公钥解密
     * @param data       已加密数据
     * @param publicKey  公钥(BASE64编码)
     * @return  java.lang.String
     * @throws  Exception ex
     * @author  Gopal.pan
     */
    public static String decryptByPublicKey(String data,String publicKey) throws Exception {
        byte[] bytes = decryptByPublicKey(Base64.getDecoder().decode(data), publicKey);
        return new String(bytes);

    }


    /**
     * 公钥解密
     * @param encryptedData 已加密数据
     * @param publicKey     公钥(BASE64编码)
     * @return  byte[]
     * @throws  Exception ex
     * @author  Gopal.pan
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        return getBytes(encryptedData, keyFactory, publicK, Cipher.DECRYPT_MODE, MAX_DECRYPT_BLOCK);
    }





    /**
     * 私钥解密
     * @param data          已加密数据
     * @param privateKey    私钥(BASE64编码)
     * @return  java.lang.String
     * @throws  Exception ex
     * @author  Gopal.pan
     */
    public static String decryptByPrivateKey(String data,String privateKey) throws Exception {
        byte[] bytes = RSAEncrypt.decryptByPrivateKey(Base64.getDecoder().decode(data), privateKey);
        return new String(bytes);

    }

    /**
     * 私钥解密
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     * @return  byte[]
     * @throws  Exception ex
     * @author  Gopal.pan
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        return getBytes(encryptedData, keyFactory, privateK, Cipher.DECRYPT_MODE, MAX_DECRYPT_BLOCK);
    }





    private static byte[] getBytes(byte[] data, KeyFactory keyFactory, Key publicK, int encryptMode, int maxEncryptBlock)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        Cipher cipher = getCipher(keyFactory, publicK, encryptMode);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxEncryptBlock) {
                cache = cipher.doFinal(data, offSet, maxEncryptBlock);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * maxEncryptBlock;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }


    private static Cipher getCipher(KeyFactory keyFactory, Key privateK, int encryptMode) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(encryptMode, privateK);
        return cipher;
    }

}
