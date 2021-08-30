package com.github.sylphlike.framework.mq.core;

/**
 * <p>  time 11:41 2021/08/28  星期六 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
public interface MessageDef {



    /**
     * 设定当前实例消费的topic
     * 相同topic 可以存在多个，但对应的tag 需要不同，即当前应用可以消费一个topic下的多个tag，互不干扰
     * @return String
     * @author  Gopal.pan
     */
    String  getTopic();

    /**
     * 设定当前实例消费的tag,
     * @return String
     * @author  Gopal.pan
     */
    String  getTag();

    /**
     * topic是否根据当前运行环境变化
     * true  TOPIC_TEST_${env}
     * false TOPIC_TEST
     * {@link com.github.sylphlike.framework.adapt.EnvProfile}
     * @return Boolean
     */
    Boolean getMultipleEnv();

    String  getDesc();
}
