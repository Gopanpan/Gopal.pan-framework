package com.github.sylphlike.framework.boxing;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * spring应用上下文
 * <p>  time 17:56 2019/05/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

@Component
public class UniteContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     * @param applicationContext  ApplicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        UniteContextUtil.applicationContext = applicationContext;
    }

    /**
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    /**
     * 获取对象 这里重写了bean方法，起主要作用
     * @param name bean名称
     * @return  java.lang.Object
     * @throws  BeansException ex
     * @author  Gopal.pan
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }



    /**
     * 根据类型获取bean
     * @param clazz   class
     * @return  T
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



}
