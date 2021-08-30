package com.github.sylphlike.framework.mq.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>  time 15:13 2021/08/30  星期一 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

@Data
@Component
@ConfigurationProperties(prefix = "sylphlike.framework.mq")
public class MQProperties {


    /** 设置 TCP 接入域名 */
    private String serverUrl;

    /** AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建 */
    private String accessKey;

    /** SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建 */
    private String secretKey;

    /** 应用分组前缀 */
    private String groupIdPrefix;

    /** 消费端实例包路径 XX.XXX.XXX */
    private String reflectionPackage;
}
