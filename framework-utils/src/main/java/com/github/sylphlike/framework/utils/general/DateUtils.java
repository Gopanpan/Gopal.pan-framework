package com.github.sylphlike.framework.utils.general;

import com.github.sylphlike.framework.utils.enums.DateEnums;
import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;


/**
 * 时间工具类，jdk版本应大于等于1.8
 * <p>  time 17:56 2021/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class DateUtils {


    /**
     * 时间转字符串形式,缺省格式为：yyyy-MM-dd HH:mm:ss
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String date2String(){
        return date2String(LocalDateTime.now(), DateEnums.HYPHEN_YYYYMMddHHmmss.getPatterns());

    }


    /**
     * 时间转字符串形式,缺省格式为：yyyy-MM-dd HH:mm:ss
     * @param date     java LocalDateTime
     * @param patterns 格式
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String date2String(LocalDateTime date,String patterns){

        if(null ==date){return null;}


        if(StringUtils.isEmpty(patterns)){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateEnums.HYPHEN_YYYYMMddHHmmss.getPatterns());
            return date.format(formatter);
        } else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patterns);
            return date.format(formatter);
        }
    }



    /**
     * 字符串转 LocalDateTime
     * @param date      时间字符串
     * @param patterns  时间字符串格式
     * @return  java.time.LocalDateTime
     * @author  Gopal.pan
     */
    public static LocalDateTime string2DateTime(String date,String patterns){
        if(StringUtils.isEmpty(date)){return null;}
        if(StringUtils.isEmpty(patterns)) {return null;}

        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(patterns));

    }


    /**
     * 字符串转 LocalDateTime
     * @param date      时间字符串
     * @param patterns  时间字符串格式
     * @return  java.time.LocalDate
     * @author  Gopal.pan
     */
    public static LocalDate string2Date(String date,String patterns){
        if(StringUtils.isEmpty(date)){return null;}
        if(StringUtils.isEmpty(patterns)) {return null;}

        return LocalDate.parse(date, DateTimeFormatter.ofPattern(patterns));

    }


    /**
     * 获取当前时间的指定字符串格式,缺省格式为：yyyy-MM-dd HH:mm:ss
     * @param patterns  时间格式
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String getCurrentDate(String patterns){
        LocalDateTime localDateTime = LocalDateTime.now();
        if(StringUtils.isEmpty(patterns)){
            return localDateTime.format(DateTimeFormatter.ofPattern(DateEnums.HYPHEN_YYYYMMddHHmmss.getPatterns()));
        } else {
            return localDateTime.format(DateTimeFormatter.ofPattern(patterns));
        }


    }


    /**
     * 前时间加上或减去自定义天数
     * @param days  加减天数
     * @return  java.time.LocalDateTime
     * @author  Gopal.pan
     */
    public static LocalDateTime plusDays(int days){
        LocalDateTime now = LocalDateTime.now();
        return now.plusDays(days);
    }



    /**
     * 当前时间所在月份的第一天
     * @return  java.time.LocalDateTime
     * @author  Gopal.pan
     */
    public static LocalDateTime firstDayOfMonth(){
        LocalDateTime now = LocalDateTime.now();
        return now.with(TemporalAdjusters.firstDayOfMonth());
    }


    /**
     * 当前时间所在月份的最后一天
     * @return  java.time.LocalDateTime
     * @author  Gopal.pan
     */
    public static LocalDateTime lastDayOfMonth(){
        LocalDateTime now = LocalDateTime.now();
        return now.with(TemporalAdjusters.lastDayOfMonth());
    }


    /**
     * 获取今天是星期几
     * @return  int
     * @author  Gopal.pan
     */
    public static int dayOfWeek(){
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        return dayOfWeek.getValue();
    }


    /**
     * 对比两个时间差
     * @param contrast          对比时间
     * @param byContrast        被对比时间
     * @param dateEnums         对比类型（毫秒、秒、分、时、天）
     * @return  java.lang.Long
     * @author  Gopal.pan
     */
    public static Long durationTime(LocalDateTime contrast,LocalDateTime byContrast,DateEnums dateEnums){
        if(null == byContrast || null == contrast){return null;}

        Duration duration = Duration.between(byContrast, contrast);
        String patterns = dateEnums.getPatterns();
        switch (patterns){
            case "millis":
                return duration.toMillis();
            case "abs_millis":
                return duration.abs().toMillis();

            case "seconds":
                return duration.getSeconds();
            case "abs_seconds":
                return duration.abs().getSeconds();

            case "minutes":
                return duration.toMinutes();
            case "abs_minutes":
                return duration.abs().toMinutes();

            case "hours":
                return duration.toHours();
            case "abs_hours":
                return duration.abs().toHours();

            case "days":
                return duration.toDays();
            case "abs_days":
                return duration.abs().toDays();

            default:
                return null;
        }
    }


    /**
     * 对比年
     * @param datumTime         比数
     * @param compareTime       对比日期
     * @param compareMinutiae   是否比较日
     * @return  int
     * @author  Gopal.pan
     */
    public static int dateToCompareYear(LocalDate datumTime, LocalDate compareTime,boolean compareMinutiae) {
        int datumTimeYear = datumTime.getYear();
        int compareTimeYear = compareTime.getYear();
        int datumTimeDayOfYear = datumTime.getDayOfYear();
        int compareDayOfYear = compareTime.getDayOfYear();

        int year = compareTimeYear - datumTimeYear;

        if(compareMinutiae){
            int dayOfYear = compareDayOfYear - datumTimeDayOfYear;
            if(dayOfYear < 0){
                year = year - 1;
            }
        }

        return year;
    }


    /**
     * 当前时间年份
     * @return  int
     * @author  Gopal.pan
     */
    public static int thisYear() {
        return LocalDate.now().getYear();
    }


    /**
     * 是否闰年
     * @param year  当前年
     * @return  boolean
     * @author  Gopal.pan
     */
    public static boolean isLeapYear(int year) {
        return LocalDate.of(year,1,1).isLeapYear();
    }



}
