package io.github.planegg.allone.starter.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.planegg.allone.starter.service.cache.RedisServiceImpl;
import io.github.planegg.allone.starter.service.lock.IDistributedLockService;
import io.github.planegg.allone.starter.service.lock.RedissonServiceImpl;
import io.github.planegg.allone.starter.service.cache.ICacheService;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {
    @NacosValue("${spring.redis.host}")
    private String host;
    @NacosValue("${spring.redis.port}")
    private String port;
    @NacosValue("${spring.redis.password}")
    private String password;
    @NacosValue("${spring.redis.database}")
    private String database;

    @Bean
    @SuppressWarnings("all")
    public ICacheService cacheServiceFactory(RedisConnectionFactory factory){

        // 为方便统一管理直接使用 <String, Object>
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        // Json序列化配置
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        // String 的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        ICacheService redisService = new RedisServiceImpl(template);
        return redisService;
    }


    @Bean(destroyMethod="shutdown")
    public IDistributedLockService getRedissonClient() {
        Config config = new Config();
        //配置地址、数据库
        config.useSingleServer()
                .setAddress("redis://"+host+":"+port)
//                .setPassword(password)
                .setDatabase(Integer.valueOf(database));
        RedissonClient redissonClient = Redisson.create(config);
        IDistributedLockService distributedLockService = new RedissonServiceImpl(redissonClient);
        return distributedLockService;
    }

}
