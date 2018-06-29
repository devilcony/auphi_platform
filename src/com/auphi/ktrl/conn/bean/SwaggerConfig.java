package com.auphi.ktrl.conn.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @auther Tony
 * @create 2018-05-28 10:36
 */
@Configuration    // 配置注解，自动在本类上下文加载一些环境变量信息
@EnableSwagger2   // 使swagger2生效
@EnableWebMvc
@ComponentScan(basePackages="com.auphi.ktrl")
public class SwaggerConfig {


    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                //.ignoredParameterTypes(ModelMap.class, HttpServletRequest.class,HttpServletResponse.class, BindingResult.class)
                .apis(RequestHandlerSelectors.basePackage("com.auphi.ktrl"))
                .build()
                .apiInfo(apiInfo())
                //.globalOperationParameters(setHeaderToken())
                .ignoredParameterTypes(ApiIgnore.class);
    }




    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("傲飞数据整合平台接口API 文档")
                .description("傲飞数据整合平台接口API接口\n\r")
                .version("1.0.0")
                .build();
    }


}