package com.github.sylphlike.framework.security;


import java.util.Map;

/**
 * <p>  time 15:23 2021/06/23  星期三 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
public class RSATest {

    public static void main(String[] args) throws Exception {
        Map<String, String> keyMap = RSAEncrypt.genKeyPair();

        String publicKey = keyMap.get(RSAEncrypt.PUBLIC_KEY);
        String privateKey = keyMap.get(RSAEncrypt.PRIVATE_KEY);

        String data = "!QAZx4r4ssfggttsw23eDCfER%1234er56ty7u";
        String sign = RSAEncrypt.sign(data, privateKey);
        System.out.println(sign);
        System.out.println(sign.length());




    }
}
