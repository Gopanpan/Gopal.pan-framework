package com.github.sylphlike.framework.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


/**
 * <p>  time 17:56 2017/03/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class Base64Utils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Base64Utils.class);



	/**
	 * base64加密字符串
     * <p>  time 18:27 2021/1/29      </p>
     * <p> email 15923508369@163.com  </p>
	 * @param str   待加密字符串
	 * @return  java.lang.String
	 * @author  Gopal.pan
	 */
	public static String encodeBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes(StandardCharsets.UTF_8);

            Base64.Encoder encoder = Base64.getEncoder();
            s = encoder.encodeToString(b);
        } catch (Exception e) {
            LOGGER.error("【FW-security】 base64加密异常",e);
        }


        return s;
	}



    /**
     * base64加密
     * <p>  time 18:27 2021/1/29      </p>
     * <p> email 15923508369@163.com  </p>
     * @param content  待加密byte数组
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String encodeBase64(byte[] content) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(content);
    }




	/**
	 * base64解密
     * <p>  time 18:27 2021/1/29      </p>
     * <p> email 15923508369@163.com  </p>
	 * @param str   Base64加密字符串
	 * @return  java.lang.String
	 * @author  Gopal.pan
	 */
	public static String decodeBase64(String str) {
        String result = null;
        try {
            byte[] b = null;
            if (str != null) {
                Base64.Decoder decoder1 = Base64.getDecoder();
                byte[] decode = decoder1.decode(str);
                result = new String(decode, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            LOGGER.error("【FW-security】 base64解密异常",e);
        }
        return result;
	}

}
