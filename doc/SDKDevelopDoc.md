# SDK feign开发接口文档
SDK提供是为了简化接入系统接入的技术复杂度，便于开发人员开发，提高开发效率，降低接入成本。

### develop 
- springboot自动装配。 resources 下增加 META-INF文件夹 新建配置文件 spring.factories
- feign默认使用 JDK中的HttpURLConnection向下游系统发起http请求，缺乏连接池的支持.  
改进使用Apache下的httpClient如下：

```xml

<!-- 引feign-httpclient依赖包 spring boot 默认启用了 feign.httpclient.enabled -->
<dependency>
	<groupId>io.github.openfeign</groupId>
	<artifactId>feign-httpclient</artifactId>
</dependency>

``` 



### 客服端接入
- 1、SDK使用Hystrix作为系统降级处理，默认熔断时间为5000ms。
- 2、SDK启用了feign请求响应压缩
- 3、业务系统可以重载1,2项配置，配置如下：

 ```yaml

feign:
  compression:
    request:
      enabled: true
      mime-types: text/xml,application/xml,application/json
      min-request-size: 2048
    response:
      enabled: true
  hystrix:
    enabled: true

hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 5000

```
