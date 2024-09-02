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

    // 实现点赞、收藏的通知队列
    public final static String EXCHANGE_NOTIFY_TOPIC = "topic.notify";
    public final static String QUERE_KEY_PRAISE = "notify.praise";
    public final static String QUERE_KEY_COLLECT = "notify.collect";
    public final static String QUERE_KEY_NOTIFY = "notify.*";
    public final static String QUERE_NAME_NOTIFY = "quere.notify";



    // 实现邮件注册的通知队列
    public final static String EXCHANGE_EMAIL_DIRECT = "direct.email";
    public final static String QUERE_KEY_EMAIL = "email";
    public final static String QUERE_NAME_EMAIL = "quere.email";

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
