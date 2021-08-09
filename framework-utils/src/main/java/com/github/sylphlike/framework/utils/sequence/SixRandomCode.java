package com.github.sylphlike.framework.utils.sequence;

import java.util.Random;

public class SixRandomCode {

    /** 自定义进制 */
    private static final char[] CHARS = new char[]{'Q', 'W', 'E', '8', 'S', '2', 'D', 'Z', 'X', '9', 'C',
            '7', 'P', '5', 'K', '3', 'M', 'J', 'U', 'F', 'R', '4', 'V', 'Y', 'T', 'N', '6', 'B', 'G', 'H'};

    /** 进制长度 */
    private static final int BASE_LENGTH = CHARS.length;

    /** 定义一个字符用来补全邀请码长度（该字符前面是计算出来的邀请码，后面是用来补全用的）*/
    private static final char COMPLETION = 'a';

    /** 随机码长度*/
    private static final int CODE_LENGTH = 6;


    /**
     *  根据 序列生成随机码
     * @param sequence  int类型参数 最大不能超过 728999999
     * @return String
     * @author  Gopal.pan
     */
    public static String toSerialCode(int sequence) {
        char[] buf = new char[32];
        int charPos = 32;

        while ((sequence / BASE_LENGTH) > 0) {
            int ind = (int) (sequence % BASE_LENGTH);
            buf[--charPos] = CHARS[ind];
            sequence /= BASE_LENGTH;
        }
        buf[--charPos] = CHARS[(int) (sequence % BASE_LENGTH)];
        String str = new String(buf, charPos, (32 - charPos));
        // 不够长度的自动随机补全
        if (str.length() < CODE_LENGTH) {
            StringBuilder sb = new StringBuilder();
            sb.append(COMPLETION);
            Random rnd = new Random();
            for (int i = 1; i < CODE_LENGTH - str.length(); i++) {
                sb.append(CHARS[rnd.nextInt(BASE_LENGTH)]);
            }
            str += sb.toString();
        }
        return str;
    }


    /**
     * 根据随机码反向生成序列号
     * @param code  随机码
     * @return int
     * @author  Gopal.pan
     */
    public static int codeToId(String code) {
        char[] chs = code.toCharArray();
        int res = 0;
        for (int i = 0; i < chs.length; i++) {
            int ind = 0;
            for (int j = 0; j < BASE_LENGTH; j++) {
                if (chs[i] == CHARS[j]) {
                    ind = j;
                    break;
                }
            }
            if (chs[i] == COMPLETION) {
                break;
            }
            if (i > 0) {
                res = res * BASE_LENGTH + ind;
            } else {
                res = ind;
            }
        }
        return res;
    }

}
