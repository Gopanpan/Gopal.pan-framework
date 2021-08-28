package com.github.sylphlike.framework.utils.sequence;

import com.github.sylphlike.framework.utils.general.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;


/**
 * <p>  time 17:56 2018/06/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class MarkProvider {

    private static final Logger logger = LoggerFactory.getLogger(MarkProvider.class);


    /**
     * 数据中心ID
     * @param maxDataCenterId  最大数据中心ID
     * @return  long
     * @author  Gopal.pan
     */
    public static long getDataCenterId(long maxDataCenterId){
        long id = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                    id = id % (maxDataCenterId + 1);
                }
            }
        } catch (Exception e) {
            logger.error("【FW-utils】获取数据中心标识，系统异常",e);
        }
        return id;

    }




    /**
     * 工作机器ID
     * @param dataCenterId   数据中心ID
     * @param maxWorkerId    最大工作机器ID
     * @return  long
     * @author  Gopal.pan
     */
    protected static long getWorkerId(long dataCenterId, long maxWorkerId) {
        StringBuilder builder = new StringBuilder();
        builder.append(dataCenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        builder.append(name);
        List<String> address = OSUtils.macAddress();
        if( address != null && !address.isEmpty()){
            String mac = address.get(0);
            builder.append(mac);
        }

        return (builder.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }





}
