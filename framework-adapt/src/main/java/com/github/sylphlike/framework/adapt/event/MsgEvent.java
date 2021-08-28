package com.github.sylphlike.framework.adapt.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * <p>  time 17:56 2010/10/29  星期五 </p>
 * <p> email 15923508369@163.com     </p>
 * @author Gopal.pan
 * @version 1.0.0
 */

@Getter
@Setter
@ToString
public class MsgEvent<T> implements Serializable, Delayed {


    private static final long serialVersionUID = -3587950611824949606L;

    /** 消息ID */
    private Long msgId;

    /** 业务ID */
    private String businessId;

    /** 业务处理实现类简单类名 */
    private String businessDealClass;


    /**消息权重 */
    private Integer msgOrder;

    /**  消息事件预计发生时间*/
    private LocalDateTime msgEventTime;

    /** 消息内容 */
    private T data;


    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(msgEventTime.toInstant(ZoneOffset.of("+8")).toEpochMilli()- System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        MsgEvent<?> object = (MsgEvent<?>) o;
        if (this.msgEventTime.compareTo(object.msgEventTime) > 0) {
            return 1;
        } else if (this.msgEventTime.compareTo(object.msgEventTime) < 0) {
            return -1;
        } else {
            if (this.msgOrder.compareTo(object.msgOrder) > 0) {
                return -1;
            } else if (this.msgOrder.compareTo(object.msgOrder) < 0) {
                return 1;
            } else {
                return Integer.compare(this.msgId.compareTo(object.msgId), 0);
            }
        }
    }

}
