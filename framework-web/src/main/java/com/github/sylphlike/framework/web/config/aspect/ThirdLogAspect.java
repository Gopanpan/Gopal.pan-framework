package com.github.sylphlike.framework.web.config.aspect;

import com.github.sylphlike.framework.norm.CharsetUtil;
import com.github.sylphlike.framework.web.utils.ParamFormat;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * third 请求、响应日志记录 aspect
 * <p>  time 17:56 2018/06/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

@Aspect
@Component
public class ThirdLogAspect {

    protected Logger logger =  LoggerFactory.getLogger(ThirdLogAspect.class);

    @Pointcut("execution(public * com.github.sylphlike.framework.web.BaseThirdService+.*(..))")
    public void thirdRequestLog(){}

    @Before("thirdRequestLog()")
    public void doBefore(JoinPoint joinPoint) {
        String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
        int index = declaringTypeName.lastIndexOf(".");
        logger.info("【FW-web】third请求,执行方法[{}]; 参数[{}]",
                StringUtils.join(declaringTypeName.substring(++index) , CharsetUtil.CHAR_ENGLISH_DOT, joinPoint.getSignature().getName()),
                ParamFormat.format(((MethodSignature)joinPoint.getSignature()).getParameterNames(), joinPoint.getArgs()));
    }

    @AfterReturning(returning = "ret", pointcut = "thirdRequestLog()")
    public void doAfterReturning(Object ret) {
        logger.info("【FW-web】third响应,[{}]", ret);
    }

}
