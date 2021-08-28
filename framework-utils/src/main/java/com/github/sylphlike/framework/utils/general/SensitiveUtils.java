package com.github.sylphlike.framework.utils.general;

import org.apache.commons.lang3.StringUtils;

/**
 * 脱敏工具类
 * <p>  time 17:56 2018/06/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
public class SensitiveUtils {


    /**
     * 只显示第一个汉字，其他隐藏为2个星号
     * @param fullName  明文中文名称
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String  chineseName(String fullName){
        if (StringUtils.isBlank(fullName)) {
            return "";
        }
        String name = StringUtils.left(fullName, 1);
        return StringUtils.rightPad(name, 3, "*");
    }



    /**
     * 身份证号 前六位，后四位，其他用星号隐藏每位1个星号
     * @param cardNum   份证号
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String idCardNum(String cardNum) {
        String out = "";
        if(StringUtils.isNotEmpty(cardNum)){
            out = StringUtils.left(cardNum, 6).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(cardNum, 4), StringUtils.length(cardNum), "*"), "******"));
        }
        return out;
    }


    /**
     * 固定电话 后四位，其他隐藏
     * @param num 固定电话
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String fixedPhone(String num) {
        String out = "";
        if(StringUtils.isNotEmpty(num)){
            out = StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*");
        }
        return out;
    }


    /**
     * 手机号码 前三位，后四位
     * @param num  手机号码
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String mobilePhone(String num) {
        if (StringUtils.isBlank(num)) {
            return "";
        }
        return StringUtils.left(num, 3).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*"), "***"));
    }


    /**
     * 地址 前六位
     * @param address  地址
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String address(String address) {
        String out = "";
        if(StringUtils.isNotEmpty(address)){
            String name = StringUtils.left(address, 6);
            out =  StringUtils.rightPad(name, StringUtils.length(address), "*");
        }
        return out;
    }




    /**
     * 电子邮件 @符号前三位
     * @param email 电子邮件
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String email(String email) {
        if (StringUtils.isBlank(email)) {
            return "";
        }
        int index = StringUtils.indexOf(email, "@");
        if (index <= 1) {
            return email;
        } else {
            return StringUtils.rightPad(StringUtils.left(email, 3), index, "*").concat(StringUtils.mid(email, index, StringUtils.length(email)));
        }
    }



    /**
     *  银行卡号 前六位，后四位，其他用星号隐藏每位1个星号
     * @param cardNum 银行卡号
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String bankCard(String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return "";
        }
        return StringUtils.left(cardNum, 6).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(cardNum, 4), StringUtils.length(cardNum), "*"), "******"));
    }


    /**
     * 车牌号 前两位后一位
     * @param carNumber 车牌号
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String carNumber(String carNumber) {
        if (StringUtils.isBlank(carNumber)) {
            return "";
        }
        return StringUtils.left(carNumber, 2).
                concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(carNumber, 1), StringUtils.length(carNumber), "*"), "**"));

    }

}
