package com.github.paicoding.forum.core.cache.local;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author wcd
 */
@Configuration
public class CaffeineConfig {
    public static final String CACHE_NAME = "typeId2NameCache";

    @Bean(name = "hotArticleCaffeineCache")
    public Cache<String, Object> HotArticleCaffeineCache(){
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(100)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .recordStats()
                .build();
    }
}
