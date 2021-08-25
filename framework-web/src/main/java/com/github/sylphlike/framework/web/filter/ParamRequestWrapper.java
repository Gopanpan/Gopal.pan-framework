package com.github.sylphlike.framework.web.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.sylphlike.framework.adapt.JsonConfig;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * <p>  time 17:56 2019/06/12  星期五 </p>
 * <p> email 15923508369@163.com     </p>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class ParamRequestWrapper extends HttpServletRequestWrapper {

    private final Logger logger = LoggerFactory.getLogger(ParamRequestWrapper.class);


    /**
     * Constructs a request object wrapping the given request.
     * @param request request
     * @throws IllegalArgumentException if the request is null
     */
    public ParamRequestWrapper(HttpServletRequest request) {
        super(request);
    }



    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if(StringUtils.isEmpty(value)){
            return value;
        }
        else{
            return cleanXSS(value);
        }

    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if(StringUtils.isEmpty(value)){
            return value;
        }
        else{
            return StringUtils.trimToEmpty( cleanXSS(value));
        }
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            int length = values.length;
            String[] escapeValues = new String[length];
            for (int i = 0; i < length; i++) {
                escapeValues[i] = StringUtils.trimToEmpty( cleanXSS(values[i]));
            }
            return escapeValues;
        }
        return super.getParameterValues(name);
    }



    @Override
    public ServletInputStream getInputStream() throws IOException {
        String contentType = super.getHeader("Content-Type");

        if(StringUtils.isEmpty(contentType))
            return super.getInputStream();

        if(contentType.equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
            String json = IOUtils.toString(super.getInputStream(), "utf-8");
            if (StringUtils.isEmpty(json)) {
                return super.getInputStream();
            }
            Map<String,Object> map = JsonConfig.mapper().readValue(json, new TypeReference<>() {});
            for (String s : map.keySet()) {
                Object o = map.get(s);
                if(o instanceof Collection || o instanceof Map){ continue;}

                map.put(s,cleanXSS(o.toString().trim()));
            }

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(JsonConfig.mapper().writeValueAsBytes(map));
            return new InputStream(byteArrayInputStream);

        }else if(contentType.equalsIgnoreCase(MediaType.APPLICATION_FORM_URLENCODED_VALUE)){
            return super.getInputStream();
        }else {
            return super.getInputStream();

        }

    }


    static class InputStream extends  ServletInputStream{
        private final ByteArrayInputStream bis;
        public InputStream(ByteArrayInputStream bis){
            this.bis=bis;
        }
        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {

        }
        @Override
        public int read(){
            return bis.read();
        }
    }

    private String cleanXSS(String value) {
        if(StringUtils.isEmpty(value)){
            return value;
        } else{

            String orginValue = value;
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            if(scriptPattern.matcher(value).matches()){ value = format(value); }

            // 会误伤百度富文本编辑器
            /*scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");*/


            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            if(scriptPattern.matcher(value).matches()){ value = format(value); }

            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            if(scriptPattern.matcher(value).matches()){ value = format(value); }

            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            if(scriptPattern.matcher(value).matches()){ value = format(value); }

            scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            if(scriptPattern.matcher(value).matches()){ value = format(value); }

            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            if(scriptPattern.matcher(value).matches()){ value = format(value); }

            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            if(scriptPattern.matcher(value).matches()){ value = format(value); }

            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            if(scriptPattern.matcher(value).matches()){ value = format(value); }

            return value;
        }
    }

    private String format(String value) {
        String result = value;

        if (StringUtils.isNotBlank(result)) {
            logger.info("【FW-web】xss拦截 原始值[{}]",result);
            boolean flag = false;
            if (result.contains("&")) {
                result = result.replaceAll("&", "");
            }
            if (result.contains("<")) {
                result = result.replaceAll("<", "");
            }
            if (result.contains(">")) {
                result = result.replaceAll(">", "");
            }
            if (result.contains("\"")) {
                result = result.replaceAll("\"", "");
            }
            if (result.contains("\'")) {
                result = result.replaceAll("\'", "");
            }

            logger.info("【FW-web】xss拦截,处理后的值[{}]",result);
        }

        return result;
    }

}
