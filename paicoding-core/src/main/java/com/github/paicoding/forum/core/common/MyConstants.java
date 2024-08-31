package com.github.paicoding.forum.core.common;

public class MyConstants {
    // 文章内容缓存
    public static final String ARTICLE_CONTENT_PROFILE = "Blogs_ArticleContent_";

    // 网站首页，通过zset存储文章的更新时间，从而形成排序
    public static final String ARTICLE_LIST_PROFILE = "Blogs_ListByCategoryId_";

    // 网站首页，文章缩略信息的缓存
    public static final String OHC_ARTICLE_INFO_PROFILE = "Blogs_ArticleInfo_";
}
