package com.github.sylphlike.framework.adapt.cache;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * 本地JVM 内存缓存简单实现 不保证缓存数据实时有效性，受限于该工具类刷新缓存的时间间隔，实际数据已过期，但在本工具类中还存在
 * <p>  time 17:56 2019/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class LocalCache {
    // 默认的缓存容量
    private static final int DEFAULT_CAPACITY = 512;
    // 最大容量
    private static final int MAX_CAPACITY = 50000;
    // 刷新缓存的频率
    private static final int MONITOR_DURATION = 10;

    // 启动监控线程
    static {
        Thread thread = new Thread(new TimeoutTimerThread());
        thread.setName("micro-api-auth-client-check-cache");
        thread.start();
    }

    // 缓存容器
    private static final ConcurrentHashMap<String, CacheEntity> cache = new ConcurrentHashMap<>(DEFAULT_CAPACITY);


    /**
     * 将key-value 保存到本地缓存并制定该缓存的过期时间
     * @param key         key
     * @param value       值
     * @param expireTime  过期时间，如果是-1 则表示永不过期  单位毫秒
     * @return  boolean
     * @author  Gopal.pan
     */
    public static boolean putValue(String key, Object value, int expireTime) {
        return putCloneValue(key, value, expireTime);
    }



    /**
     * 将值通过序列化clone 处理后保存到缓存中，可以解决值引用的问题
     * @param key         key
     * @param value       值
     * @param expireTime  过期时间，如果是-1 则表示永不过期  单位毫秒
     * @return  boolean
     * @author  Gopal.pan
     */
    private static boolean putCloneValue(String key, Object value, int expireTime) {
        try {
            if (cache.size() >= MAX_CAPACITY) {
                return false;
            }
            // 序列化赋值
            CacheEntity entityClone = clone(new CacheEntity(value, System.nanoTime(), expireTime));
            cache.put(key, entityClone);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }




    /**
     * 从本地缓存中获取key对应的值，如果该值不存则则返回null
     * @param key  key
     * @return  java.lang.Object
     * @author  Gopal.pan
     */
    public static Object getValue(String key) {
        CacheEntity cacheEntity = cache.get(key);
        if(null != cacheEntity){
            return cacheEntity.getValue();
        }
        return null;

    }

    public static boolean removeValue(String key){
        CacheEntity remove = cache.remove(key);
        if(null !=remove){
            return  true;
        }
        return false;

    }


    private static <T extends Serializable> T clone(T object) {
        T cloneObject = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(  baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            cloneObject = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloneObject;
    }

    /**
     * 缓存过期处理
     * @author  Gopal.pan
     */
    static class TimeoutTimerThread implements Runnable {
        public void run() {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(MONITOR_DURATION);
                    for (String key : cache.keySet()) {
                        CacheEntity tce = cache.get(key);
                        long timoutTime = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - tce.getGmtModify());

                        if (tce.getExpire() == -1 || tce.getExpire() > timoutTime) {
                            continue;
                        }
                        cache.remove(key);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }








}
