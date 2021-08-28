package com.github.sylphlike.framework.redis.core;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>  time 17:56 2021/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

@Component
public class SerialNumber {
    private static final Logger LOGGER = LoggerFactory.getLogger(SerialNumber.class);

    private final RedissonClient redissonClient;
    public SerialNumber(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 初始化指定规则的流水号
     * @param prefix         流水号规则前缀
     * @param serialLength   自增流水号长度
     * @param key            流水号在Redis中的key值
     * @param currentSerial  当前流水号已使用的最大值
     * @author   Gopal.pan
     */
    public void initSerial(String prefix,int serialLength,String key,String currentSerial){
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("0".repeat(Math.max(0, serialLength)));

        NumberFormat serialFormat = new DecimalFormat(prefix + stringBuffer.toString());
        long currentSequence;
        if(StringUtils.isEmpty(currentSerial)){
            currentSequence = Long.parseLong(stringBuffer.toString());
        }else {
            currentSequence = Long.parseLong(currentSerial.replace(prefix, ""));
        }
        cacheSerial(key,serialFormat,currentSequence);

        SerialEntity serialEntity = new SerialEntity();
        serialEntity.setPrefix(prefix);
        serialEntity.setSerialLength(serialLength);
        serialEntity.setKey(key);
        serialEntity.setCurrentSerial(serialFormat.format(currentSequence + Constants.CAPACITY));

        RBucket<Object> bucket = redissonClient.getBucket(key + Constants.MATE_SUFFIX,new RedisJsonCodec());
        bucket.set(serialEntity);
    }





    /**
     * 初始化指定规则的流水号
     * @param serialEntity  原始流水号实体对象
     * @author   Gopal.pan
     */
    public void initSerial(SerialEntity serialEntity){
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("0".repeat(Math.max(0, serialEntity.getSerialLength())));

        NumberFormat serialFormat = new DecimalFormat(serialEntity.getPrefix() + stringBuffer.toString());
        long currentSequence;
        if(StringUtils.isEmpty(serialEntity.getCurrentSerial())){
            currentSequence = Long.parseLong(stringBuffer.toString());
        }else {
            currentSequence = Long.parseLong(serialEntity.getCurrentSerial().replace(serialEntity.getPrefix(), ""));
        }
        cacheSerial(serialEntity.getKey(),serialFormat,currentSequence);
        serialEntity.setCurrentSerial(serialFormat.format(currentSequence + Constants.CAPACITY));
        RBucket<Object> bucket = redissonClient.getBucket(StringUtils.join(serialEntity.getKey() , Constants.MATE_SUFFIX),new RedisJsonCodec());
        bucket.set(serialEntity);
    }


    /**
     * 获取序列号
     * @param key   缓存key
     * @return   java.lang.String
     * @author   Gopal.pan
     */
    public String firstSqe(String key){
        RList<String> cacheList = redissonClient.getList(key, new StringCodec());
        String firstSqe = cacheList.remove(0);
        if (StringUtils.isEmpty(firstSqe)){
            if (!reloadSeq(key)){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            cacheList = redissonClient.getList(key, new StringCodec());
            firstSqe = cacheList.get(0);
        }
        return firstSqe;
    }


    private  void cacheSerial(String key, NumberFormat serialFormat, Long currentSequence) {
        List<String> list = new ArrayList<>();
        for (int x = 1; x <= Constants.CAPACITY; x ++){
            list.add(serialFormat.format(currentSequence + x));
        }
        RList<Object> cacheList = redissonClient.getList(key, new StringCodec());

        cacheList.addAllAsync(list);

    }




    /**
     * 重新装载序列
     * @param key 序列key
     * @return void
     * @author  Gopal.pan
     */
    public boolean reloadSeq(String key){
        LOGGER.info("【FW-redis】全局流水号,同步填充流水号,队列名称[{}]",key);
        RLock lock = null;
        boolean acquireLock = false;
        try {
            String maxKey = StringUtils.join(key,Constants.MATE_SUFFIX);
            lock = redissonClient.getLock(StringUtils.join(maxKey, "_LOCK"));
            if(lock.tryLock(2,3, TimeUnit.SECONDS)){
                RBucket<SerialEntity> bucket = redissonClient.getBucket(StringUtils.join(key , Constants.MATE_SUFFIX),new RedisJsonCodec());
                initSerial(bucket.get());
                acquireLock = true;
            }
        }catch (Exception e){
            LOGGER.info("【FW-redis】全局流水号,同步填充流水号,系统异常",e);
        }finally {
            if(lock != null){
                lock.unlock();
            }
        }
        return acquireLock;
    }
}
