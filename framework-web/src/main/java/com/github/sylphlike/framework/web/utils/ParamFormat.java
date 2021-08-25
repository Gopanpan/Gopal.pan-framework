package com.github.sylphlike.framework.web.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sylphlike.framework.adapt.JsonConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>  time 17:56 2019/10/29  星期五 </p>
 * <p> email 15923508369@163.com     </p>
 * @author Gopal.pan
 * @version 1.0.0
 */
public class ParamFormat {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParamFormat.class);

    private static final ObjectMapper mapper = JsonConfig.mapper();

    public static String format(String[] parameterNames, Object[] args){

        Map<String,Object> map = new HashMap<>();
        try {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof MultipartFile || arg instanceof ServletRequest || arg instanceof ServletResponse
                        || arg instanceof BeanPropertyBindingResult) {
                    LOGGER.info("【FW-web】请求参数格式化,参数类型在不需要格式化处理");
                    continue;
                }
                map.put(parameterNames[i],arg);
            }
            return mapper.writeValueAsString(map);

        } catch (Exception e) {
            LOGGER.error("【FW-web】请求参数格式化处理异常,返回原始参数", e);
            return  Arrays.toString(args);
        }


    }
}

