package com.github.sylphlike.framework.web.config.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sylphlike.framework.adapt.UserContextHolder;
import com.github.sylphlike.framework.norm.Response;
import com.github.sylphlike.framework.web.exception.AssertUtils;
import com.github.sylphlike.framework.web.utils.ParamFormat;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;


/**
 * web增强处理
 * <p> 1 入参出参日志记录 2 移除 ThreadLocal数据 </p>
 * <p>  time 17:56 2018/06/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
@Aspect
@Component
public class WebAspect {
    private final Logger LOGGER =  LoggerFactory.getLogger(WebAspect.class);

    private final ObjectMapper mapper;

    public WebAspect(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Pointcut("execution(public * com.github.sylphlike.framework.web.BaseController+.*(..))")
    public void webPointcut(){}

    @Before( value = "webPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        if(joinPoint.getSignature().getName().equals("setGeneralReqAndRes")){
            return;
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        AssertUtils.notNull(attributes,"ServletRequestAttributes is null");
        HttpServletRequest request = attributes.getRequest();

        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;

        LOGGER.info("【FW-web】【request】{} {},params[{}]",
                request.getMethod(),
                request.getRequestURI(),
                ParamFormat.format(methodSignature.getParameterNames(), joinPoint.getArgs()));
    }

    @AfterReturning(returning = "ret", pointcut = "webPointcut()")
    public void doAfterReturning(JoinPoint joinPoint,Object ret) {
        try {
            MethodSignature sign = (MethodSignature) joinPoint.getSignature();
            Method method = sign.getMethod();

            if(!joinPoint.getSignature().getName().equals("setGeneralReqAndRes")){
                LogStreamline logStreamline = method.getAnnotation(LogStreamline.class);
                if (null != logStreamline){
                    if(ret instanceof Response){
                        Response<?> response = (Response<?>) ret;
                        Response<Object> logStreamLine = new Response<>();
                        logStreamLine.setCode(response.getCode());
                        logStreamLine.setMsg(response.getMsg());
                        logStreamLine.setTimestamp(response.getTimestamp());
                        LOGGER.info("【FW-web】【response】过滤业务参数[{}]", mapper.writeValueAsString( logStreamLine) );
                    }else {
                        LOGGER.info("【FW-web】【response】当前返回实体不为系统定义类型,[{}]",ret);
                    }
                }else {
                    if(ret instanceof Response){
                        LOGGER.info("【FW-web】【response】data[{}]", mapper.writeValueAsString( ret));
                    }else {
                        LOGGER.info("【FW-web】【response】,当前返回实体不为系统定义类型,[{}]",ret);
                    }
                }

                UserContextHolder.resetUserAttributes();
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("【FW-web】response,json格式化响应参数异常",e);
        }

    }


    /**
     * 移除当前请求用户上下文，不处理异常， 异常由通用异常处理
     * @author  Gopal.pan
     */
    @AfterThrowing(throwing = "throwable", pointcut = "webPointcut()")
    public void afterThrowing(Throwable throwable){
        UserContextHolder.resetUserAttributes();

    }



    private String requestAnnotationName(Method method) {

        String description = "";
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if(null != requestMapping){
            description = requestMapping.name();
        }else if (null != getMapping){
            description = getMapping.name();
        }else if (null != postMapping){
            description = postMapping.name();
        }else if (null != deleteMapping){
            description = deleteMapping.name();
        }else if (null != putMapping)
            description = putMapping.name();

        return description;
    }

}
