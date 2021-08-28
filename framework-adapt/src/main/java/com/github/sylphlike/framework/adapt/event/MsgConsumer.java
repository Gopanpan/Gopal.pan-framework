package com.github.sylphlike.framework.adapt.event;



/**
 * <p>  time 17:56 2019/10/29  星期五 </p>
 * <p> email 15923508369@163.com     </p>
 * @author Gopal.pan
 * @version 1.0.0
 */
public interface  MsgConsumer {

     /**
      * 具体业务处理
      * @param msgEvent  业务参数
      */
     void doMsg(MsgEvent<?> msgEvent);


     /**
      * 消息类型
      * @return
      */
     String msgType();





}
