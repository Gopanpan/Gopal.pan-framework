package com.github.sylphlike.framework.utils.sequence;


import com.github.sylphlike.framework.utils.general.Clock;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 标准雪花算法
 * <p>  0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 *      1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0
 *      41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)得到的值，这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的
 *      10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId
 *      12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号
 * </p>
 * <p>  time 17:56 2018/06/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class StandardSequence {

    /** 时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）*/
    private final static long TWEPOCH = 1585038908558L;

    /** 数据中心标识位数 */
    private final static long DATA_CENTER_ID_BITS = 5L;
    /** 机器标识位数 */
    private final static long WORKER_ID_BITS = 5L;
    /** 毫秒内自增位 */
    private final static long SEQUENCE_BITS = 12L;

    /** 数据中心ID最大值 */
    private final static long MAX_DATA_CENTER_ID = 31;
    /** 机器ID最大值 */
    private final static long MAX_WORKER_ID = 31;



    /** 机器ID左移12位 */
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
    /** 数据中心ID左移17位 */
    private final static long DATA_CENTER_ID_SHIFT =  WORKER_ID_SHIFT + WORKER_ID_BITS;
    /** 时间毫秒左移22位 */
    private final static long TIMESTAMP_LEFT_SHIFT =  DATA_CENTER_ID_SHIFT  + DATA_CENTER_ID_BITS;


    private final static long SEQUENCE_MASK = 4095;

    /** 上次生产id时间戳 */
    private static long lastTimestamp = -1L;

    /** 并发控制 */
    private long sequence = 0L;


    private final long dataCenterId;
    private final long workerId;


    public StandardSequence() {

        this.dataCenterId = MarkProvider.getDataCenterId(MAX_DATA_CENTER_ID);
        this.workerId = MarkProvider.getWorkerId(dataCenterId, MAX_WORKER_ID);
    }


    public StandardSequence(long workerId, long dataCenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenter Id can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }


    /**
     * 获取下一个ID
     * @return  long
     * @author  Gopal.pan
     */
    public  synchronized long nextId() {
        long timestamp = Clock.now();

        timeMillisGen(timestamp);

        // 41位时间截(毫秒级) << 22 | 5位的数据机器位 << 17 | 5位workerId << 12 |12位序列
        return  ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;

    }





    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = Clock.now();
        while (timestamp <= lastTimestamp) {
            timestamp = Clock.now();
        }
        return timestamp;
    }


    private void timeMillisGen(long timestamp) {
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            // 当前毫秒内，则+1
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                // 当前毫秒内计数满了，则等待下一秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = ThreadLocalRandom.current().nextLong(0, 32);
        }
        lastTimestamp = timestamp;

    }


}

