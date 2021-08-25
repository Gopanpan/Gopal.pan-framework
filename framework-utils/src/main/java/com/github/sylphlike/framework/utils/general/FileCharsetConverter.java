package com.github.sylphlike.framework.utils.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;


/**
 * 文件编码转换工具类
 * <p>  time 17:56 2018/12/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class FileCharsetConverter {
    private static final Logger LOGGER =  LoggerFactory.getLogger(FileCharsetConverter.class);



    /**
     * 将文件或文件夹下的所有文件转换成指定的编码集,不可以使用多级目录
     * <p>  time 15:02 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param file              要转换的文件或文件夹全路径
     * @param fromCharsetName   源文件的编码
     * @param toCharsetName     要转换的编码
     * @author  Gopal.pan
     */
    public static void convertCharset(String file,String fromCharsetName,  String toCharsetName){
        LOGGER.info("【framework】 文件编码集转换 file[{}],fromCharsetName[{}],toCharsetName[{}]",file,fromCharsetName,toCharsetName);

        try {
            File dirFile = new File(file);
            if(dirFile.isDirectory()){
                File[] files = dirFile.listFiles();
                if(files != null){
                    for (File  singleFile: files ) {
                        convert(singleFile,fromCharsetName ,toCharsetName);
                    }
                }
            }else {
                convert(dirFile,fromCharsetName ,toCharsetName);
            }
        } catch (Exception e) {
            LOGGER.error("【FW-utils】文件编码集转换,失败",e);
        }

    }


    /**
     * 把指定文件转换成指定的编码
     * <p>  time 15:02 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param file              要转换的文件
     * @param fromCharsetName   源文件的编码
     * @param toCharsetName     要转换的编码
     * @throws  Exception ex
     * @author  Gopal.pan
     */
    public static void convert(File file, String fromCharsetName,  String toCharsetName) throws Exception {
        String fileContent = getFileContentFromCharset(file, fromCharsetName);
        saveFile2Charset(file, toCharsetName, fileContent);

    }


    /**
     * 以指定编码方式读取文件，返回文件内容
     * <p>  time 15:02 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param file              要转换的文件
     * @param fromCharsetName   源文件的编码
     * @return  java.lang.String
     * @throws  Exception ex
     * @author  Gopal.pan
     */
    public static String getFileContentFromCharset(File file,  String fromCharsetName) throws Exception {
        if (!Charset.isSupported(fromCharsetName)) {
            throw new UnsupportedCharsetException(fromCharsetName);
        }
        InputStream inputStream = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(inputStream,  fromCharsetName);
        char[] chs = new char[(int) file.length()];
        reader.read(chs);
        String str = new String(chs).trim();
        reader.close();
        return str;
    }




    /**
     * 以指定编码方式写文本文件，存在会覆盖
     * <p>  time 15:06 2021/2/1       </p>
     * <p> email 15923508369@163.com  </p>
     * @param file             要写入的文件
     * @param toCharsetName    要转换的编码
     * @param content          文件内容
     * @author  Gopal.pan
     */
    public static void saveFile2Charset(File file, String toCharsetName,   String content) throws Exception {
        if (!Charset.isSupported(toCharsetName)) {
            throw new UnsupportedCharsetException(toCharsetName);
        }
        OutputStream outputStream = new FileOutputStream(file);
        OutputStreamWriter outWrite = new OutputStreamWriter(outputStream, toCharsetName);
        outWrite.write(content);
        outWrite.close();
    }

}
