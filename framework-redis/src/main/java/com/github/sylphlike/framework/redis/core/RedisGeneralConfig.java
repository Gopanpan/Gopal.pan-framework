package com.github.sylphlike.framework.redis.core;

import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;


/**
 * <p>  time 17:56 2021/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */


@Configuration
public class RedisGeneralConfig  {


    @Bean
    public RedisTemplate<Serializable, Object> redisTemplate(RedissonConnectionFactory redissonConnectionFactory) {
        return initRedisTemplate(redissonConnectionFactory);
    }


    /**
     * 配置Redis序列化，value使用 Jackson 可以使用 StringRedisSerializer
     * @param redissonConnectionFactory redissonConnectionFactory
     * @return RedisTemplate<Serializable,Object>
     * @author  Gopal.pan
     */
    protected RedisTemplate<Serializable, Object> initRedisTemplate(RedissonConnectionFactory redissonConnectionFactory){
        Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(RedisJsonConfig.getMapper());
        RedisSerializer<?> stringSerializer = new StringRedisSerializer();
        RedisTemplate<Serializable, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redissonConnectionFactory);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);

        return template;
    }



}
