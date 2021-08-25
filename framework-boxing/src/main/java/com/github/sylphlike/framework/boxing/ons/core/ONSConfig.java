package com.github.sylphlike.framework.boxing.ons.core;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PropertyValueConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import com.github.sylphlike.framework.boxing.UniteConfigException;
import com.github.sylphlike.framework.boxing.ons.api.AbstractConsumer;
import com.github.sylphlike.framework.boxing.ons.api.MessageDefinition;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.*;

/**
 * <p>  time 17:56 2019/03/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

@Configuration
public class ONSConfig<T>  {

    private final Logger LOGGER = LoggerFactory.getLogger(ONSConfig.class);


    @Autowired private ONSProperties onsProperties;
    @Autowired private  ConsumerChooser<T> consumerChooser;

    @Bean
    @ConditionalOnMissingBean
    public ONSProperties onsProperties(){return new ONSProperties();};



    @ConditionalOnProperty(prefix ="sylphlike.framework.ons",name = "enable",havingValue = "true")
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean oneProducer() {
        LOGGER.info("【FW-boxing】实例化MQ生产者");

        ProducerBean producerBean = new ProducerBean();
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.NAMESRV_ADDR, onsProperties.getNamesrvAddr());
        properties.put(PropertyKeyConst.AccessKey, onsProperties.getAccesskey());
        properties.put(PropertyKeyConst.SecretKey, onsProperties.getSecretKey());
        properties.put(PropertyKeyConst.GROUP_ID, onsProperties.getGroupId());
        producerBean.setProperties(properties);


        return producerBean;
    }


    /**
     * 消息接收端
     * <p>  time 18:24 2021/1/29      </p>
     * <p> email 15923508369@163.com  </p>
     * @return  com.aliyun.openservices.ons.api.bean.ConsumerBean
     * @throws  UniteConfigException ex
     * @author  Gopal.pan
     */
    @ConditionalOnProperty(prefix ="sylphlike.framework.ons",name = "enable",havingValue = "true")
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean consumerBean() throws UniteConfigException {
        LOGGER.info("【FW-boxing】实例化MQ消费者");

        Properties properties = new Properties();
        properties.put(PropertyKeyConst.NAMESRV_ADDR, onsProperties.getNamesrvAddr());
        properties.put(PropertyKeyConst.AccessKey, onsProperties.getAccesskey());
        properties.put(PropertyKeyConst.SecretKey, onsProperties.getSecretKey());
        properties.put(PropertyKeyConst.GROUP_ID, onsProperties.getGroupId());
        properties.put(PropertyKeyConst.MessageModel, PropertyValueConst.CLUSTERING);
        properties.put(PropertyKeyConst.MaxReconsumeTimes, 16);

        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(properties);

        List<Subscription> subscriptions = dealMessageDefinition();


        Map<Subscription, MessageListener> map = new HashMap<>(2);
        for (Subscription subscription : subscriptions) {
            AbstractConsumer<T> choose = consumerChooser.choose(subscription.getTopic() + ":" + subscription.getExpression());
            if(choose != null){
                LOGGER.info("【FW-boxing】消费端实例[{}]",choose);
                map.put(subscription, choose);

            }
        }
        consumerBean.setSubscriptionTable(map);
        return consumerBean;
    }

    private List<Subscription> dealMessageDefinition() throws UniteConfigException {

        Reflections reflections = new Reflections("com.github.sylphlike");

        Map<String,String> map;
        try {
            Set<Class<? extends MessageDefinition>> messageDefinition = reflections.getSubTypesOf(MessageDefinition.class);
            map = new HashMap<>();
            for (Class<? extends MessageDefinition> monitor : messageDefinition) {
                MessageDefinition[] enumConstants = monitor.getEnumConstants();
                Method getTopic = monitor.getMethod("getTopic");
                Method getTags = monitor.getMethod("getTag");


                for (Object obj : enumConstants) {
                    String topic = (String) getTopic.invoke(obj);
                    String tag = (String) getTags.invoke(obj);

                    if(map.containsKey(topic)){
                        String existTag = map.get(topic);
                        String updateTag = existTag + " || " + tag;
                        map.put(topic,updateTag);
                    }else {
                        map.put(topic,tag);
                    }
                }
            }
        } catch (Exception e) {
            throw new UniteConfigException("配置MQ主题时异常");
        }

        List<Subscription> list = new ArrayList<>();
        map.forEach((k,v) ->{
            Subscription subscription = new Subscription();
            subscription.setTopic(k);
            subscription.setExpression(v);
            list.add(subscription);
        });
        return list;
    }


}
