package com.github.paicoding.forum.core.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用常量
 *
 * @author Louzai
 * @date 2022/11/1
 */
public class CommonConstants {

    /**
     * 消息队列
     */
    public final static String EXCHANGE_NAME_DIRECT = "direct.exchange";
    public final static String QUERE_KEY_PRAISE = "praise";
    public final static String QUERE_NAME_PRAISE = "quere.praise";

    public final static String QUERE_KEY_COLLECT = "collect";
    public final static String QUERE_NAME_COLLECT = "quere.collect";

    /**
     * 分类类型
     */
    public static final String CATEGORY_ALL             = "全部";
    public static final String CATEGORY_BACK_EMD        = "后端";
    public static final String CATEGORY_FORNT_END       = "前端";
    public static final String CATEGORY_ANDROID         = "Android";
    public static final String CATEGORY_IOS             = "IOS";
    public static final String CATEGORY_BIG_DATA        = "大数据";
    public static final String CATEGORY_INTELLIGENCE    = "人工智能";
    public static final String CATEGORY_CODE_LIFE       = "代码人生";
    public static final String CATEGORY_TOOL            = "开发工具";
    public static final String CATEGORY_READ            = "阅读";

    /**
     * 首页图片
     */
    public static final Map<String, List<String>> HOMEPAGE_TOP_PIC_MAP = new HashMap<String, List<String>>() {
        {
            put(CATEGORY_ALL, new ArrayList<String>() {
                {
                    add("https://technology-blogs.oss-cn-shanghai.aliyuncs.com/blogs/images/1.jpg");
                    add("https://technology-blogs.oss-cn-shanghai.aliyuncs.com/blogs/images/2.jpg");
                    add("https://technology-blogs.oss-cn-shanghai.aliyuncs.com/blogs/images/3.jpg");
                    add("https://technology-blogs.oss-cn-shanghai.aliyuncs.com/blogs/images/4.jpg");
                }
            });
            put(CATEGORY_BACK_EMD, new ArrayList<String>() {
                {
                    add("https://technology-blogs.oss-cn-shanghai.aliyuncs.com/blogs/images/5.gif");
                    add("https://technology-blogs.oss-cn-shanghai.aliyuncs.com/blogs/images/6.jpg");
                    add("https://technology-blogs.oss-cn-shanghai.aliyuncs.com/blogs/images/7.jpg");
                    add("https://technology-blogs.oss-cn-shanghai.aliyuncs.com/blogs/images/8.jpg");
                }
            });
        }
    };
}
