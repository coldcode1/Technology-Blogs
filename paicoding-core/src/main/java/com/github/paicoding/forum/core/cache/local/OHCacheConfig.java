package com.github.paicoding.forum.core.cache.local;

import com.github.paicoding.forum.api.model.vo.article.dto.ArticleDTO;
import com.github.paicoding.forum.api.model.vo.article.dto.CategoryDTO;
import com.github.paicoding.forum.api.model.vo.article.dto.SimpleColumnDTO;
import com.github.paicoding.forum.api.model.vo.article.dto.TagDTO;
import com.github.paicoding.forum.api.model.vo.user.dto.ArticleFootCountDTO;
import com.github.paicoding.forum.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.github.paicoding.forum.core.cache.cacheserializer.FurySerializer;
import com.github.paicoding.forum.core.cache.cacheserializer.StringSerializer;
import org.caffinitas.ohc.OHCache;
import org.caffinitas.ohc.OHCacheBuilder;


import java.util.List;

/**
 * @author wcd
 */
public class OHCacheConfig {

    // 存储
    public static final List<Class> classList = List.of(new Class[]{ArticleDTO.class, TagDTO.class, CategoryDTO.class,ArticleFootCountDTO.class, SimpleUserInfoDTO.class});
    public static final OHCache<String, ArticleDTO> ARTICLE_INFO = OHCacheBuilder.<String, ArticleDTO>newBuilder()
            .segmentCount(64)
            .timeouts(false)
            .keySerializer(new StringSerializer())
            .valueSerializer(new FurySerializer<ArticleDTO>(classList))
            .build();

    public static final OHCache<String, SimpleColumnDTO> CE_SHI = OHCacheBuilder.<String, SimpleColumnDTO>newBuilder()
            .segmentCount(64)
            .timeouts(false)
            .keySerializer(new StringSerializer())
            .valueSerializer(new FurySerializer<SimpleColumnDTO>(SimpleColumnDTO.class))
            .build();

    public static final OHCache<String, String> TTT = OHCacheBuilder.<String, String>newBuilder()
            .segmentCount(64)
            .keySerializer(new StringSerializer())
            .valueSerializer(new StringSerializer())
            .build();
}
