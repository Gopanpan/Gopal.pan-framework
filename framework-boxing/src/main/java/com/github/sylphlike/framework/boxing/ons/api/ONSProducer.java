package com.github.sylphlike.framework.boxing.ons.api;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.github.sylphlike.framework.boxing.GoSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * <p>  time 17:56 2019/03/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

@Component
public class ONSProducer {

    private final Logger logger = LoggerFactory.getLogger(ONSProducer.class);

    @Autowired(required = false) private ProducerBean producer;


    /**
     * 发送普通消息 不带业务主键
     * <p>  time 18:24 2021/1/29      </p>
     * <p> email 15923508369@163.com  </p>
     * @param topic     topic
     * @param tags      tags
     * @param message   业务数据
     * @return  com.aliyun.openservices.ons.api.SendResult
     * @author  Gopal.pan
     */
    public SendResult  sendMessage(String topic,String tags,Object message) {
        logger.info("【FW-boxing】 ONS发送消息,topic[{}] tags[{}] message[{}] ",topic,tags,message);
        Message sendMessage = new Message(topic, tags, GoSerializer.serialize(message));
        SendResult send = producer.send(sendMessage);
        logger.info("【FW-boxing】 ONS发送结果,SendResult[{}]  ",send);
        return send;
    }






    /**
     * 发送普通消息 不带业务主键
     * <p>  time 15:09 2021/1/5 (HH:mm yyyy/MM/dd)
     * <p> email 15923508369@163.com
     * @param messageDefinition  MessageDefinition
     * @param message            业务数据
     * @return  com.aliyun.openservices.ons.api.SendResult
     * @author  Gopal.pan
     */
    public SendResult  sendMessage(MessageDefinition messageDefinition,Object message) {
        logger.info("【FW-boxing】 ONS发送消息,messageDefinition[{}] message[{}]",messageDefinition,message);
        Message sendMessage = new Message(messageDefinition.getTopic(), messageDefinition.getTag(),  GoSerializer.serialize(message));
        SendResult send = producer.send(sendMessage);
        logger.info("【FW-boxing】 ONS发送结果,SendResult[{}]  ",send);
        return send;

    }




    /**
     * 发送定时消息，不带业务主键
     * <p> 设置消息的定时投递时间（绝对时间),最大延迟时间为7天.
     * <p>  time 15:09 2021/1/5 (HH:mm yyyy/MM/dd)
     * <p> email 15923508369@163.com
     * @param topic         topic
     * @param tags          tags
     * @param message       业务数据
     * @param deliverTime   发送时间
     * @return  com.aliyun.openservices.ons.api.SendResult
     * @author  Gopal.pan
     */
    public SendResult  sendMessage(String topic,String tags,Object message,long deliverTime) {
        logger.info("【FW-boxing】 ONS发送消息,topic[{}] tags[{}] message[{}] deliverTime[{}]",topic,tags,message,deliverTime);
        Message sendMessage = new Message(topic, tags, GoSerializer.serialize(message));
        sendMessage.setStartDeliverTime(deliverTime);
        SendResult send = producer.send(sendMessage);
        logger.info("【FW-boxing】 ONS发送结果,SendResult[{}]  ",send);
        return send;

    }



    /**
     * 发送普通消息 带业务主键
     * <p>  time 15:10 2021/1/5 (HH:mm yyyy/MM/dd)
     * <p> email 15923508369@163.com
     * @param messageDefinition MessageDefinition
     * @param key               业务主键
     * @param message           业务数据
     * @return  com.aliyun.openservices.ons.api.SendResult
     * @author  Gopal.pan
     */
    public SendResult  sendMessage(MessageDefinition messageDefinition,String key,String message) {
        logger.info("【FW-boxing】ONS发送消息,messageDefinition[{}] key[{}] message[{}] ",messageDefinition,key,message);
        Message sendMessage = new Message(messageDefinition.getTopic(), messageDefinition.getTag(),key, message.getBytes(StandardCharsets.UTF_8));
        SendResult send = producer.send(sendMessage);
        logger.info("【FW-boxing】ONS发送结果,SendResult[{}]  ",send);
        return send;

    }



    /**
     * 发送定时消息，带业务主键
     * <p> 设置消息的定时投递时间（绝对时间),最大延迟时间为7天.
     * <p>  time 15:11 2021/1/5 (HH:mm yyyy/MM/dd)
     * <p> email 15923508369@163.com
     * @param topic        topic
     * @param tags         tags
     * @param key          业务主键
     * @param message      业务数据
     * @param deliverTime  发送时间
     * @return  com.aliyun.openservices.ons.api.SendResult
     * @author  Gopal.pan
     */
    public SendResult  sendMessage(String topic,String tags,String key,String message,long deliverTime) {
        logger.info("【FW-boxing】ONS发送消息,topic[{}] tags[{}] key[{}] message[{}] deliverTime[{}]",topic,tags,key,message,deliverTime);
        Message sendMessage = new Message(topic, tags, message.getBytes(StandardCharsets.UTF_8));
        sendMessage.setStartDeliverTime(deliverTime);
        SendResult send = producer.send(sendMessage);
        logger.info("【FW-boxing】ONS发送结果,SendResult[{}]  ",send);
        return send;

    }



}
