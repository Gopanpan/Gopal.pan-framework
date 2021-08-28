package com.github.sylphlike.framework.adapt;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * spring 应用上下文
 * <p>  time 17:56 2019/10/29  星期五 </p>
 * <p> email 15923508369@163.com     </p>
 * @author Gopal.pan
 * @version 1.0.0
 */

@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     * @param applicationContext applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }



    /**
     * 根据名称获取对象
     * @param name   bean名称
     * @return  java.lang.Object
     * @throws  BeansException ex
     * @author  Gopal.pan
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }




    /**
     * 根据类型获取bean
     * @param clazz  class
     * @return  T    类实例
     * @author  Gopal.pan
     */
    public static <T> T getBean(Class<T> clazz) {
        T t = null;
        Map<String, T> map = applicationContext.getBeansOfType(clazz);
        for (Map.Entry<String, T> entry : map.entrySet()) {
            t = entry.getValue();
        }
        return t;
    }


    /**
     * 根据类型获取该实例下的所有子类
     * @param clazz  类实例
     * @return  java.util.Map
     * @author  Gopal.pan
     */
    public static <T> Map<String, T>  getBeansOfType(Class<T> clazz){
        return applicationContext.getBeansOfType(clazz);
    }




    /**
     * 获取当前环境
     * @return String
     * @author  Gopal.pan
     */
    public static String getActiveProfile() {
        return applicationContext.getEnvironment().getActiveProfiles()[0];
    }
}
