package com.github.sylphlike.framework.utils.general;

import com.github.sylphlike.framework.utils.UtilException;

import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>  time 16:10 2021/07/20  星期二 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
public class RandomPhone {

    //中国移动
    private static final String[] CHINA_MOBILE = { "134", "135", "136", "137", "138", "139",
            "150", "151", "152", "157", "158", "159","182", "183", "184", "187", "188", "178",
            "147", "172", "198"};
    //中国联通
    private static final String[] CHINA_UNICOM = { "130", "131", "132", "145", "155", "156",
            "166", "171", "175", "176", "185", "186", "166"};
    //中国电信
    private static final String[] CHINA_TELECOME = { "133", "149", "153", "173", "177", "180",
            "181", "189", "199"};




    /**
     *  随机生成手机号码
     * @param op   运营商 0移动，1联通，2电信
     * @return String
     * @author  Gopal.pan
     */
    public static String createMobile(int op) {
        StringJoiner js = new StringJoiner("");
        String isp;
        switch (op) {
            case 0:
                isp = CHINA_MOBILE[ThreadLocalRandom.current().nextInt(CHINA_MOBILE.length)];
                break;
            case 1:
                isp = CHINA_UNICOM[ThreadLocalRandom.current().nextInt(CHINA_UNICOM.length)];
                break;
            case 2:
                isp = CHINA_TELECOME[ThreadLocalRandom.current().nextInt(CHINA_TELECOME.length)];
                break;
            default:
                throw new UtilException("运营商不在指定范围内");
        }
        js.add(isp);
        //生成手机号后8位
        int suffix = ThreadLocalRandom.current().nextInt(10000000, 99999999);
        js.add(String.valueOf(suffix));

        return js.toString();
    }


}
