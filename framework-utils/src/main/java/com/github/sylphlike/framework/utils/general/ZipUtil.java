package com.github.sylphlike.framework.utils.general;

import com.github.sylphlike.framework.utils.UtilException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>  time 17:56 2021/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class ZipUtil {
    private static final Logger LOGGER =  LoggerFactory.getLogger(ZipUtil.class);


    /**
     * 压缩指定目录下的文件
     * @param originDir   需要压缩的文件ja
     * @param zipFile     压缩后的文件
     * @return  boolean
     * @author  Gopal.pan
     */
    public static boolean zip(String originDir,String zipFile) {
        boolean flag = true;
        try {
            if(!zipFile.endsWith(".zip")){
                flag = false;
                LOGGER.info("【FW-utils】文件压缩,压缩文件保存名称不合法 文件名[{}]",zipFile);
            }else {
                validateFiles(new File(zipFile),new File(originDir));

                OutputStream out = new FileOutputStream(zipFile);

                toZip(originDir,out,false);

            }
        } catch (FileNotFoundException e) {
            LOGGER.error("【framework】文件压缩,系统异常",e);
            flag = false;
        }


        return flag;
    }







    private static final int  BUFFER_SIZE = 2 * 1024;


    /**
     * 压缩成ZIP 方法1
     * @param srcDir 压缩文件夹路径
     * @param out    压缩文件输出流
     * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构; false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws  RuntimeException ex
     * @author  Gopal.pan
     */
    public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure) throws RuntimeException{
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile,zos,sourceFile.getName(),KeepDirStructure);
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null ) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }



    /**
     * 压缩成ZIP 方法2
     * @param srcFiles 需要压缩的文件列表
     * @param out 	        压缩文件输出流
     * @throws  RuntimeException ex
     * @author  Gopal.pan
     */
    public static void toZip(List<File> srcFiles , OutputStream out)throws RuntimeException {
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1){
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    /**
     * 压缩3
     * @param sourceFile 源文件
     * @param zos   zip输出流
     * @param name  压缩后的名称
     * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构; false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws  Exception ex
     * @author  Gopal.pan
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStructure) throws Exception{
        byte[] buf = new byte[BUFFER_SIZE];
        if(sourceFile.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if(KeepDirStructure){
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }

            }else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(),KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(),KeepDirStructure);
                    }

                }
            }
        }
    }




    /**
     * 判断压缩文件保存的路径是否为源文件路径的子文件夹，如果是，则抛出异常（防止无限递归压缩的发生）
     * @param zipFile  压缩后的产生的文件路径
     * @param originFile 被压缩的文件或目录
     * @throws  UtilException ex
     * @author  Gopal.pan
     */
    private static void validateFiles(File zipFile, File... originFile) throws UtilException {
        if (zipFile.isDirectory()) {
            throw new UtilException("Zip file ["+ zipFile.getAbsoluteFile()+"] must not be a directory !");
        }

        for (File srcFile : originFile) {
            if (null == srcFile) {
                continue;
            }
            if (!srcFile.exists()) {
                throw new UtilException("File ["+srcFile.getAbsolutePath()+"] not exist!");
            }

            try {
                final File parentFile = zipFile.getCanonicalFile().getParentFile();
                // 压缩文件不能位于被压缩的目录内
                if (srcFile.isDirectory() && parentFile.getCanonicalPath().contains(srcFile.getCanonicalPath())) {
                    throw new UtilException("Zip file path ["+zipFile.getCanonicalPath()+"] must not be the child directory of ["+ srcFile.getCanonicalPath()+"] !");
                }

            } catch (IOException e) {
                throw new UtilException(e);
            }
        }
    }
}
