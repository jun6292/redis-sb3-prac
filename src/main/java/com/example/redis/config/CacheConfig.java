package com.example.redis.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {
    @Bean
    public RedisCacheManager redisCacheManager(
            RedisConnectionFactory redisConnectionFactory
    ) {
        // 설정 구성 진행
        // Redis를 이용해서 Spring cache를 사용할 때
        // Redis 관련 설정을 모아두는 클래스
        RedisCacheConfiguration configuration = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues() // null 캐싱을 하지 않는다.
                .entryTtl(Duration.ofSeconds(120))   // 기본 캐시 유지 시간 (Time to Live)
                .computePrefixWith(CacheKeyPrefix.simple()) // 캐시를 구분하는 접두사 설정
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.java())  // 캐시에 저장할 값을 어떻게 직렬화/역직렬화 할 것인지
                );

        RedisCacheConfiguration individual = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(20))
                .enableTimeToIdle()
                .computePrefixWith(CacheKeyPrefix.simple())
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json())
                );

        return RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(configuration)
                .withCacheConfiguration("storeCache", individual)
                .build();
    }
}
