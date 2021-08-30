package com.github.sylphlike.framework.file.qiniu;


import com.qiniu.cdn.CdnManager;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * <p>  time 17:56 2019/03/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class QiniuUtils  {

    private static final Logger LOGGER = LoggerFactory.getLogger(QiniuUtils.class);

    private static QiniuConfig qiniuConfig;



    /** 私有文件下载地址截止时间 2070-05-12 10:56:55 */
    private static final Long expireTimeMillis = 3167089015791L;


    private static Configuration configuration;
    private static Auth auth;
    private static UploadManager uploadManager;
    private static BucketManager bucketManager;
    private static CdnManager cdnManager;

    public static String localTemp;




    QiniuUtils(QiniuConfig qiniuConfig){
        if(!qiniuConfig.isEnable()) return;
        LOGGER.info("【FW-file】初始化七牛云配置");
        QiniuUtils.qiniuConfig = qiniuConfig;
        localTemp = qiniuConfig.getLocalTemp();

        configuration = new Configuration();
        auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
        uploadManager = new UploadManager(configuration);
        bucketManager = new BucketManager(auth, configuration);
        cdnManager = new CdnManager(auth);


    }

    
    /**
     * 字节文件上传
     * @param fileName   存储到服务上的全文件名
     * @param uploadByte 文件字节数组
     * @return  boolean
     * @author  Gopal.pan
     */
    public static boolean upload(String fileName, byte[] uploadByte)  {
        try {
            String upToken = auth.uploadToken(qiniuConfig.getBucket());
            Response response = uploadManager.put(uploadByte, fileName, upToken);
            LOGGER.info("【FW-boxing】七牛云上传文件返回结果[{}]",response);
        } catch (QiniuException e) {
            LOGGER.error("【FW-file】七牛云上传文件系统异常",e);
            return false;
        }

        return true;
    }


    
    /**
     * 获取私有下载地址
     * @param fileFullName  文件全限定名称
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String privateDownloadUrl(String fileFullName)  {

        String downloadUrl = null;
        String encodedFileName = URLEncoder.encode(fileFullName, StandardCharsets.UTF_8).replace("+", "%20");
        String publicUrl = String.format("%s/%s", qiniuConfig.getDomain(), encodedFileName);
        Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
        downloadUrl = auth.privateDownloadUrlWithDeadline(publicUrl, expireTimeMillis);
        return downloadUrl;
    }

 
    
    /**
     * 文件下载
     * @param url     文件地址
     * @param interimPath 保存目录
     * @return  boolean
     * @author  Gopal.pan
     */
    public static boolean download(String url,String interimPath){
        boolean flag = true;
        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder().url(url).build();
        okhttp3.Response resp = null;
        FileOutputStream fops = null;
        try {
            resp = client.newCall(req).execute();
            if(resp.isSuccessful()) {
                ResponseBody body = resp.body();
                if(body != null){
                    InputStream is = body.byteStream();
                    byte[] data = readInputStream(is);
                    File imgFile = new File( interimPath);
                    fops = new FileOutputStream(imgFile);
                    fops.write(data);
                    fops.close();
                }

            }
        } catch (IOException e) {
            LOGGER.error("【FW-file】 下载文件时异常",e);
            flag = false;

        }finally {
            try {
                if(fops != null)
                    fops.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;


    }


    /**
     * 删除文件
     * @param fileName 文件名称
     * @return  boolean
     * @author  Gopal.pan
     */
    public static boolean delete(String fileName){

        boolean reslut = false;
        try {
            if (fileName.startsWith(qiniuConfig.getDomain())) {
                fileName = fileName.replace(qiniuConfig.getDomain(), "");
            }
            bucketManager.delete(qiniuConfig.getBucket(), fileName);
            reslut = true;
        } catch (QiniuException ex) {
            LOGGER.error("【FW-file】七牛云删除文件系统异常",ex);
        }
        return reslut;
    }


    
    /**
     * 重命名
     * @param oldFileName  原文件名
     * @param newFileName  新文件名
     * @return  boolean
     * @author  Gopal.pan
     */
    public static boolean rename(String oldFileName,String newFileName){

        if (oldFileName.startsWith(qiniuConfig.getDomain())) {
            oldFileName = oldFileName.replace(qiniuConfig.getDomain(), "");
        }
        if (newFileName.startsWith(qiniuConfig.getDomain())) {
            newFileName = newFileName.replace(qiniuConfig.getDomain(), "");
        }


        boolean result = true;
        String toKey = newFileName;
        Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, configuration);
        try {
            bucketManager.move(qiniuConfig.getBucket(), oldFileName, qiniuConfig.getBucket(), toKey);
        } catch (QiniuException ex) {
            LOGGER.error("【FW-file】重命名文件系统异常",ex);
            result = false;
        }
        return result;
    }

    /**
     * 获取七牛云上传token
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String uploadToken(){

        long expireSeconds = (expireTimeMillis - System.currentTimeMillis()) / 1000;
        String uploadToken = auth.uploadToken(qiniuConfig.getBucket(), null, expireSeconds, null, true);
        LOGGER.info("【FW-file】生成的上传token[{}]",uploadToken);
        return uploadToken;

    }


    private static byte[] readInputStream(InputStream is) {
        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        byte[] buff = new byte[1024 * 2];
        int len = 0;
        try {
            while((len = is.read(buff)) != -1) {
                writer.write(buff, 0, len);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toByteArray();
    }




}
