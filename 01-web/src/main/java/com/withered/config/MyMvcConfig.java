package com.withered.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Locale;

// 扩展spring mvc
// 自定义需要的功能，编写这个组件，然后将组件交给spring，springboot就会自动装配
@Configuration  // 该注解将一个类变成配置类
public class MyMvcConfig implements WebMvcConfigurer {

    // 将自定义视图解析器注入到spring中
    @Bean
    public ViewResolver MyViewResolver() {
        return new MyViewResolver();
    }

    // 自定义视图解析器。实现了视图解析器接口的类，可以看作是一个视图解析器
    public static class MyViewResolver implements ViewResolver {
        @Override
        public View resolveViewName(String s, Locale locale) throws Exception {
            return null;
        }
    }
}

