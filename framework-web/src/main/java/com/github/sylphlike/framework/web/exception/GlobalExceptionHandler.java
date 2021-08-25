package com.github.sylphlike.framework.web.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sylphlike.framework.norm.CharsetUtil;
import com.github.sylphlike.framework.norm.Response;
import com.github.sylphlike.framework.web.FReply;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * 全局异常处理
 * <p>业务中可以不捕获异常，交由该类通用处理，性能相对较差 业务本身捕获异常 </p>
 * <p>  time 17:56 2018/02/29  星期五 </p>
 * <p> email 15923508369@163.com     </p>
 * @author Gopal.pan
 * @version 1.0.0
 */
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ObjectMapper mapper;

    public GlobalExceptionHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    /** 400 - Bad Request  参数解析失败 */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Response<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        Response<Void> response = new Response<>(FReply.FW_CLIENT_PARAM_PARSE_ERROR);
        response.setMsg(response.getMsg());
        response.setSubMsg(ex.getMessage());
        writeLog(ex, request, response);
        return response;
    }

    /** 405 - Method Not Allowed  不支持当前请求方法 */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        Response<Void> response = new Response<>(FReply.FW_CLIENT_NO_SUPPORT_METHOD);
        response.setMsg(response.getMsg());
        response.setSubMsg(ex.getMessage());
        writeLog(ex, request, response);
        return response;
    }

    /** 415 - Unsupported Media Type 不支持当前媒体类型  */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Response<Void> handleHttpMediaTypeNotSupportedException(Exception ex, HttpServletRequest request) {
        Response<Void> response = new Response<>(FReply.FW_CLIENT_NO_SUPPORT_TYPE);
        response.setMsg(response.getMsg());
        response.setSubMsg(ex.getMessage());
        writeLog(ex, request, response);
        return response;
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Response<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex,
                                                                HttpServletRequest request) {
        Response<Void> response = new Response<>(FReply.FW_CLIENT_PARAM_PARSE_ERROR);
        response.setMsg(response.getMsg());
        response.setSubMsg(ex.getMessage());
        writeLog(ex, request, response);
        return response;
    }

    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public Response<Void> handleUnsatisfiedServletRequestParameterException(UnsatisfiedServletRequestParameterException ex, HttpServletRequest request) {

        Response<Void> response = new Response<>(FReply.FW_CLIENT_PARAM_ERROR);
        response.setMsg(response.getMsg());
        response.setSubMsg(ex.getMessage());
        writeLog(ex, request, response);
        return response;
    }




    @ExceptionHandler(BindException.class)
    public Response<Void> handleBindException(BindException ex, HttpServletRequest request) {
        Response<Void> response = new Response<>(FReply.FW_CLIENT_PARAM_BIND);
        response.setMsg(response.getMsg());
        response.setSubMsg(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage());
        writeLog(ex, request, response);
        return response;
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Response<Void> response = new Response<>(FReply.FW_CLIENT_PARAM_INVALID);
        response.setMsg(response.getMsg());
        response.setSubMsg(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage());
        writeLog(ex, request, response);
        return response;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Response<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        Response<Void> response = new Response<>(FReply.FW_CLIENT_PARAM_MISMATCH);
        response.setMsg(response.getMsg());
        response.setSubMsg(Objects.requireNonNull(ex.getMessage(),""));
        writeLog(ex, request, response);
        return response;
    }



    @ExceptionHandler(ServiceException.class)
    public Response<Void> handleServiceException(ServiceException ex, HttpServletRequest request) {
        Response<Void> rs = new Response<>();
        rs.setCode(ex.getCode());
        rs.setMsg(ex.getMsg());
        rs.setSubMsg(ex.getSubMsg());
        writeLog(ex, request,rs);
        return rs;
    }

    @ExceptionHandler(RuntimeException.class)
    public Response<Void> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        Throwable cause = ex.getCause();
        Response<Void> response;
        if(cause instanceof SQLException){
            response = new Response<>(FReply.FW_DATE_UNKNOWN_ERROR, cause.getMessage());
        }else {
            response = new Response<>(FReply.FW_UNKNOWN_ERROR);
        }

        writeLog(ex, request, response);
        return response;
    }


    @ExceptionHandler(Exception.class)
    public Response<Void> handleException(Exception ex, HttpServletRequest request) {

        Response<Void> response = new Response<>(FReply.FW_UNKNOWN_ERROR);
        writeLog(ex, request, response);
        return response;
    }

    private void writeLog(Exception ex, HttpServletRequest request, Response<Void> rs) {

        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String,String> map = new HashMap<>();
        Set<String> keySet = parameterMap.keySet();
        for (String key:keySet) {
            String[] strings = parameterMap.get(key);
            StringBuilder valueSBuilder = new StringBuilder();
            for (String single: strings) {
                valueSBuilder.append(single);
            }
            map.put(key,valueSBuilder.toString());
        }

        String params = null;
        try {
            params = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
           LOGGER.error("【FW-web】全局异常,参数格式化异常",e);
        }

        LOGGER.error(StringUtils.join("【FW-web】全局异常",CharsetUtil.CHAR_ENGLISH_SPACE,
                "接口地址",CharsetUtil.CHAR_ENGLISH_BRACKET_START,request.getRequestURI(),CharsetUtil.CHAR_ENGLISH_BRACKET_END,CharsetUtil.STRING_ENGLISH_SEMICOLON_SPACE,
                "客户端IP",CharsetUtil.CHAR_ENGLISH_BRACKET_START,request.getRemoteAddr(),CharsetUtil.CHAR_ENGLISH_BRACKET_END,CharsetUtil.STRING_ENGLISH_SEMICOLON_SPACE,
                "请求参数",CharsetUtil.CHAR_ENGLISH_BRACKET_START,params,CharsetUtil.CHAR_ENGLISH_BRACKET_END,CharsetUtil.STRING_ENGLISH_SEMICOLON_SPACE,
                "请求方式",CharsetUtil.CHAR_ENGLISH_BRACKET_START,request.getMethod(),CharsetUtil.CHAR_ENGLISH_BRACKET_END,CharsetUtil.STRING_ENGLISH_SEMICOLON_SPACE,
                "接口响应",CharsetUtil.CHAR_ENGLISH_BRACKET_START,rs.toString(),CharsetUtil.CHAR_ENGLISH_BRACKET_END),CharsetUtil.STRING_ENGLISH_SEMICOLON_SPACE,
                ex);


    }
}
