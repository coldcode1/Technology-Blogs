package com.github.paicoding.forum.core.cache.local;

import com.github.paicoding.forum.api.model.vo.article.dto.ArticleDTO;
import com.github.paicoding.forum.core.cache.cacheserializer.FurySerializer;
import com.github.paicoding.forum.core.cache.cacheserializer.StringSerializer;
import org.caffinitas.ohc.OHCache;
import org.caffinitas.ohc.OHCacheBuilder;

/**
 * @author wcd
 */
public class OHCacheConfig {

    // 存储
    public static final OHCache<String, ArticleDTO> ARTICLE_INFO = OHCacheBuilder.<String, ArticleDTO>newBuilder()
            .capacity(1000)
            .segmentCount(64)
            .timeouts(false)
            .keySerializer(new StringSerializer())
            .valueSerializer(new FurySerializer<ArticleDTO>(ArticleDTO.class))
            .build();
}
