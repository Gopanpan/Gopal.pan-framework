package com.github.sylphlike.framework.file.qiniu;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>  time 17:56 2019/03/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

@Data
@Component
@ConfigurationProperties(prefix = "sylphlike.framework.qiniu")
public class QiniuConfig {

    /**是否启用 */
    private boolean enable;

    private String accessKey;
    private String secretKey;

    /** 空间名称*/
    private String bucket;

    /** 域名 */
    private String domain;
    /**本地临时根目录 */
    private String localTemp;

}
