package com.github.sylphlike.framework.boxing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 对象序列化
 * <p>  time 17:56 2019/05/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class GoSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoSerializer.class);



    /**
     * 对象序列化
     * <p>  time 18:24 2021/1/29      </p>
     * <p> email 15923508369@163.com  </p>
     * @param obj 待序列化对象
     * @return  byte[]
     * @author  Gopal.pan
     */
    public static byte[] serialize(Object obj)  {
        ByteArrayOutputStream bis = null;
        ObjectOutputStream os = null;
        byte[] byteArray = null;

        try {
            bis = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bis);
            os.writeObject(obj);
            os.flush();
            byteArray = bis.toByteArray();
        } catch (IOException e) {
            LOGGER.error("【framework-boxing】 序列化异常",e);
        } finally {
            closeOutputStream(os, bis);
        }

        return byteArray;
    }



    /**
     * 反序列化
     * <p>  time 18:24 2021/1/29      </p>
     * <p> email 15923508369@163.com  </p>
     * @param byteArray  字节数组
     * @return  java.lang.Object
     * @author  Gopal.pan
     */
    public static Object deSerialize(byte[] byteArray){
        ByteArrayInputStream bos = null;
        ObjectInputStream ois = null;
        Object obj = null;

        try {
            bos = new ByteArrayInputStream(byteArray);
            ois = new ObjectInputStream(bos);
            obj = ois.readObject();
        } catch (Exception e) {
            LOGGER.error("【framework-boxing】 反序列化异常",e);
        } finally {
            closeInputStream(bos, ois);
        }

        return obj;
    }



    private static void closeInputStream(ByteArrayInputStream inputStream, ObjectInputStream ois)  {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.error("【framework-boxing】 关闭输入流时异常",e);
            }
        }

        if (ois != null) {
            try {
                ois.close();
            } catch (IOException e) {
                LOGGER.error("【framework-boxing】 关闭输入流时异常",e);
            }
        }

    }



    private static void closeOutputStream(ObjectOutputStream outputStream, ByteArrayOutputStream bis) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                LOGGER.error("【framework-boxing】 关闭输出流时异常",e);
            }
        }

        if (bis != null) {
            try {
                bis.close();
            } catch (IOException e) {
                LOGGER.error("【framework-boxing】 关闭输出流时异常",e);
            }
        }

    }



}
