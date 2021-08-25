package com.github.sylphlike.framework.adapt;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * 全局Jackson格式化配置，一旦定义后全局不可修改,参数使用个性化@JsonFormat 配置无效
 *
 *   LocalDateTime 序列化配置全局配置 全局格式为 yyyy-MM-dd HH:mm:ss
 *   LocalDate     序列化配置全局配置 全局格式为 yyyy-MM-dd
 *   LocalTime     序列化配置全局配置 全局格式为 HH:mm:ss
 *   Long类型转换成 String类型
 * <p>  time 17:56 2019/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
public class JsonConfig {

    // 默认时间类型序列化，反序列化格式
    private static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE = "yyyy-MM-dd";
    private static final String TIME = "HH:mm:ss";

    // 时间正则
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile("^\\d{4}(\\-\\d{1,2}){2}\\s+\\d{1,2}(\\:\\d{1,2}){2}$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}(\\-\\d{1,2}){2}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^\\d{1,2}(\\:\\d{1,2}){2}$");


    private JsonConfig() {}
    private static final ObjectMapper objectMapper = objectMapper();
    public static ObjectMapper mapper(){ return objectMapper; }


    private static ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());

        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());

        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer());
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer());

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        objectMapper.registerModules(javaTimeModule,simpleModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }



    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)throws IOException {
            gen.writeString(value.format(DateTimeFormatter.ofPattern(DATE_TIME)));
        }
    }

    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext deserializationContext)throws IOException {
            String valueAsString = p.getValueAsString();
            if(DATE_TIME_PATTERN.matcher(valueAsString).matches())
                return LocalDateTime.parse(valueAsString, DateTimeFormatter.ofPattern(DATE_TIME));

             return LocalDateTime.parse(p.getValueAsString());
        }
    }


    public static class LocalDateSerializer extends JsonSerializer<LocalDate> {
        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.format(DateTimeFormatter.ofPattern(DATE)));
        }
    }


    public static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext deserializationContext)throws IOException {
            String valueAsString = p.getValueAsString();
            if(DATE_PATTERN.matcher(valueAsString).matches())
                return LocalDate.parse(valueAsString, DateTimeFormatter.ofPattern(DATE));
            return LocalDate.parse(valueAsString);
        }
    }



    public static class LocalTimeSerializer extends JsonSerializer<LocalTime>{
        @Override
        public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.format(DateTimeFormatter.ofPattern(TIME)));
        }

    }


    public static class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {
        @Override
        public LocalTime deserialize(JsonParser p, DeserializationContext deserializationContext)throws IOException {
            String valueAsString = p.getValueAsString();
            if(TIME_PATTERN.matcher(valueAsString).matches())
                return LocalTime.parse(p.getValueAsString(), DateTimeFormatter.ofPattern(TIME));
            return LocalTime.parse(valueAsString);
        }
    }
}
