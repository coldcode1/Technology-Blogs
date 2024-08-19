package com.github.paicoding.forum.core.cache.local;

import org.caffinitas.ohc.OHCache;
import org.caffinitas.ohc.OHCacheBuilder;

/**
 * @author wcd
 */
public class OHCacheConfig {

    // 存储
    public static final OHCache<String, String> USERNAME_CACHE = OHCacheBuilder.<String, String>newBuilder()
            .capacity(1000)
            .segmentCount(64)
            .keySerializer(new StringSerializer())
            .valueSerializer(new StringSerializer())
            .build();
}
