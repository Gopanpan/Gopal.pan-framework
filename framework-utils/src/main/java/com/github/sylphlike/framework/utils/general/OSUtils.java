package com.github.sylphlike.framework.utils.general;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>  time 17:56 2018/12/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class OSUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(OSUtils.class);

    private static String INTRANET_IP ;
    private static String INTERNET_IP ;
    private static String MAC;
    private static String HOST_NAME;



    /**
     * 获得内网IP
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String intranetIp(){
        try{
            if(!StringUtils.isEmpty(INTERNET_IP)){
                return INTRANET_IP;
            }
            return INTRANET_IP =  InetAddress.getLocalHost().getHostAddress();
        } catch(Exception e){
            LOGGER.error("【FW-utils】获取内网IP地址时发生异常",e);
        }
        return null;
    }



    /**
     * 获得外网IP
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String internetIp(){
        try{
            if(!StringUtils.isEmpty(INTERNET_IP)){
                return INTERNET_IP;
            }
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            Enumeration<InetAddress> addrs;
            while (networks.hasMoreElements()) {
                addrs = networks.nextElement().getInetAddresses();
                while (addrs.hasMoreElements()){
                    ip = addrs.nextElement();
                    if (ip instanceof Inet4Address && ip.isSiteLocalAddress() && !ip.getHostAddress().equals(INTRANET_IP)) {
                        return INTERNET_IP = ip.getHostAddress();
                    }
                }
            }
        } catch(Exception e){
            LOGGER.error("【FW-utils】获取外网网IP地址时发生异常",e);
        }
        return null;
    }

    /**
     * 获取MAC地址 C8-FF-28-5B-32-25
     * @return  java.util.List
     * @author  Gopal.pan
     */
    public static List<String> macAddress(){
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            StringBuilder sb = new StringBuilder();
            ArrayList<String> tmpMacList=new ArrayList<>();
            while(en.hasMoreElements()){
                NetworkInterface iface = en.nextElement();
                List<InterfaceAddress> addrs = iface.getInterfaceAddresses();
                for(InterfaceAddress addr : addrs) {
                    InetAddress ip = addr.getAddress();
                    NetworkInterface network = NetworkInterface.getByInetAddress(ip);
                    if(network==null){continue;}
                    byte[] mac = network.getHardwareAddress();
                    if(mac==null){continue;}
                    sb.delete( 0, sb.length() );
                    for (int i = 0; i < mac.length; i++) {sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));}
                    tmpMacList.add(sb.toString());
                }        }
            if(tmpMacList.size()<=0){return tmpMacList;}
            List<String> unique = tmpMacList.stream().distinct().collect(Collectors.toList());

            LOGGER.info("【FW-utils】当前系统的MAC地址信息为[{}]",unique);
            return unique;
        } catch (Exception e) {
            LOGGER.error("【FW-utils】获取MAC地址时发生异常",e);
        }
        return null;
    }



    public static String hostName(){

        try {
            if(!StringUtils.isEmpty(HOST_NAME)){
                return HOST_NAME;
            }
            InetAddress localHost = InetAddress.getLocalHost();
            return HOST_NAME = localHost.getHostName();
        } catch (UnknownHostException e) {
            LOGGER.error("【FW-utils】获取host名称时发生异常",e);
        }

        return null;

    }



    /**
     * 获取版本号
     * @return  java.lang.String
     * @author  Gopal.pan
     */
    public static String serviceVersion(){
        return OSUtils.class.getPackage().getImplementationVersion();
    }


    /**
     * 得到计算机的ip,名称,操作系统名称,操作系统版本
     * @return  集合
     * @author  Gopal.pan
     */
    public static Map<String,String> systemConfig() {
        try {
            Map<String,String> map = new HashMap<>();
            Properties props = System.getProperties();

            //操作系统名称
            map.put("osName",props.getProperty("os.name"));
            //操作系统的版本
            map.put("osVersion",props.getProperty("os.version"));
            //操作系统的构架
            map.put("osArch",props.getProperty("os.arch"));

            //行分隔符
            map.put("lineSeparator",props.getProperty("line.separator"));

            //Java的安装路径
            map.put("javaHome",props.getProperty("java.home"));
            //Java的运行环境版本
            map.put("javaVersion",props.getProperty("java.version"));
            //Java的运行环境供应商
            map.put("javaVendor",props.getProperty("java.vendor"));
            //Java供应商的URL
            map.put("javaVendorUrl",props.getProperty("java.vendor.url"));
            //Java的虚拟机规范版本
            map.put("javaVmSpecificationVersion",props.getProperty("java.vm.specification.version"));
            //Java的虚拟机规范供应商
            map.put("javaVmSpecificationVendor",props.getProperty("java.vm.specification.vendor"));

            //Java运行时环境规范版本
            map.put("javaSpecificationVersion",props.getProperty("java.specification.version"));
            //Java运行时环境规范名称
            map.put("javaSpecificationName",props.getProperty("java.specification.name"));
            //Java的类格式版本号
            map.put("javaClassVersion",props.getProperty("java.class.version"));
            //Java的类路径
            map.put("javaClassPath",props.getProperty("java.class.path"));

            //加载库时搜索的路径列表
            map.put("javaLibraryPath",props.getProperty("java.library.path"));
            //默认的临时文件路径
            map.put("javaIoTmpdir",props.getProperty("java.io.tmpdir"));
            //用户的当前工作目录
            map.put("userDir",props.getProperty("user.dir"));
            //用户的账户名称
            map.put("userName",props.getProperty("user.name"));
            //用户的主目录
            map.put("userHome",props.getProperty("user.home"));
            //JVM实例编号
            map.put("jvmIndex",props.getProperty("jvm.index"));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }


}
