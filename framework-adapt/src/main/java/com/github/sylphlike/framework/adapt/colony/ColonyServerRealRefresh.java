package com.github.sylphlike.framework.adapt.colony;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.utils.UtilAndComs;
import com.netflix.loadbalancer.ServerListUpdater;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static com.alibaba.nacos.api.PropertyKeyConst.*;

/**
 * 实时刷新nacos注册中心的服务列表
 * <p>  time 10:12 2021/06/07  星期一 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
@Component
public class ColonyServerRealRefresh  implements ApplicationRunner {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final NacosDiscoveryProperties nacosDiscoveryProperties;
    private final ColonyServerListUpdate colonyServerListUpdate;
    private final ColonyProperties colonyProperties;

    public ColonyServerRealRefresh(NacosDiscoveryProperties nacosDiscoveryProperties, ColonyServerListUpdate colonyServerListUpdate, ColonyProperties colonyProperties) {
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
        this.colonyServerListUpdate = colonyServerListUpdate;
        this.colonyProperties = colonyProperties;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {

        Properties properties = new Properties();

        properties.put(SERVER_ADDR, nacosDiscoveryProperties.getServerAddr());
        properties.put(USERNAME, Objects.toString(nacosDiscoveryProperties.getUsername(), ""));
        properties.put(PASSWORD, Objects.toString(nacosDiscoveryProperties.getPassword(), ""));
        properties.put(NAMESPACE, nacosDiscoveryProperties.getNamespace());
        properties.put(UtilAndComs.NACOS_NAMING_LOG_NAME, nacosDiscoveryProperties.getLogName());
        properties.put(ACCESS_KEY, nacosDiscoveryProperties.getAccessKey());
        properties.put(SECRET_KEY, nacosDiscoveryProperties.getSecretKey());
        properties.put(CLUSTER_NAME, nacosDiscoveryProperties.getClusterName());
        properties.put(NAMING_LOAD_CACHE_AT_START, nacosDiscoveryProperties.getNamingLoadCacheAtStart());

        NamingService namingService = NamingFactory.createNamingService(properties);


        List<String> serverNames = colonyProperties.getServerNames();
        if(serverNames == null){
            return;
        }
        String groupName = colonyProperties.getGroupName();
        for (String serverName : serverNames) {
            namingService.subscribe(serverName,groupName, event -> {
                LOGGER.info("【FW-adapt】【服务刷新】订阅的服务名称[{}]",serverName);
                if(event instanceof NamingEvent){
                    List<Instance> instances = ((NamingEvent) event).getInstances();
                    if(ObjectUtils.isNotEmpty(instances)){
                        instances.forEach(instance -> {
                            LOGGER.info("【FW-adapt】【服务刷新】订阅的服务实例信息[{}]",instance.getInstanceId());
                        });
                    }else {
                        LOGGER.info("【FW-adapt】【服务刷新】订阅的服务下无可用实例");
                    }
                    ServerListUpdater.UpdateAction updateAction = colonyServerListUpdate.getUpdateAction();
                    if (updateAction != null){
                        updateAction.doUpdate();
                    }
                }
            });
        }

    }
}
