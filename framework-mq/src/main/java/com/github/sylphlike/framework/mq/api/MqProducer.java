package com.github.sylphlike.framework.mq.api;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.github.sylphlike.framework.mq.core.MessageDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * <p>  time 15:48 2021/08/30  星期一 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
@DependsOn("producerBeanInit")
@Component
public class MqProducer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Resource private ProducerBean producerBean;

    /**
     * 发送普通消息 不带业务主键
     * @param topic     topic
     * @param tags      tags
     * @param message   业务数据
     * @return  SendResult
     * @author  Gopal.pan
     */
    public SendResult sendMessage(String topic, String tags, String message) {
        logger.info("【FW-mq】send message content topic[{}] tags[{}] message[{}] ",topic,tags,message);
        Message sendMessage = new Message(topic, tags, message.getBytes(StandardCharsets.UTF_8));
        SendResult send = producerBean.send(sendMessage);
        logger.info("【FW-mq】send results [{}]  ",send);
        return send;
    }






    /**
     * 发送普通消息 不带业务主键
     * @param messageDef  MessageDefinition
     * @param message            业务数据
     * @return  SendResult
     * @author  Gopal.pan
     */
    public SendResult  sendMessage(MessageDef messageDef, String message) {
        logger.info("【FW-mq】send message content messageDef[{}] message[{}]",messageDef,message);
        Message sendMessage = new Message(messageDef.getTopic(), messageDef.getTag(), message.getBytes(StandardCharsets.UTF_8));
        SendResult send = producerBean.send(sendMessage);
        logger.info("【FW-mq】send results [{}]  ",send);
        return send;

    }




    /**
     * 发送定时消息，不带业务主键
     * 设置消息的定时投递时间（绝对时间),最大延迟时间为7天.
     * @param topic         topic
     * @param tags          tags
     * @param message       业务数据
     * @param deliverTime   发送时间
     * @return  SendResult
     * @author  Gopal.pan
     */
    public SendResult  sendMessage(String topic,String tags,String message,long deliverTime) {
        logger.info("【FW-mq】send message content topic[{}] tags[{}] message[{}] deliverTime[{}]",topic,tags,message,deliverTime);
        Message sendMessage = new Message(topic, tags, message.getBytes(StandardCharsets.UTF_8));
        sendMessage.setStartDeliverTime(deliverTime);
        SendResult send = producerBean.send(sendMessage);
        logger.info("【FW-mq】send results [{}]  ",send);
        return send;

    }



    /**
     * 发送普通消息 带业务主键
     * @param messageDef MessageDefinition
     * @param key               业务主键
     * @param message           业务数据
     * @return  SendResult
     * @author  Gopal.pan
     */
    public SendResult  sendMessage(MessageDef messageDef,String key,String message) {
        logger.info("【FW-mq】send message content messageDef[{}] key[{}] message[{}] ",messageDef,key,message);
        Message sendMessage = new Message(messageDef.getTopic(), messageDef.getTag(),key, message.getBytes(StandardCharsets.UTF_8));
        SendResult send = producerBean.send(sendMessage);
        logger.info("【FW-mq】send results [{}]  ",send);
        return send;

    }



    /**
     * 发送定时消息，带业务主键
     * 设置消息的定时投递时间（绝对时间),最大延迟时间为7天.
     * @param topic        topic
     * @param tags         tags
     * @param key          业务主键
     * @param message      业务数据
     * @param deliverTime  发送时间
     * @return  SendResult
     * @author  Gopal.pan
     */
    public SendResult  sendMessage(String topic,String tags,String key,String message,long deliverTime) {
        logger.info("【FW-mq】send message content topic[{}] tags[{}] key[{}] message[{}] deliverTime[{}]",topic,tags,key,message,deliverTime);
        Message sendMessage = new Message(topic, tags, message.getBytes(StandardCharsets.UTF_8));
        sendMessage.setStartDeliverTime(deliverTime);
        SendResult send = producerBean.send(sendMessage);
        logger.info("【FW-mq】send results [{}]",send);
        return send;

    }
    
}
