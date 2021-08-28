package com.github.sylphlike.framework.web.endpoint;

import com.github.sylphlike.framework.adapt.Constants;
import com.github.sylphlike.framework.norm.CharsetUtil;
import com.github.sylphlike.framework.norm.Response;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

/**
 * 系统类对外提供接口服务
 * <p> 接入需要满足规范
 *      1 类上需有 RequestMapping 并且指定name字段值 ， 该name 用于 controllers类说明
 *      2 方法上可以使用标准的 GetMapping,PostMapping 等， 需指定name字段值，该值用于描述方法作用
 *      缺少1,2规范小的name值时， 接口说明字段将为空
 * </p>
 * <p>  time 17:56 2021/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </p>
 * @author Gopal.pan
 * @version 1.0.0
 */
@RestController
@RequestMapping(value = "/endpoint")
public class EndpointHandler {

    private final Logger LOGGER =  LoggerFactory.getLogger(EndpointHandler.class);

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public EndpointHandler(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    private static final List<String> FILTER_ADDRESS = new ArrayList<>( Arrays.asList(
            "/endpoint/business/url","/swagger-resources/configuration/security","/swagger-resources/configuration/ui",
            "/swagger-resources","/v2/api-docs-ext","/swagger-resources-ext","/error"
        ));


    @Value("${spring.application.name}")
    private String appName;



    /**
     * 系统中业务接口地址列表
     * @return Response<ApiVO>
     * @author  Gopal.pan
     */
    @GetMapping(value = "/business/url",name = "业务接口地址列表")
    public Response<ApiVO> businessUrl(){
        Map<Class<?>,List<ApiVO.ApiInfo>> groupMap = new HashMap<>();

        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        LOGGER.info("【FW-web】系统中全部URL地址个数为[{}]",map.size());

        ApiVO.ApiInfo apiInfo;

        Set<RequestMappingInfo> requestMappingInfos = map.keySet();
        for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
            Set<String> patterns = requestMappingInfo.getPatternsCondition().getPatterns();
            String urlPath = "";
            if(!patterns.isEmpty()){
                urlPath = patterns.iterator().next();
            }

            String methodType = "";
            Set<RequestMethod> methods = requestMappingInfo.getMethodsCondition().getMethods();
            if(!methods.isEmpty()){
                methodType = methods.iterator().next().name();
            }

            if(!FILTER_ADDRESS.contains(urlPath) && StringUtils.isNotEmpty(urlPath)){

                String[] split = ObjectUtils.defaultIfNull(requestMappingInfo.getName(), CharsetUtil.CHAR_ENGLISH_EMPTY).split("#");
                String name = split.length == Constants.DIGITAL_TWO ? split[Constants.DIGITAL_ONE] : "";

                apiInfo = new ApiVO.ApiInfo();
                apiInfo.setMethodType(methodType);
                apiInfo.setUrlPath(urlPath);
                apiInfo.setName(name);

                HandlerMethod handlerMethod = map.get(requestMappingInfo);
                Class<?> beanType = handlerMethod.getBeanType();
                List<ApiVO.ApiInfo> apiInfos = groupMap.get(beanType);
                if(ObjectUtils.isEmpty(apiInfos)){
                    groupMap.put(beanType,  new ArrayList<>(Collections.singletonList(apiInfo)));
                }else {
                    apiInfos.add(apiInfo);
                    groupMap.put(beanType, apiInfos);
                }
            }
        }

        List<ApiVO.Controllers> controllersList = new ArrayList<>();
        ApiVO.Controllers controllers;
        for (Class<?> aClass : groupMap.keySet()) {
            RequestMapping annotation = aClass.getAnnotation(RequestMapping.class);
            controllers = new ApiVO.Controllers();
            controllers.setName(aClass.getName());
            controllers.setSimpleName(aClass.getSimpleName());
            controllers.setDesc(annotation.name());
            controllers.setApiInfoList(groupMap.get(aClass));

            controllersList.add(controllers);
        }

        ApiVO apiVO = ApiVO.builder().appName(appName).controllers(controllersList).build();
        return new Response<>(apiVO);

    }
}
