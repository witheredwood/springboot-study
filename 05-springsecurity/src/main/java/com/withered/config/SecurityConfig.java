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
        http.formLogin().usernameParameter("name").passwordParameter("pwd").loginPage("/login");
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
