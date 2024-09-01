truncate table article;
truncate table article_detail;

INSERT INTO `article` (`id`, `user_id`, `article_type`, `title`, `short_title`, `picture`, `summary`, `category_id`, `source`, `source_url`, `offical_stat`, `topping_stat`, `cream_stat`, `status`, `deleted`, `create_time`, `update_time`) VALUES (1, 1, 1, 'RabbitMQ实战--代码实现queue、exchange的申明', '', '', 'rabbiMQ实战: queue初始配置、Topic的exchange实现通知消息', 1, 2, '', 0, 0, 0, 1, 0, '2024-08-17 17:41:40', '2024-08-17 17:41:52');
INSERT INTO `article` (`id`, `user_id`, `article_type`, `title`, `short_title`, `picture`, `summary`, `category_id`, `source`, `source_url`, `offical_stat`, `topping_stat`, `cream_stat`, `status`, `deleted`, `create_time`, `update_time`) VALUES (2, 1, 1, '技术博客园-如何保证高可用性', '', '', '在技术博客园中，如何保证高可用性', 1, 2, '', 0, 0, 0, 1, 0, '2024-08-17 18:04:09', '2024-08-17 18:04:09');
INSERT INTO `article` (`id`, `user_id`, `article_type`, `title`, `short_title`, `picture`, `summary`, `category_id`, `source`, `source_url`, `offical_stat`, `topping_stat`, `cream_stat`, `status`, `deleted`, `create_time`, `update_time`) VALUES (3, 1, 1, 'MVC中的过滤器、拦截器详解', '', '', 'MVC中常用的过滤器、拦截器详细解析、实战。以及可能存在的问题。', 1, 2, '', 0, 0, 0, 1, 0, '2024-08-21 16:52:01', '2024-08-21 16:52:34');
INSERT INTO `article` (`id`, `user_id`, `article_type`, `title`, `short_title`, `picture`, `summary`, `category_id`, `source`, `source_url`, `offical_stat`, `topping_stat`, `cream_stat`, `status`, `deleted`, `create_time`, `update_time`) VALUES (4, 1, 1, '技术博客园开发plan', '', '', '记录 技术博客园功能及各模块的实现想法', 8, 2, '', 0, 0, 0, 1, 0, '2024-08-21 17:13:59', '2024-08-22 17:58:52');

INSERT INTO `article_detail` (`id`, `article_id`, `version`, `content`, `deleted`, `create_time`, `update_time`) VALUES (10, 1, 1, '关键点在于：部署在新机器上时，不可能专门去rabbitMQ管理中手动配置queue等。所以需要在代码中直接实现。\n\n1、在配置文件中写入基础的配置信息\n\n​	由于我工作原因，会涉及到在不同电脑上coding（笔记本没有安装rabbitMQ），为了方便使用配置文件中的switchFlag进行切换，使用`@ConditionalOnProperty(value = \"rabbitmq.switchFlag\")`便可以自由切换\n\n```\nrabbitmq:\n  host: 127.0.0.1\n  port: 5672\n  username: guest\n  passport: guest\n  virtualhost: /\n  switchFlag: true\n  pool_size: 10\n```\n\n2、代码实现连接、channel。以及申明exchange、queue。\n\n本来exchange都是direct的，分别是点赞和收藏文章。现在为了利用rabbitMQ实现一个邮件发送的功能，所需要新增一个queue。联想到使用消息队列要做的事情越来越多，所以进行了一个封装，将关于文章的点赞、收藏等通知信息封装在notify中，统一用topic的exchange发送。\n\n（被注释掉的是之前，对点赞、收藏分别申明的queue）\n\n```\npublic class RabbitMqAutoConfig implements ApplicationRunner {\n    @Resource\n    private RabbitmqService rabbitmqService;\n\n    @Autowired\n    private RabbitmqProperties rabbitmqProperties;\n\n    @Override\n    public void run(ApplicationArguments args) throws Exception {\n        String host = rabbitmqProperties.getHost();\n        Integer port = rabbitmqProperties.getPort();\n        String userName = rabbitmqProperties.getUsername();\n        String password = rabbitmqProperties.getPassport();\n        String virtualhost = rabbitmqProperties.getVirtualhost();\n        Integer poolSize = rabbitmqProperties.getPoolSize();\n\n        RabbitmqConnectionPool.initRabbitmqConnectionPool(host, port, userName, password, virtualhost, poolSize);\n        RabbitmqConnection connection = RabbitmqConnectionPool.getConnection();\n        Channel channel = connection.getConnection().createChannel();\n        // 声明exchange中的消息为可持久化，不自动删除\n        // channel.exchangeDeclare(CommonConstants.EXCHANGE_NAME_DIRECT, BuiltinExchangeType.DIRECT, true, false, null);\n\n        channel.exchangeDeclare(CommonConstants.EXCHANGE_NAME_TOPIC, BuiltinExchangeType.TOPIC, true, false, null);\n\n        // 声明点赞的消息队列\n//        channel.queueDeclare(CommonConstants.QUERE_NAME_PRAISE, true, false, false, null);\n//        //绑定队列到交换机\n//        channel.queueBind(CommonConstants.QUERE_NAME_PRAISE, CommonConstants.EXCHANGE_NAME_DIRECT, CommonConstants.QUERE_KEY_PRAISE);\n//\n//        // 声明收藏的消息队列\n//        channel.queueDeclare(CommonConstants.QUERE_NAME_COLLECT, true, false, false, null);\n//        //绑定队列到交换机\n//        channel.queueBind(CommonConstants.QUERE_NAME_COLLECT, CommonConstants.EXCHANGE_NAME_DIRECT, CommonConstants.QUERE_KEY_COLLECT);\n\n        channel.queueDeclare(CommonConstants.QUERE_NAME_NOTIFY, true, false, false, null);\n        channel.queueBind(CommonConstants.QUERE_NAME_NOTIFY, CommonConstants.EXCHANGE_NAME_TOPIC, CommonConstants.QUERE_KEY_NOTIFY);\n        channel.close();\n        RabbitmqConnectionPool.returnConnection(connection);\n        AsyncUtil.execute(() -> rabbitmqService.processConsumerMsg());\n    }\n}\n```\n\n3、通过注解`@RabbitListener(queues = CommonConstants.QUERE_NAME_NOTIFY)`实现一个监听器，\n\n```java\n@RabbitListener(queues = CommonConstants.QUERE_NAME_NOTIFY)\npublic void notifyMesSaveAndSubmit(String message,  @Header(\"amqp_receivedRoutingKey\") String routingKey) {\n    try {\n        log.info(\"Consumer msg: {}\", message);\n\n        if(routingKey.equals(CommonConstants.QUERE_KEY_COLLECT)){\n            notifyService.saveArticleNotify(JsonUtil.toObj(message, UserFootDO.class), NotifyTypeEnum.COLLECT);\n        }else if (routingKey.equals(CommonConstants.QUERE_KEY_PRAISE)){\n            notifyService.saveArticleNotify(JsonUtil.toObj(message, UserFootDO.class), NotifyTypeEnum.PRAISE);\n        }\n    } catch (Exception e) {\n        log.info(\"错误信息:{}\", e.getMessage());\n    }\n}\n```\n\n而这个ApplicationRunner的运行是在RabbitListen的bean之后的，导致一直在报bug。一个简单的解决办法是：先手动注释listen的注解，声明exchange和queue之后，再重新运行代码。\n\n然而部署在服务器上，很显然这个方法并不可行。\n\n4、利用bean的生命周期\n\n我们可以利用bean的生命周期来解决这个问题，将init方法放入到监听RabbitMsg的bean中，便可以解决这个问题。\n\n```java\n@PostConstruct\nprivate void init(){\n    // \n}\n```\n\n特别地，我们没有直接建立rabbitMQ的connection，而是在init的过程中，创建了一个pool（一个阻塞队列），方便连接的服用。\n\n```java\npublic static void initRabbitmqConnectionPool(String host, int port, String userName, String password,\n                                         String virtualhost,\n                                       Integer poolSize) throws InterruptedException, IOException {\n    pool = new LinkedBlockingQueue<>(poolSize);\n    for (int i = 0; i < poolSize; i++) {\n        pool.add(new RabbitmqConnection(host, port, userName, password, virtualhost));\n    }\n}\n```\n\n这是rabbitMQ实战的第一篇，也是最基础的实现，在这个过程中，也参考了很多大佬的博客和代码，可能没法一一贴上来，在此向广大开源的技术人致谢！后续还会继续更新文章~作为我自己学习过程中的记录。', 0, '2024-08-17 17:41:40', '2024-08-17 17:41:40');
INSERT INTO `article_detail` (`id`, `article_id`, `version`, `content`, `deleted`, `create_time`, `update_time`) VALUES (11, 2, 1, '\n这是一个说大不大说小也不小的问题，在实际中，同学们可能也经常被面试官问到，如何保证一个高可用性。往小了说，从我个人记录博客的一个应用而言，他仅仅是一个单体项目，然后并没有多高的qps（甚至可能也只有我点击进来看），所以即使请求全部打到mysql中，也没什么问题。\n\n但是实际上，这反应了一个人对技术的追求，去思考优雅的代码实现和可能会存在的问题，接下来我就结合技术博客园，谈论如何保证一个后端项目的高可用性。\n\n1、缓存的使用\n\n后端项目，避免不了接触数据，而大量的数据库都存放在mysql中，大家都知道缓存穿透、缓存击穿、缓存雪崩等问题。这些绝不是简单的名词，而是实际开发中必然会用到的情况。（犹记得实习的时候，用了一个**OHC堆外缓存**，上线后没有去研究缓存的**hit率**和缓存的**存储量**，被mentor指出来~~）\n\n（1）redis：主要用来存储文章。以及利用ZSet实现阅读活跃排行榜。\n\n（2）caffeine：作为本地缓存性能之王，我们用caffeine对网站的侧边栏（关于技术博客园、推荐资源、热门文章）进行了缓存，即使**热门文章**可能会更改，但是采用caffeine的写后缓存便可实现定时更新功能。（这是偷懒做法，实际上，更应该做的是，一个定时器，定时更改）\n\n\n\n2、消息队列\n\n消息队列的三大优点：异步、解耦、削峰\n\n试想，当我们新发表一篇文章，可能会被很多人瞬间看到(可能不太可能，哈哈哈)，并且文章写的很好，这会导致大量的点赞，很显然，这样的高流量不能直接打到数据库中，所以采用rabbitMQ进行削峰。\n\n\n\n3、业务可用性\n\n登陆两条线：\n\n	（1）、通过微信公众号的callback接口，简化了登陆。\n	（2）、基于邮箱的注册登录。\n\n\n\n3、服务的安全性、稳定性\n\n这一点更多的是服务的安全、稳定性。比如，不可能让人轻松地注册无限个账号（通过邮箱限制），无限制的发起邮箱验证码（设置时间限制）等等。\n\n\n\n', 0, '2024-08-17 18:04:09', '2024-08-17 18:04:09');
INSERT INTO `article_detail` (`id`, `article_id`, `version`, `content`, `deleted`, `create_time`, `update_time`) VALUES (14, 3, 1, '\n### 1、过滤器与拦截器的定义\n\n​	过滤器和拦截器 均体现了`AOP`的编程思想，都可以实现诸如日志记录、登录鉴权等功能，但二者的不同点也是比较多的：\n\n（1）过滤器`Filter` 的使用要依赖于`Tomcat`等容器，导致它只能在`web`程序中使用。而在一个mvc应用中，过滤器在Dispatchservlet之前，而拦截器在Dispatchservlet之后，具体的ControllerHandle之前。（这里讨论的是pre，post的顺序是反着的，具体的图示见第二节）。简单的图示如下：\n\n![img](https://technology-blogs.oss-cn-shanghai.aliyuncs.com/techonologyblogs/ceea6a675d321c2017141010a525888a.png)\n\n（2）**过滤器**和**拦截器**底层实现方式大不相同，**过滤器**是基于***函数回调（FilterChain）***的，**拦截器**则是基于Java的***反射机制（动态代理）***实现的。\n\n​	`FilterChain`是一个回调接口，`ApplicationFilterChain`是它的实现类， 这个实现类内部也有一个 `doFilter()` 方法就是回调方法。实际上，执行过滤器的doFilter就是通过`ApplicationFilterChain`去实现的，\n\n（3）**拦截器可以获取IOC容器中的各个bean，而过滤器不行，这点很重要，在拦截器里注入一个service，可以调用业务逻辑。**\n\n这是因为`spring`容器初始化`bean`对象的顺序是***listener-->filter-->servlet***。如果在一些特定场景中确实需要在过滤器中使用bean。\n\n### 2、过滤器、拦截器在一个web应用中具体的位置\n\n​	这里有一个细节，`postHandle`发生在`Controller`方法处理完之后，`DispatcherServlet`进行视图的渲染之前，也就是说在这个方法中你可以对`ModelAndView`进行操作。而`afterCompletion`是在视图渲染之后。\n\n![img](https://technology-blogs.oss-cn-shanghai.aliyuncs.com/techonologyblogs/565d30037c97b0278af24945fb9e15ea.png)\n\n### 3、过滤器与拦截器的应用方案\n\n#### 过滤器：\n\n##### 	（1）实现`Filter`接口\n\n```java\npublic class ReqRecordFilter implements Filter {\n\n    @Override\n    public void init(FilterConfig filterConfig) {\n    }\n\n    @Override\n    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {\n        // 逻辑处理\n        filterChain.doFilter(request, servletResponse);\n    }\n\n    @Override\n    public void destroy() {\n    }\n}\n```\n\n##### 	（2）\n\n##### 方式1.使用`@WebFilter`注解。\n\n##### *注意：这种方式可能顺序order不生效！*\n\n​	Filter实现类如下所示。此外这种方式需要在启动类上面加上`@ServletComponentScan`注解。\n\n```java\n@WebFilter(urlPatterns = \"/*\", filterName = \"reqRecordFilter\", asyncSupported = true)\npublic class ReqRecordFilter implements Filter {\n    //\n}\n```\n\n##### 方式2.注册实现的filter过滤器\n\n​	Filter实现类如下所示。\n\n```java\n@Component\npublic class ReqRecordFilter implements Filter {\n    //\n}\n```\n\n​	通过配置类，配置一个bean，如下所示：\n\n```\n@Bean\npublic FilterRegistrationBean<Filter> orderFilter(){\n    FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();\n    registrationBean.setFilter(new MyTestFilter());\n    registrationBean.addUrlPatterns(\"/*\");\n    registrationBean.setName(\"MyTestFilter\");\n    registrationBean.setOrder(1);\n    return registrationBean;\n}\n```\n\n#### 拦截器：\n\n##### 	（1）实现拦截器`AsyncHandlerInterceptor`接口\n\n```java\n@Component\n@Slf4j\npublic class MyTestInterceptor implements AsyncHandlerInterceptor {\n    @Override\n    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {\n        log.info(\"MyTestInterceptor preHandle\");\n        return true;\n    }\n\n    @Override\n    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {\n        log.info(\"MyTestInterceptor postHandle\");\n    }\n\n    @Override\n    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {\n        log.info(\"MyTestInterceptor afterCompletion\");\n    }\n}\n```\n\n##### 	（2）实现`WebMvcConfigurer`接口中的`addInterceptors`方法，向其中注册拦截器\n\n```java\npublic class QuickForumApplication implements WebMvcConfigurer{\n\n    @Resource\n    private MyTestInterceptor myTestInterceptor; // MyTestInterceptor\n\n    @Override\n    public void addInterceptors(InterceptorRegistry registry) {\n        registry.addInterceptor(myTestInterceptor).addPathPatterns(\"/**\");\n    }\n```\n\n### 4、可能出现的问题及解决方案：\n\n##### 问题1\n\n​	当我们在前端通过ajax调用后端rest接口请求json数据，会导致重复发送request到后端。此时会导致访问一个页面会多次被过滤器拦截器所过滤拦截，而这会导致记录页面的PV数据出问题。\n\n这种问题有一个简单的解决方法，在请求中判断是不是ajax请求。', 0, '2024-08-21 16:52:01', '2024-08-21 16:52:01');
INSERT INTO `article_detail` (`id`, `article_id`, `version`, `content`, `deleted`, `create_time`, `update_time`) VALUES (16, 4, 1, '### 一、用户模块\n\n#### 注册功能\n\n注册：通过RabbitMQ+邮件发送。实验用户的注册。\n\n通过setNx防止用户注册的并发问题。\n\n#### 登录功能\n\n（1）JWT+redis。\n\n（2）微信公众号回调\n\n\n\n### 二、侧边栏以及站点模块\n\n侧边栏通知：本地缓存caffeine存储。\n\n站点PV/UV等：在过滤器实现，通过Redis中的Hash实现，注意。过滤掉AJAX的请求防止防止多加PV。\n\n\n\n\n\n### 三、文章模块\n\n1、阅读\n\nredis中的zset存储热门项目的id。文章存储于OHC缓存中。通过分布式锁防止并发问题。\n\n\n\n\n\n### 四、热门项目\n\n\n\n### 五、算法OJ实现\n\ntodo//\n\n\n\n### 六、积分商城模块\n\ntodo//', 0, '2024-08-21 17:13:59', '2024-08-21 17:13:59');