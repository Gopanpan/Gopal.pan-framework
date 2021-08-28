package com.github.sylphlike.framework.utils.general;


import com.github.sylphlike.framework.utils.UtilException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import java.io.*;

/**
 * <p>  time 17:56 2018/12/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class FileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);


    public static final char CHAR_ENGLISH_SLASH = '/';
    public static final char CHAR_ENGLISH_BACKSLASH = '\\';
    public static final char CHAR_ENGLISH_DOT = '.';

    /**
     * 判断指定文件夹是否存在，不存在是创建文件夹
     * @param dirPath  目录地址
     * @author  Gopal.pan
     */
    public static void pathExistCreate(String dirPath){
        File file = new File(dirPath);
        if(!file.exists())
            file.mkdirs();

    }

    /**
     * InputStream 转file
     * @param ins  输入流
     * @param file 文件
     * @throws  Exception ex
     * @author  Gopal.pan
     */
    public static void inputStreamToFile(InputStream ins, File file) throws Exception {
        OutputStream os = new FileOutputStream(file);
        try {
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } finally {
            os.close();
            ins.close();
        }

    }


    /**
     * 文件转字节数组
     * @param file    文件
     * @return  byte[]
     * @author  Gopal.pan
     */
    public static byte[] fileToByteArray(File file){
        byte[] bytes = null;
        try(FileInputStream fis = new FileInputStream(file);ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                // 将指定字节数组中从偏移量 off 开始的 len 个字节写入此字节数组输出流。
                byteArrayOutputStream.write(buffer, 0, len);
            }
            bytes = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bytes;
    }


    /**
     * 根据byte[] 数组生成文件
     * @param bfile  字节数组
     * @param filePath 文件路径
     * @param fileName 文件名
     * @author  Gopal.pan
     */
    public static void getFile(byte[] bfile, String filePath,String fileName) {
        BufferedOutputStream bos = null;  //带缓冲得文件输出流
        FileOutputStream fos = null;      //文件输出流
        File file = null;
        try {
            File dir = new File(filePath);
            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);  //文件路径+文件名
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    /**
     * 图片转byte数组
     * @param path      图片地址
     * @return  byte[]
     * @author  Gopal.pan
     */
    public static byte[] image2byte(String path){
        byte[] data = null;
        try( FileImageInputStream input =  new FileImageInputStream(new File(path)); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
        } catch (Exception e) {
            LOGGER.error("【FW-utils】图片转byte数组异常",e);
        }

        return data;
    }


    /**
     * byte数组转图片
     * @param data  数组
     * @param path   生成图片保存地址目录
     * @author  Gopal.pan
     */
    public static void byte2Image(byte[] data, String path){
        if(data.length <3 ||path.equals("")) return;
        try( FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path))){
            imageOutput.write(data, 0, data.length);
        } catch(Exception ex) {
            LOGGER.error("【FW-utils】byte数组转图片异常",ex);
        }
    }


    /**
     * 删除单个文件
     * @param fileName   要删除的文件的文件名
     * @return  boolean  单个文件删除成功返回true，否则返回false
     * @author  Gopal.pan
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                LOGGER.info("【FW-utils】删除文件失败,文件全路径[{}]",fileName);
                return false;
            }
        } else {
            LOGGER.info("【FW-utils】删除文件失败,文件全路径[{}]",fileName);
            return false;
        }
    }

    /**
     * 删除文件或文件夹
     * @param fileDir 文件地址
     * @author  Gopal.pan
     */
    public static void delAllFile(File fileDir){

        if (!fileDir.isDirectory()){
            fileDir.delete();
        } else{
            File [] files = fileDir.listFiles();

            // 空文件夹
            if (files.length == 0){
                fileDir.delete();
                return;
            }

            // 删除子文件夹和子文件
            for (File file : files){
                if (file.isDirectory()){
                    delAllFile(file);
                } else {
                    file.delete();
                }
            }

            // 删除文件夹本身
            fileDir.delete();

        }
    }



    /**
     * 创建File对象
     * <p> 此方法会检查slip漏洞，漏洞说明见http://blog.nsfocus.net/zip-slip-2/
     * @param parent 父文件对象
     * @param path   文件路径
     * @return  java.io.File
     * @throws  IOException ex
     * @author  Gopal.pan
     */
    public static File file(File parent, String path) throws IOException {
        return checkSlip(parent, new File(parent, path));
    }

    /**
     * 返回主文件名
     *
     * @param file 文件
     * @return 主文件名
     */
    public static String mainName(File file) {
        if (file.isDirectory()) {
            return file.getName();
        }
        String fileName = file.getName();

        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        int len = fileName.length();
        if (0 == len) {
            return fileName;
        }
        if (isFileSeparator(fileName.charAt(len - 1))) {
            len--;
        }

        int begin = 0;
        int end = len;
        char c;
        for (int i = len - 1; i >= 0; i--) {
            c = fileName.charAt(i);
            if (len == end && CHAR_ENGLISH_DOT == c) {
                // 查找最后一个文件名和扩展名的分隔符：.
                end = i;
            }
            // 查找最后一个路径分隔符（/或者\），如果这个分隔符在.之后，则继续查找，否则结束
            if (isFileSeparator(c)) {
                begin = i + 1;
                break;
            }
        }

        return fileName.substring(begin, end);
    }



    /**
     * 检查父完整路径是否为自路径的前半部分，如果不是说明不是子路径，可能存在slip注入。
     * 见http://blog.nsfocus.net/zip-slip-2/
     * @param parentFile 父文件或目录
     * @param file       子文件或目录
     * @return  java.io.File  子文件或目录
     * @throws  IllegalArgumentException ex
     * @throws  IOException ex
     * @author  Gopal.pan
     */
    public static File checkSlip(File parentFile, File file) throws IllegalArgumentException, IOException {
        if (null != parentFile && null != file) {
            String parentCanonicalPath;
            String canonicalPath;

            parentCanonicalPath = parentFile.getCanonicalPath();
            canonicalPath = file.getCanonicalPath();

            if (!canonicalPath.startsWith(parentCanonicalPath)) {
                throw new IllegalArgumentException("New file is outside of the parent dir: " + file.getName());
            }
        }
        return file;
    }


    /**
     * 获得一个输出流对象
     * @param file  文件
     * @return  java.io.BufferedOutputStream
     * @author  Gopal.pan
     */
    public static BufferedOutputStream getOutputStream(File file)  {
        try {
            return new BufferedOutputStream(new FileOutputStream(touch(file)));
        } catch (Exception e) {
            throw new UtilException(e);
        }
    }



    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件
     *  此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     * @param file 文件对象
     * @return  java.io.File 文件，若路径为null，返回null
     * @author  Gopal.pan
     */
    public static File touch(File file) {
        if (null == file) {
            return null;
        }
        if (!file.exists()) {
            mkParentDirs(file);
            try {

                file.createNewFile();
            } catch (Exception e) {
                throw new UtilException(e);
            }
        }
        return file;
    }

    /**
     * 创建所给文件或目录的父目录
     *
     * @param file 文件或目录
     * @return 父目录
     */
    public static File mkParentDirs(File file) {
        final File parentFile = file.getParentFile();
        if (null != parentFile && !parentFile.exists()) {
            parentFile.mkdirs();
        }
        return parentFile;
    }



    /**
     * 是否为Windows或者Linux（Unix）文件分隔符
     * Windows平台下分隔符为\，Linux（Unix）为/
     * @param c 字符
     * @return 是否为Windows或者Linux（Unix）文件分隔符
     */
    public static boolean isFileSeparator(char c) {
        return CHAR_ENGLISH_SLASH == c || CHAR_ENGLISH_BACKSLASH == c;
    }



}
