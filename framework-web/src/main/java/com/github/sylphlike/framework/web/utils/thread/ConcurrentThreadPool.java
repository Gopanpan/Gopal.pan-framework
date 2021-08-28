package com.github.sylphlike.framework.web.utils.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


/**
 *  多线程任务提交
 * <p> 调用方式：
 *          主线程调用
 *             List<CallableTemplate<Boolean>> tasks = new ArrayList<>();
 *             for (int x = 0; x < 21; x ++){
 *                  DownRqCode downRqCode = new DownRqCode(redisClient);
 *                  tasks.add(downRqCode);
 *             }
 *             List<Boolean> downloadResult = concurrentThreadPool.invokeAll(tasks);
 *             //解析处理线程返回结果
 *
 *          业务代码块
 *             static class DownRqCode extends CallableTemplate<Boolean>{
 *                  private RedisClient redisClient;
 *
 *                  public DownRqCode(RedisClient redisClient){
 *                      this.redisClient = redisClient;
 *
 *                  }
 *
 *              @Override
 *              public Boolean process() {
 *                  //业务逻辑
 *                  return true;
 *              }
 *          }
 * </p>
 * <p>  time 17:56 2021/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </p>
 * @author Gopal.pan
 * @version 1.0.0
 */

@Component
public class ConcurrentThreadPool {

    private final Logger LOGGER = LoggerFactory.getLogger(ConcurrentThreadPool.class);



    @Autowired private ThreadPoolProperties threadPoolProperties;
    @Autowired private ThreadPoolExecutor  threadPoolExecutor;

    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolProperties threadPoolConfig(){ return new ThreadPoolProperties(); }


    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(threadPoolProperties.getCorePoolSize(),  threadPoolProperties.getMaximumPoolSize(), threadPoolProperties.getKeepAliveTime(),
                TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
    }



    /**
     * 提交单个任务
     * @param task  可执行类
     * @return  V
     * @throws  ExecutionException  ex
     * @throws  InterruptedException ex
     * @author  Gopal.pan
     */
    public   <V> V submit(CallableTemplate<V> task) throws ExecutionException, InterruptedException {
        LOGGER.info("【FW-web】 提交多线程任务 [{}]",task);
        Future<V> result = threadPoolExecutor.submit(task);
        return result.get();
    }



    /**
     * 批量提交多个任务
     * @param tasks   可执行类
     * @return  java.util.List<V>
     * @throws  ExecutionException  ex
     * @throws  InterruptedException ex
     * @author  Gopal.pan
     */
    public <V> List<V> invokeAll(List<? extends CallableTemplate<V>> tasks)  throws InterruptedException, ExecutionException {

        LOGGER.info("【FW-web】 批量多线程任务 提交任务数量为[{}]",tasks.size());
        List<Future<V>> tasksResult = threadPoolExecutor.invokeAll(tasks);
        LOGGER.info("【FW-web】 批量多线程任务 批量任务执行完成");
        List<V> resultList = new ArrayList<V>();
        for (Future<V> future : tasksResult) {
            resultList.add(future.get());
        }
        return resultList;
    }

}
