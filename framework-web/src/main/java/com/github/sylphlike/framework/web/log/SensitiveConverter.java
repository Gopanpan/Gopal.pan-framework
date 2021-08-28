package com.github.sylphlike.framework.web.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.sylphlike.framework.utils.validator.PatternPool;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;

/**
 * 日志脱敏转换器
 * <p>已实现脱敏字段：   1 身份证号  2 手机号码 3 银行卡号 4 邮箱地址
 *    使用方法. 需要在业务系统的日志配置文件中增加 转换规则配置: <conversionRule conversionWord="m" converterClass="com.github.sylphlike.web.log.SensitiveConverter"> </conversionRule>
 * </p>
 * <p>  time 17:56 2019/06/12  星期五 </p>
 * <p> email 15923508369@163.com     </p>
 * @author Gopal.pan
 * @version 1.0.0
 */
public class SensitiveConverter extends ClassicConverter {


    @Override
    public String convert(ILoggingEvent event){

        String originalLog = event.getFormattedMessage();
        if(StringUtils.isBlank(originalLog)) {
            return originalLog;
        }
        String content;
        content = idCard(originalLog);
        content = mobile(content);
        content = bankCard(content);
        content = email(content);
        return content;
    }

    /**
     * 身份证号 前4后4，其他隐藏
     * @param content  原始带脱敏字符串
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    private static String idCard(String content){
        Matcher matcher = PatternPool.CITIZEN_ID_18.matcher(content);
        StringBuilder stringBuffer = new StringBuilder() ;
        while(matcher.find()){
            matcher.appendReplacement(stringBuffer, desensitization(matcher.group(), 4, 4));
        }
        matcher.appendTail(stringBuffer) ;
        return stringBuffer.toString();
    }



    /**
     * 手机号码 前三位，后四位，其他隐藏 138******1234
     * @param content 原始带脱敏字符串
     * @return java.lang.String
     * @author Gopal.pan
     */
    private static String mobile(String content){
        Matcher matcher = PatternPool.MOBILE.matcher(content);
        StringBuffer sb = new StringBuffer() ;
        while(matcher.find()){
            matcher.appendReplacement(sb, desensitization(matcher.group(), 3, 4)) ;
        }
        matcher.appendTail(sb) ;
        return sb.toString();
    }


    /**
     * 银行卡号 前三位，后四位，其他隐藏 622******3499
     * @param content 原始带脱敏字符串
     * @return java.lang.String
     * @author Gopal.pan
     */
    private static String bankCard(String content){
        Matcher matcher = PatternPool.BANK_CARD.matcher(content);
        StringBuilder sb = new StringBuilder() ;
        while(matcher.find()){
            matcher.appendReplacement(sb, desensitization(matcher.group(), 3, 4)) ;
        }
        matcher.appendTail(sb) ;
        return sb.toString();
    }




    /**
     * 邮箱地址 前3，其他隐藏 。
     * @param content 原始带脱敏字符串
     * @return java.lang.String
     * @author Gopal.pan
     */
    private String email(String content) {
        Matcher matcher = PatternPool.EMAIL.matcher(content);
        StringBuilder sb = new StringBuilder() ;
        while(matcher.find()){
            String group = matcher.group();
            int i = group.lastIndexOf("@");
            String left = StringUtils.left(group,i);
            matcher.appendReplacement(sb, StringUtils.join( desensitization(left, 3, 4),group.substring(i))) ;
        }
        matcher.appendTail(sb) ;
        return sb.toString();
    }



    /**
     *  基础脱敏处理 指定起止展示长度 剩余用"*"中字符替换
     * @param str               待脱敏的字符串
     * @param startLength       开始展示长度
     * @param endLength         末尾展示长度
     * @return java.lang.String
     * @author Gopal.pan
     */
    private static String desensitization(String str, int startLength, int endLength) {
        if (StringUtils.isBlank(str)) { return ""; }
        String replacement = str.substring(startLength,str.length() - endLength);
        return StringUtils.left(str, startLength).concat(StringUtils.leftPad(StringUtils.right(str, endLength), str.length() - startLength, "*".repeat(replacement.length())));
    }

}
