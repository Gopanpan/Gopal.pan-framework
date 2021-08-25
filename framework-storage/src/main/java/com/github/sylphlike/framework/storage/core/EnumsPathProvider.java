package com.github.sylphlike.framework.storage.core;

import com.github.sylphlike.framework.norm.StorageInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
/**
 * <p>  time 14:24 2021/07/02  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
public class EnumsPathProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumsPathProvider.class);

    public static List<Class<?>> getAllAssignedClass(String enumPackage) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL resource = classloader.getResource(enumPackage);
        if(resource == null){
            return classes;
        }
        String protocol = resource.getProtocol();
        if(ResourceEnum.FILE.getCode().equals(protocol)){
            File fileDir = new File(resource.getFile());
            if (!fileDir.exists()) {
                return classes;
            }
            getImplementationClass(resource.getFile(),classes);
        }else {
            String jarPath = resource.getPath();
            JarFile jarFile = null;
            try {
                URL url = new URL("jar", null, 0, jarPath);
                jarFile = ((JarURLConnection) url.openConnection()).getJarFile();

                Enumeration<JarEntry> ee = jarFile.entries();
                while (ee.hasMoreElements()) {
                    JarEntry entry = ee.nextElement();
                    if (entry.getName().startsWith(enumPackage) && entry.getName().endsWith(".class")) {
                        String className = entry.getName().replace('/', '.');
                        className = className.substring(0, className.length() - 6);
                        Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(className);
                        if(StorageInterface.class.isAssignableFrom(aClass)){
                            classes.add(aClass);
                        }
                    }
                }

            } catch (IOException e1) {
                LOGGER.error("【FW-storage】解析jar包文件异常",e1);
            } finally {
                if (null != jarFile) {
                    try {
                        jarFile.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return classes;
    }




    public static void getImplementationClass(String directoryPath, List<Class<?>> classes) throws ClassNotFoundException {
        File baseFile = new File(directoryPath);
        if (baseFile.isFile() || !baseFile.exists()) {
            return ;
        }
        File[] files = baseFile.listFiles();

        if(files == null)
            return ;

        for (File file : files) {
            if (file.isDirectory()) {
                LOGGER.debug("【FW-storage】 获取到的包路径[{}]",file.getAbsolutePath());
                getImplementationClass(file.getAbsolutePath(),classes);
            }else {
                String absolutePath = file.getAbsolutePath();
                String separator = File.separator;
                String fullName = absolutePath.substring(0, absolutePath.lastIndexOf( "com" + separator));
                int index = fullName.lastIndexOf(separator) + 1;
                String substring = absolutePath.substring(index, absolutePath.length() - 6);
                String targetPackageName = substring.replace(separator, ".");

                Class<?> aClass = Class.forName(targetPackageName);
                if(StorageInterface.class.isAssignableFrom(aClass)){
                    classes.add(aClass);
                }
            }
        }
    }

}
