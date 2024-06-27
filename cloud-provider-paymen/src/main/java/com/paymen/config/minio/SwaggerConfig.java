//package com.paymen.config;
//
//import com.google.common.base.Predicates;
//import com.google.common.collect.Lists;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.ParameterBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.schema.ModelRef;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.service.Parameter;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//import java.util.List;
//
///**
// * @description:
// * @author: huangzouqiang
// * @create: 2024-06-26 20:10
// * @Version 1.0
// **/
//
//@Configuration
//@EnableSwagger2
//public class SwaggerConfig {
//    @Bean
//    public Docket webApiConfig(){
//        //设置分片下载的swagger请求头
//        List<Parameter> operationParameters = Lists.newArrayList();
//        operationParameters.add(new ParameterBuilder().
//                name("Range").
//                description("Range").
//                modelRef(new ModelRef("string")).
//                parameterType("header").defaultValue("bytes=0-").hidden(true).required(true).build());
//
//        return new Docket(DocumentationType.SWAGGER_2)
//                .groupName("龙虾编程")
//                .apiInfo(webApiInfo())
//                .select()
//                //接口中由/admin   /error就不显示
//                .paths(Predicates.not(PathSelectors.regex("/admin/.*")))
//                .paths(Predicates.not(PathSelectors.regex("/error.*")))
//                //扫描指定的包
//                .apis(RequestHandlerSelectors.basePackage("com.longxia"))
//                .build()
//                .globalOperationParameters(operationParameters);
//    }
//
//    private ApiInfo webApiInfo(){
//        return new ApiInfoBuilder()
//                .title("龙虾编程——分片下载")     //swagger页面上大标题
//                .description("龙虾编程——分片下载")    //描述
//                .version("1.0")
//                .contact(new Contact("龙虾编程", "http://www.baidu.com", "1733150517@qq.com"))
//                .build();
//    }
//
//}
