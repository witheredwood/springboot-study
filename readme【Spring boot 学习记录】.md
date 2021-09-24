# Spring boot 学习记录(进行中)

javaweb：独立开发MVC网站

ssm：框架

war：tomcat运行4



从 Github 上克隆的springboot项目， 

File -> Project Structure -> Modules

点击右方的 `+` ，选择 `lmport Module` ，找到要变成模块的文件，导入就可以了。

**约定大于配置**



微服务架构：把每个功能单独出来，把独立出来的功能元素动态的组合。打破了 all in one 的架构。



## 创建项目



![image-20210810091643848](https://gitee.com/withered-wood/picture/raw/master/20210810091645.png)



创建项目时需要勾选 `JDBC API` 和 `MySQL Driver ` ，用来连接数据库。 或者在 `pom.xml` 中引入依赖

```xml
<!-- JDBC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<!-- MySQL -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```



## 自动装配原理

启动器：spring-boot-starter  

主程序

注解：

```java
@SpringBootConfiguration
	@Configuration
		@Component	组件


@EnableAutoConfiguration	自动配置
            
```

在了解自动装配的原理之后，有一点需要注意，自动配置类必须在一定条件下才能生效。只有 `@Conditonal` 指定的条件成立，才给容器中添加组件，配置里面的所有内容才能生效。

在配置文件 `application.yaml` 中能配置的东西，都有一个**规律**：

**配置文件 `application.yaml` 中能配置的东西，都有一个对应的 `xxxAutoConfiguration` 和 `xxxProperties` 文件。**

`xxxAutoConfiguration` ：自动配置类。有默认值。如果绑定配置文件，可以使用自定义配置。

`xxxProperties`：配置文件。

每一个 `xxxAutoConfiguration` 类都是容器中的一个组件，最后都加入到容器中，可以用他们来进行自动配置，每一个自动配置类都可以进行自动配置的功能。

根据当前不同的条件判断，决定找个配置类是否生效。一旦配置类生效，这个配置类就会给容器中添加各种组件。这些组件的属性从对应的 properties 类中获取，这些类中的每一个属性又是和配置文件绑定的。

### 自动装配的步骤(原理)

1. Springboot 启动会加载大量的自动配置类；

2. 然后看我们需要的功能是否在Springboot默认写好的自动配置类中；
3. 如果需要的功能在自动配置类中，再查看该自动配置类中配置了哪些组件。如果我们要用的组件在里面，就不需要手动配置了。
4. 给容器中的自动配置类添加组件的时候，会从properties类中获取某些属性。我们只需要在配置文件 `application.yaml` 中指定这些属性的值即可。

注：

- **`xxxAutoConfiguration`**：自动配置类。给容器中添加组件。

- **`xxxProperties`**：配置文件。封装配置文件中的相关属性。



## SpringApplication 类

做了4件事：

- 判断应用事普通的项目还是web项目
- 查找并加载所有可用的初始化器，设置到 `initializers` 属性中
- 找出所有的应用程序监听器，设置 `listeners` 属性中
- 判断并设置 `main` 方法的定义类，找到运行的主类。



run() 方法中有全局监听器，获取上下文，处理bean。



## Spring 配置

yaml 可以保存键值对、对象、数组，可以注入到配置类中。

properties 只能保存键值对。

```yaml
# 普通的key-value
name: zs

# 对象
student:
  name: zs
  age: 3

person: {name: zs,age: 3}

# 数组
pets:
  - dog
  - cat
  - pig

pet: [dog,cat,pig]
```

不同类型的数据赋值如下：

```yaml
person:
  name: zs
  age: 3
  ishappy: true
  birth: 2021/08/11
  maps: {k1: v1,k2: v2}
  lists: [a,b,c]
  dog:
    name: ls
    age: 3
```

application.properties 中的赋值方式如下：

```properties
student.name=zs
student.age=3
```



## JSR303 校验

```
@Validated  // 数据校验
```



## 资源加载路径

1.  `file:./config/`
2. `file:./`
3.  `classpath:/config/` 
4.  `classpath:/` ：默认。



Spring 多环境配置：

```yaml
server:
	port: 8080
spring:
	profiles:
		active: dev
---
server:
	port: 8081
spring:
	profiles: dev
	
---
server:
	port: 8082
spring:
	profiles: test
```



------

## Web开发

### 静态资源导入

可以在 `WebMvcAutoConfiuration.java` 中查看相应方案的源码。相关源码如下：

```java
public void addResourceHandlers(ResourceHandlerRegistry registry) {
	// 方案三：自定义路径。默认路径会失效。  
    if (!this.resourceProperties.isAddMappings()) {
        logger.debug("Default resource handling disabled");
        return;
    }
    // 方案一：使用webjars包。
    addResourceHandler(registry, "/webjars/**", "classpath:/META-INF/resources/webjars/");
    // 方案二：当前目录下的默认路径。
    addResourceHandler(registry, this.mvcProperties.getStaticPathPattern(), (registration) -> { registration.addResourceLocations(this.resourceProperties.getStaticLocations());
        if (this.servletContext != null) {
            ServletContextResource resource = new ServletContextResource(this.servletContext, SERVLET_LOCATION);
            registration.addResourceLocations(resource);
        }
    });
}
```

**方案一：导入 webjars 依赖。**以juqery为例。

webjars官网：https://www.webjars.org/

```xml
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>jquery</artifactId>
    <version>3.6.0</version>
</dependency>
```

**方案二：当前目录下的默认路径**

方案二所需的其他文件的源码如下。

`WebMvcPreoterties.java`相关源码（相关函数是按照查找顺序排放）：

```java
public String getStaticPathPattern() {
    return this.staticPathPattern;
}

// 当前目录下的所有东西都可以识别
private String staticPathPattern = "/**";
```

在 `WebMvcAutoConfiuration.java` 中查看关于 resource 的内容

```java
addResourceHandler(registry, this.mvcProperties.getStaticPathPattern(), (registration) -> {
		registration.addResourceLocations(this.resourceProperties.getStaticLocations());
    if (this.servletContext != null) {
        ServletContextResource resource = new ServletContextResource(this.servletContext, SERVLET_LOCATION);
        registration.addResourceLocations(resource);
    }
});
```

点击 `this.resourceProperties.getStaticLocations()` 中的 `getStaticLocations()` ，查看 `WebProperties.java` 文件。（相关函数是按照查找顺序排放）

```java
public String[] getStaticLocations() {
    return this.staticLocations;
}

private String[] staticLocations = CLASSPATH_RESOURCE_LOCATIONS;


// 这些路径下都可以作为静态资源的路径
private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { 
    "classpath:/META-INF/resources/", "classpath:/resources/", 
    "classpath:/static/", "classpath:/public/" 
};
```

优先级顺序依次是：`resources` > `static`(默认) > `public` 。

 `/META-INF/resources` 是方案一中的路径。这4中路径是springboot的默认路径。

### 首页

**相关源码**。`WebMvcPreoterties.java` 中设置首页的相关源码如下：

```java
// 首页的映射
@Bean
public WelcomePageHandlerMapping welcomePageHandlerMapping(ApplicationContext applicationContext, FormattingConversionService mvcConversionService, ResourceUrlProvider mvcResourceUrlProvider) {
    WelcomePageHandlerMapping welcomePageHandlerMapping = new WelcomePageHandlerMapping(
        new TemplateAvailabilityProviders(applicationContext), applicationContext, getWelcomePage(),
        this.mvcProperties.getStaticPathPattern());
    welcomePageHandlerMapping.setInterceptors(getInterceptors(mvcConversionService, mvcResourceUrlProvider));
    welcomePageHandlerMapping.setCorsConfigurations(getCorsConfigurations());
    return welcomePageHandlerMapping;
}

// 获取首页
private Resource getWelcomePage() {
    // 获取首页的位置
    for (String location : this.resourceProperties.getStaticLocations()) {
        Resource indexHtml = getIndexHtml(location);
        if (indexHtml != null) {
            return indexHtml;
        }
    }
    ServletContext servletContext = getServletContext();
    if (servletContext != null) {
        // 获取首页页面
        return getIndexHtml(new ServletContextResource(servletContext, SERVLET_LOCATION));
    }
    return null;
}

// 在相应路径中获取 index.html。设置index.html就可以设置首页内容
private Resource getIndexHtml(Resource location) {
    try {
        Resource resource = location.createRelative("index.html");
        if (resource.exists() && (resource.getURL() != null)) {
            return resource;
        }
    }
    catch (Exception ex) {
    }
    return null;
}
```

**方案**。设置首页的方案如下：

在静态资源的默认路径（这里不配置自己的路径，只使用默认路径存放静态资源）下创建一个 `index.html` 文件，在 `index.html` 中编写首页内容。重启访问 `http:localhost:8080/` 就可以访问首页。



### thymeleaf 模板引擎

在 `templates` 下的页面必须通过 `controller` 才能访问。

**相关依赖**

在 `pom.xml` 中导入相关的依赖：

```xml
<!-- thymeleaf 模板引擎 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

**源码**

在 `ThymeleafProperties.java` 中查看相关源码：

```java
// 相当于视图解析器。在 /templates下存放页面，页面文件以 .html 结尾
public static final String DEFAULT_PREFIX = "classpath:/templates/";
public static final String DEFAULT_SUFFIX = ".html";
```

**测试**

在 测试页面的路径： `resources/templates/test.html` 。启动之后，输入 `http://localhost:8080/test` 既可以看到测试页面上的内容。



### MVC 配置原理

路径：`arc/main/java/com/withered/config/MymvcConfig.java`。如果需要自定义的功能，编写这个组件，然后将组件交给spring，springboot就会自动装配。

扩展 springmvc 的代码如下：

```java
// 扩展spring mvc。
@Configuration  // 该注解将一个类变成配置类
public class MymvcConfig implements WebMvcConfigurer {

    // 将自定义视图解析器注入到spring中
    @Bean
    public ViewResolver myViewResolver() {
        return new myViewResolver();
    }

    // 自定义视图解析器。实现了视图解析器接口的类，可以看作是一个视图解析器
    public static class myViewResolver implements ViewResolver {
        @Override
        public View resolveViewName(String s, Locale locale) throws Exception {
            return null;
        }
    }
}
```

**源码**

`ContentNegotiationConfigurer` 中的相关源码：

```java
// 重写resolveViewName方法
public View resolveViewName(String viewName, Locale locale) throws Exception {
    RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
    Assert.state(attrs instanceof ServletRequestAttributes, "No current ServletRequestAttributes");
    List<MediaType> requestedMediaTypes = getMediaTypes(((ServletRequestAttributes) attrs).getRequest());
    if (requestedMediaTypes != null) {
        // 获取候选视图解析器
        List<View> candidateViews = getCandidateViews(viewName, locale, requestedMediaTypes);
        // 获取最好的视图
        View bestView = getBestView(candidateViews, requestedMediaTypes, attrs);
        if (bestView != null) {
            return bestView;
        }
    }

    String mediaTypeInfo = logger.isDebugEnabled() && requestedMediaTypes != null ?
        " given " + requestedMediaTypes.toString() : "";

    if (this.useNotAcceptableStatusCode) {
        if (logger.isDebugEnabled()) {
            logger.debug("Using 406 NOT_ACCEPTABLE" + mediaTypeInfo);
        }
        return NOT_ACCEPTABLE_VIEW;
    }
    else {
        logger.debug("View remains unresolved" + mediaTypeInfo);
        return null;
    }
}

```

### 格式化

**源码**

```java
// 日期格式化
public FormattingConversionService mvcConversionService() {
    Format format = this.mvcProperties.getFormat();
    WebConversionService conversionService = new WebConversionService(new DateTimeFormatters()
                                                                      .dateFormat(format.getDate()).timeFormat(format.getTime()).dateTimeFormat(format.getDateTime()));
    addFormatters(conversionService);
    return conversionService;
}
```

在 `WebMvcProperties.java` 中查看相关源码：

```java
public String getDateTime() {
    return this.dateTime;
}

/**
* Date-time format to use, for example `yyyy-MM-dd HH:mm:ss`.
*/
private String dateTime;

```

### springboot的默认配置

- springboot 在自动化配置很多组件的时候，先查看容器中是否有用户自己配置的。
- 如果有，就使用用户配置的；如果没有，就使用默认配置进行自动配置。
- 如果有些组件存在多个（例如视图解析器），就把用户和默认的结合起来。



## 数据库 Mysql（重点）

在项目中配置了MySql数据库，这里测试了使用JDBC连接和操作数据库。

**添加依赖**

在 `pom.xml` 中添加以下依赖

```xml
<!-- JDBC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<!-- MySQL -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

**连接数据库**

在 `application.yaml` 中配置数据库连接的属性，如下：

```yaml
spring:
  datasource:
    username: root
    password: 123456
    url: jdbc:mysql://localhost:3306/myweb?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
```

**测试连接**

```java
@Autowired
DataSource dataSource;

@Test
public void testConnection() throws SQLException {
    // 查看默认的数据源
    System.out.println(dataSource.getClass());  

    Connection connection = dataSource.getConnection();  // 获取数据库连接
    System.out.println(connection);

    connection.close();  // 关闭连接
}
```

`dataSource.getClass()` 输出结果为 `class com.zaxxer.hikari.HikariDataSource` ，默认使用 `hikari` 数据源 。

**增删改查操作**

以下示例是在没有实体类的情况下，实现数据库的操作。

```java
@RestController
public class JdbcController {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    // 查询数据库所有数据
    @GetMapping("/find")
    public List<Map<String, Object>> find() {
        String sql = "select * from myweb.user";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }

    @GetMapping("/add")
    public String add() {
        String sql = "insert into myweb.user(id, name, age, sex) values (6, '小明', 15, '男')";
        jdbcTemplate.update(sql);
        return "add ok";
    }
    
    // 只能修改数据库中已存在的数据
    @GetMapping("/update/{id}")  
    public String update(@PathVariable("id") int id) {
        String sql = "update myweb.user set name=?, age=?, sex=? where id = " + id;
        // 封装对象。将要设置的数据封装成对象传入
        Object[] objects = new Object[3];  // 要传递的参数
        objects[0] = "小红";
        objects[1] = 5;
        objects[2] = "女";
        jdbcTemplate.update(sql, objects);
        return "update ok";
    }
    
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        // 第一种方式
        String sql = "delete from myweb.user where id = " + id;
        jdbcTemplate.update(sql);
        // 第二种方式
        // String sql = "delete from myweb.user where id = ?";
        // jdbcTemplate.update(sql, id);
        return "delete ok";
    }
}
```

## 整合 Druid 数据源

查找依赖地址：[MavenRepositor](https://mvnrepository.com/) 。druid依赖地址：https://mvnrepository.com/artifact/com.alibaba/druid/1.2.6

### 导入依赖

**Step1 导入druid数据源依赖**

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.2.6</version>
</dependency>
<!-- 日志 log4j -->
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```

###  配置Druid数据源

**Step2 在 `application.yaml` 中设置数据源**

```yaml
spring:
  datasource:
    username: root
    password: 1234567
    url: jdbc:mysql://localhost:3306/myweb?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
```

到这，就可以使用Druid数据源了。重新测试数据库连接，可以看到使用的数据源是Druid数据源。

```
2021-09-23 08:55:46.481  INFO 1604 --- [nio-8080-exec-1] com.alibaba.druid.pool.DruidDataSource   : {dataSource-1} inited

2021-09-23 08:55:46,592 DEBUG [druid.sql.Statement] - {conn-10005, stmt-20000, rs-50000} query executed. 105.9726 millis. select * from myweb.user 
```

此外，还可以在 `application.yaml` 中配置 Druid 数据源的专有配置，还可以配置druid的监控功能。

**Step3 Druid专有配置（可选）**

```yaml
    # druid 专有配置
    initialsize: 5
    minIdle: 5
    maxActive: 20
    maxwait: 60000
    timeBetweenEvictionRunsMillis: 6000
    eminEvictableIdleTimeMi1lis: 300000
    validationQuery: SELECT 1 FROM DUAL
    testwhileIdle: true
    testOnBorrow: false
    testOnReturn: false
    poolPreparedStatements: true
```

**Step4 Druid监控功能（可选）**

```yaml
    # 配置监控统计拦截的filters：stat(监控统计)、log4j(日志记录)、wall(防御sql注入)
    filters: stat,wall,log4j
    maxPoolPreparedStatementPerConnectionSize: 20
    useGlobalDataSourceStat: true
    connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500
```

**Step5 设置日志**

druid 用到了 log4j ，所以需要配置日志文件 `log4j.properties`，以在控制台输出日志信息。

```properties
log4j.rootLogger=DEBUG, stdout
# 控制台输出
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d %p [%c] - %m %n
```

### druid配置类

**Step6 druid配置类（可选）**

springboot 内置了servlet 容器，所以没有 `web.xml` ，可以用替代类。

```java
package com.withered.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class DruidConfig {

    // 绑定yaml中的属性，需要以下两步。
    // 1. @Bean 
    // 2. @ConfigurationProperties(prefix = "spring.datasource")
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

    // 后台监控功能
    @Bean
    public ServletRegistrationBean statViewServlet() {
        // 默认监控页面
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        
        // 后台需要有人登录，配置账户密码
        Map<String, String> initParameters = new HashMap<>();
        // 增加配置
        initParameters.put("LoginUsername", "admin");  // 登录key，是固定的LoginUsername
        initParameters.put("LoginPassword", "123456");  // 登录key，是固定LoginPassword
        // 允许谁能访问
        initParameters.put("allow", "");  // ""-允许所有人访问；"localhost"-允许本机访问
        // 禁止谁访问
        initParameters.put("someone", "xxx.xxx.xxx.xxx");  // xxx.xxx.xxx.xxx - ip地址

        bean.setInitParameters(initParameters);  // 初始化参数
        return bean;
    }
    
     
    // 过滤器filter。过滤掉一些请求，不进行统计
    public FilterRegistrationBean web() {
        FilterRegistrationBean bean = new FilterRegistrationBean<>();
        bean.setFilter(new WebStatFilter());

        // 过滤哪些请求
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("exclusions", "*.js,*.css,/druid/*");  // 这些不进行统计
        
        bean.setInitParameters(initParameters);
        return bean;
    }

}
```

启动项目，在浏览器中输入 `http://localhost/druid` ，就可以看到 druid 的监控页面。

<img src="https://gitee.com/withered-wood/picture/raw/master/20210918155806.png" alt="image-20210918155805127" style="zoom:80%;" />

## 整合Mybatis框架

**Step1 导入整合包依赖**

查询依赖地址：https://mvnrepository.com/artifact/org.mybatis.spring.boot/mybatis-spring-boot-starter/2.2.0

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.2.0</version>
</dependency>
```

**Step2 编写 mapper 接口**

mapper接口的路径：`java/com/withered/mapper/UserMapper`

```java
@Mapper  // 表明这是一个mabatis 的 mapper 类
public interface UserMapper {
    List<User> get();
    User getUserById(int id);
}
```

**Step3 编写 sql 语句**

在 `resources` 下创建文件 `UserMapper.xml` ，路径为 `resources/mapper/UserMapper.xml` 。

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.withered.mapper.UserMapper">
    <select id="get" resultType="User">
		select * from myweb.user
	</select>
    <select id="getUserById" resultType="User">
		select * from myweb.user where id = #{id}
	</select>
</mapper>
```

**Step4 在  `application.yaml` 中设置 mybatis**

在 `application.yaml` 中添加整合 mybatis 的设置， `classpath:` 表示当前文件所在的文件夹。

```yaml
# 数据库
spring:
  datasource:
    username: root
    password: 1234567
    url: jdbc:mysql://localhost:3306/myweb?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    
# 整合mybatis
mybatis:
  type-aliases-package: com.withered.pojo
  mapper-locations: classpath:mapper/*.xml
```

**Step5 控制层测试**

编写控制层类 `UserController` ，路径：`java/com/withered/controller/UserController` 。

```java

import com.withered.mapper.UserMapper;
import com.withered.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class UserController {
    @Autowired
    private UserMapper userMapper;

    // 查询数据库所有数据
    @GetMapping("/get")
    public List<User> get() {
        System.out.println("get() ------------ ");
        List<User> list = userMapper.get();
        return list;
    }

    @GetMapping("/getById")
    public User getById() {
        System.out.println("getById() ------------ ");
        User user = userMapper.getUserById(2);
        return user;
    }
}
```

**Step6 启动**

启动 `MybatisApplication` 类，在地址栏输入 `http://localhost:8080/get` ，就可以在页面看到数据库中的数据的字符串。

## SpringSecurity环境搭建

安全：拦截器、过滤器。

安全框架: shiro、springsecurity（身份认证和授权）



- 功能权限
- 访问权限
- 菜单权限
- 拦截器、过滤器

[官方文档参考文档](https://spring.io/projects/spring-security)

学习 SpringSecurity 要关注的类和注解：

- WebSecurityConfigurerAdapter：自定义Security策略
- AuthenticationManagerBuilder:自定义认证策略
- @EnableWebSecurity：开启WebSecurity模式。@Enablexxxx开启某个功能

### 导入依赖

[在spring官网上引入相关依赖](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.build-systems.starters)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 基本的完整代码

这部分代码包括 用户认证和授权、注销、关闭csrf功能、开启记住我功能。

自定义类 `SecurityConfig` ， 继承 `WebSecurityConfigurerAdapter` 。

```java
package com.withered.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // 授权
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 首页所有人都可以访问，功能页只有对应有权限的人才能访问
        // 请求授权的规则
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/level1/**").hasRole("vip1")
                .antMatchers("/level2/**").hasRole("vip2")
                .antMatchers("/level3/**").hasRole("vip3");
        // 没有权限会到登录页面，需要开启登录的页面
        http.formLogin();
        // 防止网站工具。关闭csrf功能。
        http.csrf().disable();  // 登录失败可能存在的原因
        // 记住我。开启这个功能。默认保存两周
        http.rememberMe();
        // 注销
        http.logout();
    }

    // 认证。spring security5.0+ 增加了很多的加密方式。
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("root").password("123123").roles("vip1", "vip2", "vip3")
                .and()
                .withUser("test").password("123123").roles("vip1");
    }
}
```

下面分别介绍各部分的内容和代码，以及一些自定义的设置。

### 授权

重写方法 `configure(HttpSecurity http)` （注意是http的 `configure`）。这里的例子是设置不同页面的授权：首页所有人都可以访问，功能页只有对应有权限的人才能访问。

```java
protected void configure(HttpSecurity http) throws Exception {
    // 请求授权的规则
    http.authorizeRequests()
        .antMatchers("/").permitAll()  // 首页所有人可以访问
        .antMatchers("/level1/**").hasRole("vip1")  // level1下所有页面有vip1权限的人能访问
        .antMatchers("/level2/**").hasRole("vip2")  // level2下所有页面有vip2权限的人能访问
        .antMatchers("/level3/**").hasRole("vip3"); // level3下所有页面有vip3权限的人能访问
}
```

- `antMatchers()` ：要为哪个页面设置权限。
- `permitAll()` ：所有人都可以访问。
- `hasRole()` ：拥有某个角色/权限。

### 认证

重写方法 `configure(AuthenticationManagerBuilder auth)` （这里是 的 `configure`, 和上面授权的方法参数不同）。这里的例子是设置不同页面的授权：首页所有人都可以访问，功能页只有对应有权限的人才能访问。

```java
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser("root").password("123123").roles("vip1", "vip2", "vip3")
        .and()
        .withUser("test").password("123123").roles("vip1");
}
```

- `.withUser()` ：添加用户
- `.password()` ：该用户的登录密码。
- `.roles()` ：该用户拥有哪些角色/权限。
- `.and()` ：如果需要添加不同的用户，两个用户之后需要用 `.and()` 连接。

### 注销

在 `SecurityConfig` 中开启注销的功能。

```java
http.logout();
```

### 记住我

本质是一个cookie。在 `SecurityConfig` 中开启记住我的功能，会在浏览器的cookie中新增一个记录，默认保存2周。

```java
http.rememberMe();
```

### 关闭csrf功能

网站使用get请求，会不安全，处出于安全的考虑，网站会拒绝get请求。如果登录时，是用get请求，有可能会不允许登录。可以在  `SecurityConfig` 中关闭csrf。

```java
http.csrf().disable();  // 登录失败可能存在的原因
```

### 自定义登录页面

```java
http.formLogin().usernameParameter("name").passwordParameter("pwd").loginPage("/login");
```

- `.usernameParameter()` ：登录表单提交时的用户名的 `name` ， 默认是 `username`。
- `.passwordParameter()` ：登录表单提交时的密码的 `name` ， 默认是 `password`。
- `.loginPage()` ：自定义登录页面。



## Shiro

Shiro可以非常容易的开发出足够好的应用，其不仅可以用在JavaSE环境，也可以用在JavaEE环境。

Shiro可以完成，认证，授权，加密，会话管理，Web集成，缓存等。

[官网10分钟快速入门](http://shiro.apache.org/10-minute-tutorial.html)

[Github地址](https://github.com/apache/shiro.git)

Shiro 三个重要的部分： Subject、SecurityManager、Realm。

### 快速开始

将GitHub上的 `QuickStart` 例子复制到本地的项目中，并启动。

**Step1 创建maven项目**

创建一个maven项目，项目名为 `01-quickstart`。

**Step2 复制依赖** 

在 `pom.xml` 中导入以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.shiro</groupId>
        <artifactId>shiro-core</artifactId>
        <version>1.8.0</version>
    </dependency>
    <!-- configure logging -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>jcl-over-slf4j</artifactId>
        <scope>1.7.21</scope>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <scope>1.7.21</scope>
    </dependency>
    <dependency>
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        <version>1.2.17</version>
    </dependency>
</dependencies>

```

**Step3 复制 `log4j.properties`** 

`log4j.properties` 文件放在 `resources` 下。

```properties
log4j.rootLogger=INFO, stdout

log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d %p [%c] - %m %n

# General Apache libraries
log4j.logger.org.apache=WARN

# Spring
log4j.logger.org.springframework=WARN

# Default Shiro logging
log4j.logger.org.apache.shiro=INFO

# Disable verbose logging
log4j.logger.org.apache.shiro.util.ThreadContext=WARN
log4j.logger.org.apache.shiro.cache.ehcache.EhCache=WARN
```

**Step4 复制 `shiro.ini`** 

`shiro.ini` 文件放在 `resources` 下。

```ini
[users]
# user 'root' with password 'secret' and the 'admin' role
root = secret, admin
# user 'guest' with the password 'guest' and the 'guest' role
guest = guest, guest
# user 'presidentskroob' with password '12345' ("That's the same combination on
# my luggage!!!" ;)), and role 'president'
presidentskroob = 12345, president
# user 'darkhelmet' with password 'ludicrousspeed' and roles 'darklord' and 'schwartz'
darkhelmet = ludicrousspeed, darklord, schwartz
# user 'lonestarr' with password 'vespa' and roles 'goodguy' and 'schwartz'
lonestarr = vespa, goodguy, schwartz

# -----------------------------------------------------------------------------
# Roles with assigned permissions
#
# Each line conforms to the format defined in the
# org.apache.shiro.realm.text.TextConfigurationRealm#setRoleDefinitions JavaDoc
# -----------------------------------------------------------------------------
[roles]
# 'admin' role has all permissions, indicated by the wildcard '*'
admin = *
# The 'schwartz' role can do anything (*) with any lightsaber:
schwartz = lightsaber:*
# The 'goodguy' role is allowed to 'drive' (action) the winnebago (type) with
# license plate 'eagle5' (instance specific id)
goodguy = winnebago:drive:eagle5
```

**Step5 复制 `Quickstart.java`** 

```java
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
//import org.apache.shiro.ini.IniSecurityManagerFactory;
//import org.apache.shiro.lang.util.Factory;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Quickstart application showing how to use Shiro's API.
 *
 * @since 0.9 RC2
 */
public class Quickstart {

    private static final transient Logger log = LoggerFactory.getLogger(Quickstart.class);


    public static void main(String[] args) {
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        // Now that a simple Shiro environment is set up, let's see what you can do:

        // 获取当前用户
        Subject currentUser = SecurityUtils.getSubject();

        // 通过当前用户拿到session
        Session session = currentUser.getSession();
        session.setAttribute("someKey", "aValue");
        String value = (String) session.getAttribute("someKey");
        if (value.equals("aValue")) {
            log.info("Subject ==> session: Retrieved the correct value! [" + value + "]");
        }

        // let's login the current user so we can check against roles and permissions:
        // 判断当前用户是否被认证
        if (!currentUser.isAuthenticated()) {
            // token 令牌
            UsernamePasswordToken token = new UsernamePasswordToken("lonestarr", "vespa");
            token.setRememberMe(true);  // 设置记住我
            try {
                currentUser.login(token);  // 执行登录操作
            } catch (UnknownAccountException uae) {
                log.info("There is no user with username of " + token.getPrincipal());
            } catch (IncorrectCredentialsException ice) {
                log.info("Password for account " + token.getPrincipal() + " was incorrect!");
            } catch (LockedAccountException lae) {
                log.info("The account for username " + token.getPrincipal() + " is locked.  " +
                        "Please contact your administrator to unlock it.");
            }
            // ... catch more exceptions here (maybe custom ones specific to your application?
            catch (AuthenticationException ae) {
                //unexpected condition?  error?
            }
        }

        //say who they are:
        //print their identifying principal (in this case, a username):
        log.info("User [" + currentUser.getPrincipal() + "] logged in successfully.");

        //test a role:
        if (currentUser.hasRole("schwartz")) {
            log.info("May the Schwartz be with you!");
        } else {
            log.info("Hello, mere mortal.");
        }

        //test a typed permission (not instance-level) 粗粒度
        if (currentUser.isPermitted("lightsaber:wield")) {
            log.info("You may use a lightsaber ring.  Use it wisely.");
        } else {
            log.info("Sorry, lightsaber rings are for schwartz masters only.");
        }

        //a (very powerful) Instance Level permission:  细粒度
        if (currentUser.isPermitted("winnebago:drive:eagle5")) {
            log.info("You are permitted to 'drive' the winnebago with license plate (id) 'eagle5'.  " +
                    "Here are the keys - have fun!");
        } else {
            log.info("Sorry, you aren't allowed to drive the 'eagle5' winnebago!");
        }

        //all done - log out!  注销
        currentUser.logout();

        System.exit(0);  // 结束
    }
}
```

**Step6 启动 `Quickstart` 文件**

启动以后，可以在控制台看到以下信息：

```
2021-09-22 16:21:01,949 INFO [org.apache.shiro.session.mgt.AbstractValidatingSessionManager] - Enabling session validation scheduler... 
2021-09-22 16:21:02,279 INFO [Quickstart] - Retrieved the correct value! [aValue] 
2021-09-22 16:21:02,280 INFO [Quickstart] - User [lonestarr] logged in successfully. 
2021-09-22 16:21:02,280 INFO [Quickstart] - May the Schwartz be with you! 
2021-09-22 16:21:02,281 INFO [Quickstart] - You may use a lightsaber ring.  Use it wisely. 
2021-09-22 16:21:02,281 INFO [Quickstart] - You are permitted to 'drive' the winnebago with license plate (id) 'eagle5'.  Here are the keys - have fun!
```

### Subject 分析	

```
Subject currentUser = SecurityUtils.getSubject();  // 获取当前用户
Session session = currentUser.getSession();  // 获取session
currentUser.isAuthenticated()  // 用户是否被认证
currentUser.getPrincipal()  // 认证信息
currentUser.hasRole("schwartz")  // 用户角色
currentUser.isPermitted("lightsaber:wield")  // 用户权限
currentUser.logout();  // 注销
```

这些 Spring Security都有。

### Spring boot 整合shiro

**Step1 导入依赖**

```xml
<!-- shiro整合包 -->
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring-boot-web-starter</artifactId>
    <version>1.8.0</version>
</dependency>
```

在包 `java/com/withered/config` 下创建两个类， `ShiroConfig` 和 `UserRealm` 。

`ShiroConfig` ：自定义Shiro配置。 其中需要的 Realm 对象，要自定义。所以，`UserRealm` ，是自定义的Realm对象。

**Step2 自定义 Realm **

先创建好 `ShiroConfig` 所需要的自定义的 Realm 对象， 内容如下：

```java
package com.withered.config;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

// 自定义realm
public class UserRealm extends AuthorizingRealm {
    // 授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("执行了 ==> 授权 doGetAuthorizationInfo");
        return null;
    }
    // 认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行了 ==> 认证 doGetAuthenticationInfo");
        return null;
    }
}
```

**Step3 自定义配置**

`ShiroConfig` 总共有3个部分：

- 创建一个realm对象；
- 创建安全管理器 `DefaultWebSecurityManager` ；
- 创建过滤器配置 `ShiroFilterFactoryBean` ；

如何使用spring的bean对象？

bean对象需要通过参数的方式使用，同时使用 `@Qualifier()`  指定注入哪个对象。



完整代码如下：

```java
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    // Step3 创建ShiroFilterFactoryBean
    @Bean(name="shiroFilterFactoryBean")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(defaultWebSecurityManager);  // 设置安全管理器
        return bean;
    }

    // Step2 创建DefaultWebSecurityManager
    @Bean(name="securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);  // 关联 realm
        return securityManager;
    }

    // Step1 创建一个realm对象，需要自定义类
    @Bean
    public UserRealm userRealm() {
        return new UserRealm();
    }
}
```



### 登录拦截

可以在过滤器配置 `ShiroFilterFactoryBean` 中添加内置的过滤器：

- `anon` ：无需认证就可以访问
- `authc` ：必须认证了才能让问
- `user` ：必须拥有记住我功能才能用
- `perms`  ：拥有对某个资源的权限才能访问;
- `role` ：拥有某个角色权限才能访问

测试的效果是：从首页进入到两个不同的功能页面。实现的拦截效果是没有登录认证，从首页点击功能页面链接会跳转到登录页面。登录拦截设置如下：

```java
@Bean(name="shiroFilterFactoryBean")
public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
    ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
    bean.setSecurityManager(defaultWebSecurityManager);  // 设置安全管理器

    // 添加内置过滤器
    Map<String, String>  filterMap = new LinkedHashMap<>();
   	// 拦截
    filterMap.put("/user/*", "authc");
    bean.setFilterChainDefinitionMap(filterMap);
    bean.setLoginUrl("/toLogin");  // 设置登录请求,跳转到登录页面

    return bean;
}
```

启动服务器测试是否拦截成功，需要添加一些测试的页面进行测试。下面是测试用到的页面和请求。

**测试**

1）添加thymeleaf模板依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

2）测试页面

在 `resources/templates` 下创建以下要测试的页面：`index.html` ，`login.html` ，`user/add.html` ，`user/update.html`。

`index.html` ：首页。

```html
<body>
    <h1>首页</h1>
    <h2>hello，shiro</h2>
    <p th:text="${msg}"></p>
    <hr>
    <a th:href="@{/user/add}">add</a> | <a th:href="@{/user/update}">update</a>
</body>
```

`login.html` ：登录页面。

```html
<body>
    <h1>登录</h1>
    <form action="">
        <p>用户名：<input type="text" name="username"></p>
        <p>密  码：<input type="text" name="password"></p>
        <p><input type="submit" name="password"></p>
    </form>
</body>
```

`user/add.html` ：user 文件夹下的添加页面。

```html
<body>
    <h1>添加</h1>
</body>
```

`user/update.html` ：user 文件夹下的修改页面。

```html
<body>
    <h1>更新</h1>
</body>
```

3）controller 处理请求

```java
@Controller
public class RouterController {
    @RequestMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @RequestMapping("/user/add")
    public String add() {
        return  "user/add";
    }
    @RequestMapping("/user/update")
    public String update() {
        return  "user/update";
    }

    @RequestMapping("/toLogin")
    public String toLogin() {
        return  "login";
    }
}
```

### 用户认证

在 `RouterController` 中处理登录请求：

```java
@RequestMapping("/login")
public String login(String username, String password, Model model) {
    // 获取当前用户
    Subject subject = SecurityUtils.getSubject();
    // 封装登录数据
    UsernamePasswordToken token = new UsernamePasswordToken(username, password);

    try {
        subject.login(token);  // 执行登录方法，如果没有异常就说明oK 了
        return "index";
    } catch (UnknownAccountException e) { //用户名不存在
        model.addAttribute("msg", "用户名错误");
        return "login";
    } catch (IncorrectCredentialsException e) { // 密码不存在
        model.addAttribute("msg", "密码错误");
        return "login";
    }
}
```

在自定义Realm对象中，添加用户名和密码，完成认证的过程。

```java
protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
    System.out.println("执行了 ==> 认证 doGetAuthenticationInfo");

    // 临时用户名和密码。实际中从数据库中取数据
    String name = "root";
    String pw = "123456";

    UsernamePasswordToken userToken = (UsernamePasswordToken) token;
    if (!userToken.getUsername().equals(name)) {
        return null;  // 抛出异常
    }
    // 密码认证由shiro做
    return new SimpleAuthenticationInfo("", password, "");
}
```

### Shiro整合mybatis

**Step1 导入依赖**

```xml
<!-- shiro整合包 -->
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring-boot-web-starter</artifactId>
    <version>1.8.0</version>
</dependency>
<!-- JDBC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<!-- MySQL -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<!-- Mybatis -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.2.0</version>
</dependency>
```

**Step2 测试mybatis是否可用**

编写以下文件：

`java/com/withered/pojo/Account.java` 、`java/com/withered/mapper/AccountMapper.java` 、`java/com/withered/service/AccountService` 、`java/com/withered/service/AccountServiceImpl` 、`java/com/withered/controller/AccountController` 、`resources/mapper/AccountMapper.xml` 、

在测试文件 `java/com/withered/SpringbootShiroApplicationTests `中测试mybatis是否可用。

**Step3 加入到shiro**

```java
protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
    System.out.println("执行了 ==> 认证 doGetAuthenticationInfo");

    UsernamePasswordToken userToken = (UsernamePasswordToken) token;
    // 连接真实数据库
    Account account = accountService.getAccountByName(userToken.getUsername());
    if (account == null) {  // 没有这个账号
        return null;  // 抛出异常
    }
    // 密码认证由shiro做
    return new SimpleAuthenticationInfo(account, account.getPwd(), "");
}
```

### 授权

在 `ShiroConfig` 中添加以下授权内容

```java
@Bean(name = "shiroFilterFactoryBean")
public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
    ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
    bean.setSecurityManager(defaultWebSecurityManager);  // 设置安全管理器

    Map<String, String> filterMap = new LinkedHashMap<>();
    //授权。正常的情况下,没有授权会跳转到未授权页面
    filterMap.put("/user/add", "perms[user:add]");
    filterMap.put("/user/update", "perms[user:update]");
    // 拦截
    filterMap.put("/user/*", "authc");
    bean.setFilterChainDefinitionMap(filterMap);
    bean.setLoginUrl("/toLogin");  // 设置登录请求,跳转到登录页面
    // 未授权请求
    bean.setUnauthorizedUrl("/noauth");

    return bean;
}
```

在 `RouterController` 中处理未经授权的请求

```java
@RequestMapping("/noauth")
@ResponseBody
public String unauthorized() {
    return "未经授权无法访问此页面";
}
```

在 `UserRealm` 中给用户设置权限

```java
// 授权
protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    System.out.println("执行了 ==> 授权 doGetAuthorizationInfo");

    //        // 方案一：这里所有的用户都有权限。正常在数据库中获取不同用户的权限
    //        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    //        info.addStringPermission("user:add");
    //        info.addStringPermission("user:update");

    // 方案二：从数据库中获取权限
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    Subject subject = SecurityUtils.getSubject();  // 获取当前登录的这个对象
    Account currentAccount = (Account) subject.getPrincipal();  //拿到Account 对象
    // 设置当前用户的权限
    info.addStringPermission(currentAccount.getPerm());

    return info;
}

```

### shiro整合thymeleaf

**Step1 导入依赖**

```xml
<!-- shiro 和 thymeleaf 整合包 -->
<dependency>
    <groupId>com.github.theborakompanioni</groupId>
    <artifactId>thymeleaf-extras-shiro</artifactId>
    <version>2.0.0</version>
</dependency>
```

**Step2 ShiroDialect**

在 `ShiroConfig` 中整合 `ShiroDialect`

```java
@Bean
public ShiroDialect getShiroDialect () {
    return new ShiroDialect();
}
```

**Step3 修改首页**

在 `index.html` 中添加

```html
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml"
      xmlns:shiro="http://www.w3.org/1999/xhtml">
<body>
    <h1>首页</h1>
    <div th:if="${session.loginUser==null}">
        <a th:href="@{/toLogin}">登录</a>
    </div>
    <p th:text="${msg}"></p>
    <hr>
    <div shiro:hasPermission="user:add">
        <a th:href="@{/user/add}">add</a>
    </div>
    |
    <div shiro:hasPermission="user:update">
        <a th:href="@{/user/update}">update</a>
    </div>
</body>
```

**Step4 设置session**

在 `UserRealm` 中设置session

```java
// 认证
protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
    System.out.println("执行了 ==> 认证 doGetAuthenticationInfo")
    // 方案二：数据库取用户名和密码
    UsernamePasswordToken userToken = (UsernamePasswordToken) token;
    // 连接真实数据库
    Account account = accountService.getAccountByName(userToken.getUsername());
    if (account == null) {  // 没有这个账号
        return null;  // 抛出异常
    }
    // 放到session中
    Subject currentSubject = SecurityUtils.getSubject();
    Session session = currentSubject.getSession();
    session.setAttribute( "1oginUser", account);
    // 密码认证由shiro做
    return new SimpleAuthenticationInfo(account, account.getPwd(), "");
}
```

## Swagger

前后端分离：

- 前端测试后端接口
- 后端提供接口，需要实时更新最新的消息和改动

所以，swagger应运而生。

- 号称世界上最流行的Api框架;
- RestFul Api文档在线自动生成工具=>Api文档与API定义同步更新
- 直接运行，可以在线测试API接口;
- 支持多种语言（Java，Php....)

[官网](https://swagger.io/)

在项目中使用swagger需要springbox：

- swagger2
- UI

### Springboot集成swagger

hello工程测试

 创建一个springboot空项目，新建一个controller层的文件 `HelloController` ，内容如下：

```java
@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }
}
```

**Step1 导入依赖**

```xml
<!-- swagger -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.7.0</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

**Step2 配置swagger**

在 `config` 包下创建一个配置文件 `SwaggerConfig` ，内容如下：

```java
@Configuration
@EnableSwagger2  // 开启swagger2
public class SwaggerConfig {
}
```

swagger有默认配置，使用默认配置进行测试。

**Step3 测试运行**

启动服务器后，在浏览器地址栏输入 `http://localhost:8080/swagger-ui.html`

![image-20210924102705965](https://gitee.com/withered-wood/picture/raw/master/20210924135154.png)

### 配置Swagger

swagger的bean实例是 Docket。

```java
@Configuration
@EnableSwagger2  // 开启swagger2
public class SwaggerConfig {

    // 配置了swagger的docket的bean实例
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo());
    }

    // 配置swagger基本信息，也就是配置apiInfo
    private ApiInfo apiInfo() {
        // 作者信息
        Contact contact = new Contact("withered", "", "");
        return new ApiInfo(
               "withered的Api文档",
               "即使再小的帆也能远航",
               "v1.0",
               "urn:tos这是什么",
                contact,
               "Apache 2.0",
               "http://www.apache.org/licenses/LICENSE-2.0",
               new ArrayList()
       );

    }
}
```

启动服务器，查看效果

<img src="https://gitee.com/withered-wood/picture/raw/master/20210924135102.png" alt="image-20210924102412399" style="zoom:50%;" />

### 配置扫描接口和开关

在 Docket 实例中配置要扫描的接口。

**`RequestHandlerSelectors` ：配置要扫描的接口的方式。**

- `basePackage` ：指定只扫描哪个包下面的接口。一般用这种方式。
- `any()` ：扫描所有的接口
- `none()` ：不扫描所有的接口
- `withClassAnnotation(RestController.class)` ：扫描类上的注解，参数是一个注解的反射对象。
- `withMethodAnnotation(GetMapping.class)` ：扫描方法上的注解，参数是一个注解的反射对象。

**`.paths()` ：指定过滤什么路径。**

- `.ant()` ：只扫描带有什么路径的接口。例如 `/withered/**` ：只扫描路径中带有 `/withered` 的接口

```java
public Docket docket() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        // RequestHandlerSelectors：配置要扫描的接口的方式
        .apis(RequestHandlerSelectors.basePackage("com.withered.controller"))
        .paths(PathSelectors.ant("/withered/**"))  // 过滤什么路径
        .build();
}
```

**配置是否启动swagger**

通过设置 `.enable(true) `，来设置是否启动swagger。如果该值为 `false` ，则在浏览器中不能访问swagger。

```java
@Bean
public Docket docket() {
    return new Docket(DocumentationType.SWAGGER_2)
        .enable(true)  // 是否启用swagger
}
```

**思考**：swagger 在开发环境是启用，在发布时不启用

思路一：

判断当前运行环境是不是生产环境  `flag = false` ， 注入 `enable (flag)`

在 `SwaggerConfig` 中修改以下内容：

```java
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@Bean
public Docket docket(Environment environment) {
    // 设置要显示的Swagger环境
    Profiles profiles = Profiles.of ("dev", "test");
    // 获取项目当前环境
    boolean flag = environment.acceptsProfiles(profiles);  // 通过 acceptsProfiles 判断是否处在自己设定的环境当中
    System.out.println(flag);

    return new Docket(DocumentationType.SWAGGER_2)
        .enable(flag)  // 是否启用swagger
}
```

**配置不同的环境**

开发环境设置 `application-dev.yaml`

```yaml
server:
  port: 8081
```

生产环境设置 `application-pro.yaml`

```yaml
server:
  port: 8082
```

在 `application.yaml` 中设置启动哪个环境

```yaml
spring:
  profiles:
    active: dev
```

### 配置api文档分组

```java
@Bean
public Docket docket(Environment environment) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("withered")
}
```

配置多个分组：编写多个Docket实例

```java
@Bean
public Docket docket1(Environment environment) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("group 1");
}
@Bean
public Docket docket2(Environment environment) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("group 2");
}
@Bean
public Docket docket3(Environment environment) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("group 3");
}
```

### 配置实体类

如果在请求中使用了实体类，会在该请求中显示实体类Model。

增加一个请求 `/user` ，使用到了实体类 `User`（已创建）

```java
@PostMapping("/user")
public User user() {
    return new User();
}
```

在实体类 `User` 中配置以下的中文说明

```java
@ApiModel("用户实体类")
public class User {
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("密码")
    private String password;
}
```

启动服务器后，会在api文档中出现以下信息：

<img src="https://gitee.com/withered-wood/picture/raw/master/20210924141805.png" alt="image-20210924141804000" style="zoom:50%;" />

总结：

- 我们可以通过Swagger给一些比较难理解的属性或者接口，增加注释信息
- 接口文档实时更新
- 在线测试

注意：在正式发布时关闭swagger，也可以节省内存。

## 任务

异步任务、定时任务、邮件发送

### 异步任务

**Step1  编写service层**

service层文件 `AsyncService` 。通过在方法上添加 `@Async` 注解，告诉spring这个方法是一个异步的方法。

```java
@Service
public class AsyncService {
    @Async  // 告诉spring这是一个异步方法
    public void hello() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("数据正在处理。。。。。。。。");
    }
}
```

**Step2 开启异步注解**

在启动类 `TasksApplication` 中开启异步注解功能。

```java
@EnableAsync  // 开启异步注解功能
@SpringBootApplication
public class TasksApplication {
    public static void main(String[] args) {
        SpringApplication.run(TasksApplication.class, args);
    }
}
```

**Step3 编写controller层**

controller层文件 `AsyncController` 。由于 `asyncService.hello()` 是一个异步方法，所以会先给前端返回 `ok` ，等 ``asyncService.hello()`  方法执行完之后，会在后台输出这个函数的输出语句  `数据正在处理。。。。。。。。`

```java
@RestController
public class AsyncController {
    @Autowired
    AsyncService asyncService;

    @GetMapping("/hello")
    public String hello() {
        asyncService.hello();  // 停止3秒
        return "ok";
    }
}
```

### 邮件任务

**Step1 导入依赖**

```xml
<!-- 邮件 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

**Step2 邮箱设置**

使用qq邮箱发送邮件，需要先在qq邮箱中开启 `pop3/smtp` 

<img src="https://gitee.com/withered-wood/picture/raw/master/20210924154352.png" alt="image-20210924154345683" style="zoom:50%;" />

开启之后会有个授权码，程序中使用授权码作为密码登录。

在 `application.properties` 中添加以下设置：

```properties
spring.mail.username=123456789@qq.com
spring.mail.password=bxzdwahekxyybdbf
spring.mail.host=smtp.qq.com
#开启加密验证
spring.mail.properties.mail.smtp.ssl.enable=true
```

**Step3 测试**

在 `TasksApplicationTests` 中测试简单邮件发送和复杂邮件发送。

```java
@SpringBootTest
class TasksApplicationTests {
    @Autowired
    JavaMailSenderImpl mailSender;

    // 简单邮件测试
    @Test
    void simpleMail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("这是一封简单邮件的主题");  // 主题
        message.setText("这是测试简单邮件发送的正文内容哦~~~");  // 正文
        message.setFrom("123456789@qq.com");  // 发件人
        message.setTo("123456789@qq.com");  // 收件人
        mailSender.send(message);
    }
    // 复杂邮件测试
    @Test
    void complexMail() throws MessagingException {
        MimeMessage mimeMessage =  mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject("这是一封复杂邮件的主题");  // 主题
        helper.setText("<p style='color:red'>这是测试复杂邮件发送的正文内容哦~~~</p>", true);  // 正文
        // 附件
        helper.addAttachment("1.jpg", new File("D:\\Desktop\\图片\\头像1.jpg"));
        helper.addAttachment("2.jpg", new File("D:\\Desktop\\图片\\头像2.jpg"));
        helper.setFrom("123456789@qq.com");  // 发件人
        helper.setTo("123456789@qq.com");  // 收件人
        mailSender.send(mimeMessage);
    }
}
```

**封装为工具类**

```java
```

### 定时任务



## END
