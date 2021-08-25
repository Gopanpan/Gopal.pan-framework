package com.github.sylphlike.framework.utils.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>  time 17:56 2021/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class BeanMapUtils {
    private static final Logger LOGGER =  LoggerFactory.getLogger(BeanMapUtils.class);


    /**
     * 对象转map
     * <p>  time 15:02 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param obj   待转的实体对象
     * @return  java.util.Map
     * @author  Gopal.pan
     */
    public static Map<String, Object> beanTrans2Map(Object obj) {

        if (null == obj){
            return null;
        }

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            Map<String, Object> map = new HashMap<>();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (!key.equals("class")) {
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    map.put(key, value);
                }
            }
            return map;
        } catch (Exception e) {
            LOGGER.error("【FW-utils】对象转map发生异常",e);
        }
        return null;
    }

    /**
     * map 转Java bean
     * <p>  time 15:02 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param map         Map对象
     * @param beanClass   实体对象
     * @return  T         转换后的实体对象
     * @author  Gopal.pan
     */
    public static  <T> T mapTransBean(Map<String, String> map, Class<T> beanClass) {

        try{
            if (map == null){
                return null;
            }
            T obj = beanClass.getDeclaredConstructor().newInstance();

            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if(Modifier.isStatic(mod) || Modifier.isFinal(mod)){
                    continue;
                }
                field.setAccessible(true);
                field.set(obj, map.get(field.getName()));
            }
            return obj;
        }catch (Exception e){
            LOGGER.error("【FW-utils】maps转对象时发生异常",e);
        }

        return null;
    }
}
