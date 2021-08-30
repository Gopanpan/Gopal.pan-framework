package com.github.sylphlike.framework.mq.core;

import com.aliyun.openservices.ons.api.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>  time 15:33 2021/08/30  星期一 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
@DependsOn("springContextUtil")
@Component
public class ConsumerBeanInit implements InitializingBean, DisposableBean, ApplicationContextAware {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private ApplicationContext context;

    private static final List<Consumer> consumers = new ArrayList<>();

    @Resource
    private MQProperties mqProperties;

    @Bean
    @ConditionalOnMissingBean
    public MQProperties onsProperties(){return new MQProperties();};


    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) this.context.getAutowireCapableBeanFactory();

        Reflections reflections;
        String reflectionPackage = mqProperties.getReflectionPackage();
        if(StringUtils.isEmpty( reflectionPackage)){
            reflections = new Reflections("com.github.sylphlike");
        }else {
            reflections = new Reflections(reflectionPackage);
        }

        LOGGER.info("【FW-mq】 instantiate MQ consumer");
        Set<Class<? extends AbstractMQConsumer>> absConsumer = reflections.getSubTypesOf(AbstractMQConsumer.class);
        for (Class<? extends AbstractMQConsumer> aClass : absConsumer) {
            AbstractMQConsumer abstractMQConsumer = aClass.getDeclaredConstructor().newInstance();
            Class<? extends AbstractMQConsumer> instanceClass = abstractMQConsumer.getClass();
            BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(abstractMQConsumer.getClass());
            definition.setInitMethodName("init");
            definition.addPropertyValue("mqProperties",mqProperties);

            String simpleName = instanceClass.getSimpleName();
            beanFactory.registerBeanDefinition(simpleName, definition.getRawBeanDefinition());


            AbstractMQConsumer bean = beanFactory.getBean(abstractMQConsumer.getClass());
            consumers.add( bean.consumer);

        }

    }

    @Override
    public void destroy() throws Exception {
        for (Consumer consumer : consumers) {
            consumer.shutdown();
        }
        LOGGER.info("【FW-mq】 shutting down finished ");
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;

    }
}
