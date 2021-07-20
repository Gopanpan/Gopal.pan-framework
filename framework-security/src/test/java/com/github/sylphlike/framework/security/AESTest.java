package com.github.sylphlike.framework.security;

import java.util.StringJoiner;

/**
 * <p>  time 17:36 2021/04/14  星期三 </p>
 * <p> email 15923508369@163.com     </P>
 *
 * @author Gopal.pan
 * @version 1.0.0
 */
public class AESTest {

    public static void main(String[] args) {
        String stringKey = AESEncrypt.createStringKey();


        String encrypt = AESEncrypt.encrypt("!QAZx4r4ssfggttsw23eDCfER%1234er56ty7u", stringKey);
        System.out.println("手机号 "+ encrypt);
        System.out.println(encrypt.length());



        String idCard = AESEncrypt.encrypt("50023519901104401X", stringKey);
        System.out.println("idCard "+ idCard);
        System.out.println(idCard.length());


    }
}
