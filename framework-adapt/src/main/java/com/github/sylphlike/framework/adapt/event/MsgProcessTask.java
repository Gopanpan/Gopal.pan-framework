package com.github.sylphlike.framework.adapt.event;

import com.github.sylphlike.framework.adapt.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * 延迟消息处理类
 * <p>具体业务处理类交由Spring管理，并且bean实例名称应与消息类型一致。即实msgType（）方法返回的值为该类的简单类名，首字母小写 </p>
 * <p>  time 17:56 2021/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </p>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class MsgProcessTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MsgProcessTask.class);

    static  private final Map<String, MsgConsumer>  msgConsumerMap = new ConcurrentHashMap<>();
    
    static {
        ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
        Map<String, MsgConsumer> beansOfType = applicationContext.getBeansOfType(MsgConsumer.class);
        beansOfType.forEach((k,v) ->{
            LOGGER.info("【FW-web】队列任务,队列实现key[{}],实现类[{}]",k,v);
        });
        msgConsumerMap.putAll(beansOfType);

    }


    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (true){
            /* 消息刷新时间间隔(单位:毫秒) */
            int FLUSH_INTERVAL_MS = 100;
            try {
                MsgEvent<?> msgEvent = MsgEventManager.msgEventDelayQueue.poll(FLUSH_INTERVAL_MS, TimeUnit.MILLISECONDS);
                if(msgEvent != null){
                    LOGGER.info("【FW-web】队列任务,到期消费的消息为[{}]",msgEvent);
                    MsgConsumer msgConsumer = msgConsumerMap.get(msgEvent.getBusinessDealClass());
                    if(null != msgConsumer){
                        LOGGER.info("【FW-web】队列任务, 当前处理任务实现类为[{}]",msgConsumer);
                        msgConsumer.doMsg(msgEvent);
                    }else {
                        LOGGER.info("【FW-web】到期消息为找到对应的业务处理类,请检查业务端实现");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    TimeUnit.MILLISECONDS.sleep(FLUSH_INTERVAL_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
