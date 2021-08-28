package com.github.sylphlike.framework.utils.sequence;

import com.github.sylphlike.framework.utils.general.Clock;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>  time 17:56 2018/06/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
public class GeneSequence {

    /** 时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）*/
    private final static long TWEPOCH = 1585038908558L;

    /** 数据中心标识位数 */
    private final static long DATA_CENTER_ID_BITS = 4L;
    /** 机器标识位数 */
    private final static long WORKER_ID_BITS = 4L;
    /** 毫秒内自增位 */
    private final static long SEQUENCE_BITS = 10L;
    /** 基因自增位 */
    private final static long GENE_BITS = 4L;


    /** 机器ID最大值 */
    private final static long MAX_WORKER_ID = 14;
    /** 数据中心ID最大值 */
    private final static long MAX_DATA_CENTER_ID = 14;



    /** 序列左移4位 */
    private final static long SEQUENCE_ID_SHIFT = GENE_BITS;
    /** 机器ID偏左移12位 */
    private final static long WORKER_ID_SHIFT = SEQUENCE_ID_SHIFT + SEQUENCE_BITS;
    /** 数据中心ID左移17位 */
    private final static long DATA_CENTER_ID_SHIFT = WORKER_ID_SHIFT + WORKER_ID_BITS;
    /** 时间毫秒左移22位 */
    private final static long TIMESTAMP_LEFT_SHIFT = DATA_CENTER_ID_SHIFT +DATA_CENTER_ID_BITS;


    private final static long SEQUENCE_MASK = 1023;

    /** 上次生产id时间戳 */
    private static long lastTimestamp = -1L;

    /** 并发控制 */
    private long sequence = 0L;

    /** 分库分表大小 */
    private final static long GENE_SIZE = 16;



    private final long dataCenterId;
    private final long workerId;


    public GeneSequence() {
        this.dataCenterId = MarkProvider.getDataCenterId(MAX_DATA_CENTER_ID);
        this.workerId = MarkProvider.getWorkerId(dataCenterId, MAX_WORKER_ID);
    }


    public GeneSequence(long workerId, long dataCenterId) {
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
     * @param orgId 原始ID
     * @return  long
     * @author  Gopal.pan
     */
    public  synchronized long nextId(Long orgId) {
        long timestamp = Clock.now();
        timeMillisGen(timestamp);
        // 41位时间截(毫秒级) << 22 | 4位的数据机器位 << 16 | 4位workerId << 14 | 8位序列 << 4 | 4为基因序列
        return  ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence << SEQUENCE_ID_SHIFT
                | orgId % GENE_SIZE;

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
            sequence = ThreadLocalRandom.current().nextLong(0, GENE_SIZE);
        }
        lastTimestamp = timestamp;

    }



}
