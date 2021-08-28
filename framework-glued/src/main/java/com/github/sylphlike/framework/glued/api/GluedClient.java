package com.github.sylphlike.framework.glued.api;


import com.alipay.api.AlipayApiException;
import com.github.sylphlike.framework.glued.common.exception.GluedException;
import com.github.sylphlike.framework.glued.domain.out.AuthToken;
import com.github.sylphlike.framework.glued.domain.out.UserInfo;

import java.io.IOException;

/**
 * 三方授权登录接口
 * <p> 调用示例，获取用户授权token接口
 *         AccessTokenClient accessTokenClient  = new AccessTokenClient();
 *         accessTokenClient.setPlatform("WEIBO");
 *         accessTokenClient.setAppKey("3731843804");
 *         accessTokenClient.setAppSecret("bed1cddb07673ebe3d28fe8b01127a98");
 *         accessTokenClient.setRedirectUri("http://84krhb.natappfree.cc/unite-demo/glued/weiboAccessToken");
 *         accessTokenClient.setCode(code);
 *         GluedClient instants = DefaultAuthSource.valueOf(accessTokenClient.getPlatform()).instants(accessTokenClient);
 *
 *         AuthToken accessToken = instants.accessToken();
 * </p>
 * <p>  time 17:56 2019/03/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
public interface GluedClient {


    /**
     * 请求用户授权Token
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    String authorize() throws GluedException;


    /**
     * 获取授权过的Access Token
     * @return   com.github.sylphlike.framework.glued.domain.AuthToken
     * @author   Gopal.pan
     */
    AuthToken accessToken() throws Exception;



    /**
     * 用户信息
     * @return  com.github.sylphlike.framework.glued.domain.out.UserInfo
     * @author  Gopal.pan
     */
    UserInfo userInfo() throws GluedException, IOException, AlipayApiException;



}
