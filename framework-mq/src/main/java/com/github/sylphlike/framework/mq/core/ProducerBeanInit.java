package com.github.sylphlike.framework.mq.core;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Properties;

/**
 * <p>  time 15:42 2021/08/30  星期一 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
@Configuration
public class ProducerBeanInit {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource private MQProperties mqProperties;

    @Bean
    @ConditionalOnMissingBean
    public MQProperties mqProperties(){return new MQProperties();}



    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean oneProducer() {
        LOGGER.info("【FW-mq】instantiate MQ producer");

        ProducerBean producerBean = new ProducerBean();
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getServerUrl());
        properties.put(PropertyKeyConst.AccessKey, mqProperties.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, mqProperties.getSecretKey());
        properties.put(PropertyKeyConst.GROUP_ID, StringUtils.join( mqProperties.getGroupIdPrefix(),".","producer"));
        producerBean.setProperties(properties);


        return producerBean;
    }

}
