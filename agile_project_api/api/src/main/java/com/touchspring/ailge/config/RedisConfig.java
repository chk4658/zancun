package com.touchspring.ailge.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis多数据源配置
 */
@Configuration
public class RedisConfig {
//    /**
//     * 配置lettuce连接池
//     *
//     * @return
//     */
//    @Bean("redisPool")
//    @ConfigurationProperties(prefix = "spring.redis.lettuce.pool")
//    public GenericObjectPoolConfig redisPool() {
//        return new GenericObjectPoolConfig();
//    }
//    /**
//     * 配置lettuce连接池
//     *
//     * @return
//     */
//    @Bean("redisPool2")
//    @ConfigurationProperties(prefix = "spring.redis2.lettuce.pool")
//    public GenericObjectPoolConfig redisPool2() {
//        return new GenericObjectPoolConfig();
//    }
//
//    /**
//     * 配置第一个数据源的
//     *
//     * @return
//     */
//    @Bean("redisConfig1")
//    @ConfigurationProperties(prefix = "spring.redis")
//    public RedisStandaloneConfiguration redisConfig() {
//        return new RedisStandaloneConfiguration();
//    }
//
//    /**
//     * 配置第一个数据源的连接工厂
//     * 这里注意：需要添加@Primary 指定bean的名称，目的是为了创建两个不同名称的LettuceConnectionFactory
//     *
//     * @param config
//     * @param redisConfig
//     * @return
//     */
//    @Bean("factory")
//    @Primary
//    public LettuceConnectionFactory factory(@Qualifier("redisPool") GenericObjectPoolConfig config, @Qualifier("redisConfig1") RedisStandaloneConfiguration redisConfig) {
//        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
//        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
//    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        return getStringStringRedisTemplate(factory);
    }


    /**
     * 设置序列化方式 （这一步不是必须的）
     *
     * @param factory
     * @return
     */
    private RedisTemplate<String, String> getStringStringRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        RedisSerializer<String> redisSerializer = new StringRedisSerializer();//Long类型不可以会出现异常信息;
        template.setKeySerializer(redisSerializer);
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(redisSerializer);
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }

}


