# Prometheus + Grafana 监控

###  1.应用系统集成 micrometer

* pom 依赖jar包
```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>

```  
* 配置文件增加配置
```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"  # 打开所有 Actuator 服务,也可以打开指定的Actuator服务
  metrics:
    tags:
      application: ${spring.application.name} # 将应用名称添加到计量器的 tag 中去， 以便 Prometheus 根据应用名区分不同服务
```

* 启动类添加Bean 用于监控JVM性能指标
```java
    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer( @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }
```

### 2.部署Prometheus
* 官网地址：https://prometheus.io/
* 添加配置，配置文件地址  prometheus.yml
  * job_name 任务名称
  * metrics_path 指定监控端点的路径。  
  * targets 指定应用的IP端口

```yaml
  - job_name: 'unite-demo'
    metrics_path: '/unite-demo/actuator/prometheus'
    static_configs:
    - targets: ['http://s76hdb.natappfree.cc']
```

### 3.部署Grafana 
* 官网地址：https://grafana.com/grafana/
  
  

