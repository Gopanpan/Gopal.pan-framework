package com.github.sylphlike.framework.utils.general;


import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>  time 17:56 2018/12/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class HttpUtils {

    private static final Logger LOGGER =  LoggerFactory.getLogger(HttpUtils.class);

    private static final PoolingHttpClientConnectionManager connMgr;
    private static final RequestConfig requestConfig;

    static {
        connMgr = new PoolingHttpClientConnectionManager();
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectTimeout(2000);            //http请求的三个阶段，一：建立连接；二：数据传送；三，断开连接。超时后会ConnectionTimeOutException
        configBuilder.setSocketTimeout(5000);             //客户端和服务器建立连接后，客户端从服务器读取数据的timeout，超出后会抛出SocketTimeOutException
        configBuilder.setConnectionRequestTimeout(1000);  //从连接池获取连接的timeout

        requestConfig = configBuilder.build();
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据
     * <p>  time 15:06 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param url  api地址
     * @return  java.lang.String
     * @throws  IOException ex
     * @author  Gopal.pan
     */
    public static String doGet(String url) throws IOException {
        return doGet(url, new HashMap<>(16));
    }



    /**
     * 发送 GET 请求（HTTP），K-V形式
     * <p>  time 15:06 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param url     api地址
     * @param params  参数
     * @return  java.lang.String
     * @throws  IOException ex
     * @author  Gopal.pan
     */
    public static String doGet(String url, Map<String, Object> params) throws IOException {

        try(CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String buildUrl = buildUrl(url, params);
            if(StringUtils.isEmpty(buildUrl)){
                LOGGER.info("【FW-utils】GET请求地址为空,不发送请求");
                return "";

            }
            LOGGER.info("【FW-utils】 GET请求信息为：[{}]",buildUrl);
            HttpGet httpGet = new HttpGet(buildUrl);
            httpGet.setConfig(requestConfig);
            String response = httpclient.execute(httpGet, responseHandler());
            LOGGER.info("【FW-utils】 GET请求响应结果为：[{}]",response);
            httpGet.abort();
            return response;
        }

    }

    private static ResponseHandler<String> responseHandler() {
        return response -> {
            int code = response.getStatusLine().getStatusCode();
            if(code >= HttpStatus.SC_OK && code < HttpStatus.SC_MULTIPLE_CHOICES){
                HttpEntity entity = response.getEntity();
                return entity !=null? EntityUtils.toString(entity, Consts.UTF_8):null;
            }else{ throw new ClientProtocolException("Unexpected response status: " + code);}
        };
    }

    private static String buildUrl(String url, Map<String, Object> params) {
        if(StringUtils.isEmpty(url)) {return null;}
        StringBuilder urlBuilder = new StringBuilder(url).append("?");
        if(null !=params && params.size() >= 1){
            params.forEach((key, value) -> urlBuilder.append(key)
                    .append("=")
                    .append(value)
                    .append("&"));

        }
        urlBuilder.setLength(urlBuilder.length() -1);
        return urlBuilder.toString();
    }




    /**
     * 发送 POST 请求（HTTP），不带输入数据
     * <p>  time 15:06 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param url  api地址
     * @return  java.lang.String
     * @throws  IOException ex
     * @author  Gopal.pan
     */
    public static String doPost(String url) throws IOException {
        return doPost(url, new HashMap<>(16));
    }


    /**
     * 发送 POST 请求（HTTP），K-V形式
     * <p>  time 15:06 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param url    API接口URL
     * @param params 参数map
     * @return  java.lang.String
     * @throws  IOException ex
     * @author  Gopal.pan
     */
    public static String doPost(String url, Map<String, Object> params) throws IOException {

        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            return doMapPost(httpClient,url,params);
        }
    }


    private static HttpEntity buildFormParams(Map<String, Object> params) {
        List<NameValuePair> formParams = new ArrayList<>();
        if (params != null) {
            params.forEach((key, value) -> formParams.add(new BasicNameValuePair(key,  String.valueOf(value))));

        }
        return new UrlEncodedFormEntity(formParams, Consts.UTF_8);
    }



    /**
     * 发送 POST 请求（HTTP），JSON形式
     * <p>  time 15:06 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param url   api地址
     * @param json  json对象
     * @return  java.lang.String
     * @throws  IOException ex
     * @author  Gopal.pan
     */
    public static String doPost(String url, Object json) throws IOException {

        try(CloseableHttpClient httpClient = HttpClients.createDefault()){
            return doJsonPost(httpClient,url,json);
        }



    }


    /**
     * 发送 SSL POST 请求（HTTPS），K-V形式
     * <p>  time 15:06 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param url    API接口URL
     * @param params 参数map
     * @return  java.lang.String
     * @throws  IOException ex
     * @author  Gopal.pan
     */
    public static String doPostSSL(String url, Map<String, Object> params) throws IOException {

        try(CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
                .setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build()) {
            return doMapPost(httpClient,url,params);
        }
    }




    /**
     * 发送 SSL POST 请求（HTTPS），JSON形式
     * <p>  time 15:06 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param url  API接口URL
     * @param json JSON对象
     * @return  java.lang.String
     * @throws  IOException ex
     * @author  Gopal.pan
     */
    public static String doPostSSL(String url, Object json) throws IOException {

        try(CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
                .setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build()){
          return doJsonPost(httpClient,url,json);
        }



    }

    private static String doJsonPost(CloseableHttpClient httpClient, String url, Object json) throws IOException {

        if(StringUtils.isEmpty(url)){
            LOGGER.info("【FW-utils】POST请求地址为空,不发送请求");
            return "";

        }

        HttpPost httpPost = new HttpPost(url);
        LOGGER.info("【FW-utils】 POST请求地址为：[{}],请求参数为：[{}]",url,json.toString());
        httpPost.setConfig(requestConfig);
        StringEntity stringEntity = new StringEntity(json.toString(), Consts.UTF_8);
        stringEntity.setContentEncoding("UTF-8");
        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);
        String response = httpClient.execute(httpPost, responseHandler());
        LOGGER.info("【FW-utils】POST请求响应结果为[{}]",response);
        return response;
    }


    private static String doMapPost(CloseableHttpClient httpClient, String url, Map<String, Object> params) throws IOException {

        if(StringUtils.isEmpty(url)){
            LOGGER.info("【FW-utils】POST请求地址为空,不发送请求");
            return "";

        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        httpPost.setEntity(buildFormParams(params));
        LOGGER.info("【FW-utils】POST请求地址为[{}],请求参数[{}]",url,params.toString());
        String response = httpClient.execute(httpPost, responseHandler());
        LOGGER.info("【FW-utils】POST请求响应结果为[{}]",response);
        httpPost.abort();
        return response;
    }

    /**
     * 创建SSL安全连接
     * @return
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslsf;
    }



}