package com.github.sylphlike.framework.web.utils.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * <p>  time 17:56 2010/10/29  星期五 </p>
 * <p> email 15923508369@163.com     </p>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class MsgEventManager  {

    private static final Logger logger = LoggerFactory.getLogger(MsgEventManager.class);;

    public static DelayQueue<MsgEvent<?>> msgEventDelayQueue  = new DelayQueue<>();

    /** 队列容积 */
    private static final int QUEUE_CAPACITY = 10000;

    /** 执行线程数。核心线程数。最大线程数。消费线程数 */
    private static final int PROCESS_THREAD_SIZE = 2;

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(PROCESS_THREAD_SIZE,PROCESS_THREAD_SIZE,50, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(500),new NamedThreadFactory("msg_event_poll_"));



    private MsgEventManager() {}
    static {
        for (int x = 0; x < PROCESS_THREAD_SIZE; x ++){
            executor.execute(new MsgProcessTask());
        }
    }


    /**
     * 添加队列数据
     * @param msgEvent msgEvent
     * @return boolean
     */
    public static boolean offerMsg(MsgEvent<?> msgEvent){

        int size = msgEventDelayQueue.size();
        if((double)size / QUEUE_CAPACITY > 0.9){
            logger.warn("【FW-web】当前队列容量超过90%，请关注队列数据");
        }
        if(!msgEventDelayQueue.contains(msgEvent)){
            msgEventDelayQueue.offer(msgEvent);
            return true;
        }

        return false;
    }



}
