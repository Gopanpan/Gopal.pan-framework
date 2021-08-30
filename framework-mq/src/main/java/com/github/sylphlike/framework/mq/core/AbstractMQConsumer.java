package com.github.sylphlike.framework.mq.core;

import com.aliyun.openservices.ons.api.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sylphlike.framework.adapt.Constants;
import com.github.sylphlike.framework.adapt.EnvProfile;
import com.github.sylphlike.framework.adapt.SpringContextUtil;
import com.github.sylphlike.framework.adapt.cache.UUIDCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * <p>  time 15:17 2021/08/30  星期一 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
public abstract class AbstractMQConsumer {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource protected ObjectMapper mapper;

    private static final int RECONSUME_TIMES = 16;
    private static final int CONSUMER_THREAD_NUMS = 10;

    protected MessageDef messageDef;
    protected Consumer consumer;
    protected MQProperties mqProperties;




    public AbstractMQConsumer(MessageDef messageDef) {  this.messageDef = messageDef; }
    public void setMqProperties(MQProperties mqProperties) {  this.mqProperties = mqProperties; }



    private void init() {
        Properties properties = new Properties();

        String groupId = StringUtils.join(mqProperties.getGroupIdPrefix(), ".", simplyClassName());
        properties.put(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getServerUrl());
        properties.put(PropertyKeyConst.AccessKey, mqProperties.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, mqProperties.getSecretKey());
        properties.put(PropertyKeyConst.GROUP_ID, groupId);
        properties.put(PropertyKeyConst.MessageModel, PropertyValueConst.CLUSTERING);
        properties.put(PropertyKeyConst.MaxReconsumeTimes, RECONSUME_TIMES);
        properties.put(PropertyKeyConst.ConsumeThreadNums, CONSUMER_THREAD_NUMS);

        String topic = evnTopic();
        String tag = messageDef.getTag();

        consumer = ONSFactory.createConsumer(properties);
        consumer.subscribe(topic, messageDef.getTag(), (message, context) -> {
            MDC.put(Constants.TRACE_MDC_KEY, UUIDCache.UUID());
            Boolean action = handleAction(message, context);
            if (action) {
                return Action.CommitMessage;
            }
            if(RECONSUME_TIMES == message.getReconsumeTimes()){
                LOGGER.error("【FW-mq】number of message consumption failures exceeded the upper limit. topic[{}],tag[{}],message[{}]",topic,tag,new String( message.getBody(), StandardCharsets.UTF_8));
                return Action.CommitMessage;
            }
            return Action.ReconsumeLater;
        });
        consumer.start();
        LOGGER.info("【FW-mq】 init success groupId[{}] topic [{}],tag[{}]", groupId, topic, tag);


    }



    private  String evnTopic(){
        String topic = messageDef.getTopic();

        if(messageDef.getMultipleEnv()){
            String activeProfile = SpringContextUtil.getActiveProfile();
            if(EnvProfile.dev.name().equals(activeProfile)){
                return StringUtils.join(topic,"_",EnvProfile.dev.name());
            }else if(EnvProfile.test.name().equals(activeProfile)){
                return StringUtils.join(topic,"_",EnvProfile.test.name());
            }else if(EnvProfile.pre.name().equals(activeProfile)){
                return StringUtils.join(topic,"_",EnvProfile.pre.name());
            }else if(EnvProfile.pro.name().equals(activeProfile)){
                return StringUtils.join(topic,"_",EnvProfile.pro.name());
            }
            throw new RuntimeException("no available run environment is specified");
        }else {
            return topic;
        }
    }



    String simplyClassName(){
        String simpleName = this.getClass().getSimpleName();
        int index = simpleName.lastIndexOf("MqConsumer");
        if(index > 0){
            return simpleName.substring(0,index);
        }
        index = simpleName.lastIndexOf("MQConsumer");
        if(index > 0){
            return simpleName.substring(0,index);
        }
        index = simpleName.lastIndexOf("Consumer");
        if(index > 0){
            return simpleName.substring(0,index);
        }
        return simpleName;

    }





    /**
     * 具体业务处理抽象接口
     * @param message  message
     * @param context  context
     * @return Boolean
     * @author  Gopal.pan
     */
    public abstract Boolean handleAction(Message message, ConsumeContext context);


}
