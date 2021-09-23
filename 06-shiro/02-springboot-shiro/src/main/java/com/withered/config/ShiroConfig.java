package com.withered.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
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
    @Bean(name = "shiroFilterFactoryBean")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(defaultWebSecurityManager);  // 设置安全管理器

        // 添加内置过滤器
        /*
         * anon:无需认证就可以访问
         * authc:必须认证了才能让问
         * user:必须拥有记住我功能才能用
         * perms :拥有对某个资源的权限才能访问;
         * role:拥有某个角色权限才能访问
         * */
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

    // Step2 创建DefaultWebSecurityManager
    @Bean(name = "securityManager")
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

    // 整合ShiroDialect
    @Bean
    public ShiroDialect getShiroDialect () {
        return new ShiroDialect();
    }
}
