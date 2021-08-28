package com.github.sylphlike.framework.utils.general;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>  time 17:56 2018/06/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class RandomUtils {


    /** 用于随机选的数字 */
    public static final String BASE_NUMBER = "0123456789";

    /** 用于随机选的字符 */
    public static final String BASE_CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    public static final String BASE_CHAR_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /** 用于随机选的字符和数字*/
    public static final String BASE_MIX = BASE_CHAR_LOWER + BASE_NUMBER + BASE_CHAR_UPPER;




    /**
     * 获得指定范围内的随机数[min, max)
     * @param min 最小数（包含）
     * @param max 最大数（不包含）
     * @return  long
     * @author  Gopal.pan
     */
    public static long betweenLongRandom(long min, long max) {
        return getRandom().nextLong(min, max);
    }



    /**
     * 获得指定范围内的随机数[min, max)
     * @param min 最小数（包含）
     * @param max 最大数（不包含）
     * @return  long
     * @author  Gopal.pan
     */
    public static long betweenIntRandom(int min, int max) {
        return getRandom().nextInt(min, max);
    }


    /**
     * 获得一个随机的字符串
     * @param baseString 随机字符选取的样本
     * @param length     字符串的长度
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String randomString(String baseString, int length) {
        if (null == baseString || "".equals(baseString)) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(length);

        if (length < 1) {
            length = 1;
        }
        int baseLength = baseString.length();
        for (int i = 0; i < length; i++) {
            int number = randomInt(baseLength);
            sb.append(baseString.charAt(number));
        }
        return sb.toString();
    }


    /**
     * 获取指定长度的随机数字
     * @param length 字符串的长度
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String randomNumber(int length){
        final StringBuilder sb = new StringBuilder(length);
        int originLength = BASE_NUMBER.length();
        for (int i = 0; i < length; i++) {
            int number = randomInt(originLength);
            sb.append(BASE_NUMBER.charAt(number));
        }
        return sb.toString();
    }



    /**
     * 获取指定长度的小写字符
     * @param length 字符串的长度
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String randomCharLower(int length){
        final StringBuilder sb = new StringBuilder(length);
        int originLength = BASE_CHAR_LOWER.length();
        for (int i = 0; i < length; i++) {
            int number = randomInt(originLength);
            sb.append(BASE_CHAR_LOWER.charAt(number));
        }
        return sb.toString();
    }


    /**
     * 获取指定长度的小写字符
     * @param length 字符串的长度
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String randomCharUpper(int length){
        final StringBuilder sb = new StringBuilder(length);
        int originLength = BASE_CHAR_UPPER.length();
        for (int i = 0; i < length; i++) {
            int number = randomInt(originLength);
            sb.append(BASE_CHAR_UPPER.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 获取指定长度的数字字母组合
     * @param length 字符串的长度
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String randomMix(int length){
        final StringBuilder sb = new StringBuilder(length);
        int originLength = BASE_MIX.length();
        for (int i = 0; i < length; i++) {
            int number = randomInt(originLength);
            sb.append(BASE_MIX.charAt(number));
        }
        return sb.toString();
    }


    /**
     * 获得指定范围内的随机数 [0,limit)
     *
     * @param limit 限制随机数的范围，不包括这个数
     * @return 随机数
     */
    public static int randomInt(int limit) {
        return getRandom().nextInt(limit);
    }


    /**
     * 获取随机数生成器对象
     * ThreadLocalRandom是JDK 7之后提供并发产生随机数，能够解决多个线程发生的竞争争夺。
     * @return {@link ThreadLocalRandom}
     */
    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }



}
