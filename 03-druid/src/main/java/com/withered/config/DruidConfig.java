package com.withered.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

// druid配置类，可以自定义一些配置
@Configuration
public class DruidConfig {

    // 绑定yaml中的属性。1. @Bean 2. @ConfigurationProperties(prefix = "spring.datasource")
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

    // 后台监控功能
    @Bean
    public ServletRegistrationBean statViewServlet() {
        // 监控页面
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");

        // 后台需要有人登录，配置账户密码
        Map<String, String> initParameters = new HashMap<>();

        // 增加配置
        initParameters.put("LoginUsername", "admin");  // 登录key，是固定的 LoginUsername
        initParameters.put("LoginPassword", "123456");  // 登录key，是固定的 LoginPassword

        // 允许谁能访问
        initParameters.put("allow", "");  // ""-允许所有人访问；"localhost"-允许本机访问
        // 禁止谁访问
        initParameters.put("someone", "xxx.xxx.xxx.xxx");  // xxx.xxx.xxx.xxx - ip地址

        bean.setInitParameters(initParameters);  // 初始化参数
        return bean;
    }

    // 过滤器filter
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
