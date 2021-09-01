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
    DEV ("dev","Development environment。 开发环境，用于开发者调试使用"),
    FAT ("fat","Feature Acceptance Test environment。功能验收测试环境，用于软件测试者测试使用"),
    UAT ("uat","User Acceptance Test environment。 用户验收测试环境，用于生产环境下的软件测试者测试使用"),
    PRO ("pro","Production environment。生产环境");

    private final String code;
    private final String desc;

}
