package com.github.sylphlike.framework.adapt;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>  time 10:46 2021/08/20  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

@AllArgsConstructor
@Getter
public enum EnvProfile {
    dev  ("开发环境，外部用户无法访问，开发人员使用，版本变动很大"),
    test ("测试环境，外部用户无法访问，专门给测试人员使用的，版本相对稳定"),
    pre  ("灰度环境，外部用户可以访问，但是服务器配置相对低，其它和生产一样"),
    pro  ("生产环境，面向外部用户的环境，连接上互联网即可访问的正式环境");

    private final String desc;

}
