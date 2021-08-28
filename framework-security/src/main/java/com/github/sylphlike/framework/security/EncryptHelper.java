package com.github.sylphlike.framework.security;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 *
 * <p>  time 17:56 2010/10/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class EncryptHelper {

    public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };


    /**
     * 参数字典顺序排序
     * @param params       待排序的参数集合
     * @param filterParams 需要过滤的参数
     * @return String  排序后并按照url请求参数格式的字符串参数
     */
    private static String sortedParam( Map<String, String> params,List<String>filterParams) {

        List<String> paramsList = Lists.newArrayList(params.keySet());
        Collections.sort(paramsList);
        StringBuilder stringBuilder = new StringBuilder();
        for (String key: paramsList) {
            if(filterParams.contains(key)){
                continue;
            }
            String value = params.get(key);
            if(null == value){
                stringBuilder.append("&").append(key).append("=");
            }else {
                stringBuilder.append("&").append(key).append("=").append(value);
            }

        }
        return StringUtils.removeStart(stringBuilder.toString(),"&");


    }



    /**
     * 6位随机数
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String sixUuid() {
        StringBuilder shortBuffer = new StringBuilder();
        String uuid = UUID.randomUUID().toString();
        for (int i = 0; i < 6; i++) {
            String str = uuid.substring(i * 6, i * 6 + 6);
            int x = Integer.parseInt(str.replace("-",""), 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();

    }



}
