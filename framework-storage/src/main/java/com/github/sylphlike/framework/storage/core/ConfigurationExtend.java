package com.github.sylphlike.framework.storage.core;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.github.pagehelper.PageInterceptor;
import com.github.sylphlike.framework.storage.config.ScanningProperties;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;


/**
 *
 * <p>  time 19:28 2021/07/05  星期一 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
@Component
public class ConfigurationExtend implements ConfigurationCustomizer {

    Logger logger = LoggerFactory.getLogger(ConfigurationExtend.class);

    private final ScanningProperties scanningProperties;

    public ConfigurationExtend(ScanningProperties scanningProperties) {
        this.scanningProperties = scanningProperties;
    }

    @Override
    public void customize(MybatisConfiguration configuration) {
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        try {
            final List<Class<?>> allAssignedClass = EnumsPathProvider.getAllAssignedClass(scanningProperties.getEnumPackage());
            allAssignedClass.forEach((clazz) ->{
                logger.info("【FW-storage】 mybatis枚举映射[{}]" , clazz);
                typeHandlerRegistry.register(clazz, GeneralTypeHandler.class);
            });


            //分页插件
            Properties properties = new Properties();
            properties.setProperty("helperDialect", "mysql");
            properties.setProperty("supportMethodsArguments", "true");
            properties.setProperty("returnPageInfo", "check");
            properties.setProperty("params", "count=countSql");
            PageInterceptor pageInterceptor = new PageInterceptor();
            pageInterceptor.setProperties(properties);

            configuration.addInterceptor(pageInterceptor);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }




}
