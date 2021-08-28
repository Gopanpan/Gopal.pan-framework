package com.github.sylphlike.framework.utils.excel.support;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>  time 17:56 2018/12/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class Util {




    /**
     * 根据反射，通过方法名称获取方法值，忽略大小写的
     * @param methodName    方法名称
     * @param obj           对象
     * @param args          参数
     * @return  java.lang.Object
     * @author  Gopal.pan
     */
    private static <T> Object getMethodValue(String methodName, T obj, Object... args) {
        try {
            Method method = obj.getClass().getDeclaredMethod(methodName);
            return  method.invoke(obj, args);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 通过code值获取对应的描述信息
     * @param code           code
     * @param enumT          枚举类
     * @param methodNames    方法名称
     * @return  java.lang.Object
     * @author  Gopal.pan
     */
    public static <T> Object getDescriptionByByCode(Object code, Class<T> enumT, String... methodNames) {

        T[] enums = enumT.getEnumConstants();

        for (T obj:enums) {

            Object resultValue = getMethodValue(methodNames[0], obj);
            if (resultValue.toString().equals(code + "")) {
                return getMethodValue(methodNames[1], obj);
            }
        }

        return "";
    }




    /**
     * 枚举转map结合code 作为map的key,description作为map的value
     * @param enumT          枚举类
     * @param methodNames    方法名称
     * @return  java.util.Map
     * @author  Gopal.pan
     */
    public static <T> Map<Object, String> EnumToMap(Class<T> enumT, String... methodNames) {
        Map<Object, String> enumMap = new HashMap<Object, String>();
        if (!enumT.isEnum()) {
            return enumMap;
        }
        T[] enums = enumT.getEnumConstants();

        for (T obj : enums) {
            try {
                Object resultValue = getMethodValue(methodNames[0], obj);
                if ("".equals(resultValue)) {
                    continue;
                }
                Object resultDes = getMethodValue(methodNames[1], obj);
                if ("".equals(resultDes)) {
                    resultDes = obj;
                }
                enumMap.put(resultValue, resultDes + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return enumMap;
    }

}
