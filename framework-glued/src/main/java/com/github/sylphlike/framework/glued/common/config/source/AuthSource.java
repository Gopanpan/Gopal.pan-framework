package com.github.sylphlike.framework.glued.common.config.source;


import com.github.sylphlike.framework.glued.api.GluedClient;
import com.github.sylphlike.framework.glued.domain.input.ClientConfig;

/**
 * 三方应用地址管理
 * <p>  time 17:56 2017/03/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
public interface AuthSource {


    /**
     * 实例化对应平台的运行实例
     * @param clientConfig ClientConfig
     * @return  com.github.sylphlike.framework.glued.api.GluedClient
     * @author  Gopal.pan
     */
    GluedClient instants(ClientConfig clientConfig);




    /**
     * 授权api地址
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    String authorize();


    /**
     * 获取accessToken的api
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    String accessToken();



    /**
     * 获取用户信息
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    String userInfo();


    /**
     * 取消授权
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    default String revoke() throws Exception {throw new Exception(""); }




    /**
     *  刷新授权
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    default String refresh() throws Exception {throw new Exception(""); }




    default String getName() {
        if (this instanceof Enum) {
            return String.valueOf(this);
        }
        return this.getClass().getSimpleName();
    }
}
