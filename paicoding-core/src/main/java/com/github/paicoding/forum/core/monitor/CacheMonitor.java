package com.github.paicoding.forum.core.monitor;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CacheMonitor {

    @Autowired
    private Cache<String,Object> hotArticleCaffeineCache;

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    private void monitorCache() {
        CacheStats stats = hotArticleCaffeineCache.stats();
        log.info("缓存命中率：{}", stats.hitRate());
        log.info("缓存数据量：{}", hotArticleCaffeineCache.estimatedSize());
    }
}
