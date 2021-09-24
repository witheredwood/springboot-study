package com.withered.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2  // 开启swagger2
public class SwaggerConfig {

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

    // 配置了swagger的docket的bean实例
    @Bean
    public Docket docket(Environment environment) {
        // 设置要显示的Swagger环境
        Profiles profiles = Profiles.of ("dev", "test");
        // 获取项目当前环境
        boolean flag = environment.acceptsProfiles(profiles);  // 通过environment.acceptsProfiles判断是否处在自己设定的环境当中
        System.out.println(flag);

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("withered")
                .enable(flag)  // 是否启用swagger
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.withered.controller"))  // RequestHandlerSelectors：配置要扫描的接口的方式
                .paths(PathSelectors.ant("/withered/**"))  // 过滤什么路径
                .build();
    }

    // 配置swagger基本信息，也就是配置apiInfo
    private ApiInfo apiInfo() {
        // 作者信息
        Contact contact = new Contact("withered", "", "");
        return new ApiInfo(
               "Withered的Api文档",
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
