package com.yami.shop.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public GroupedOpenApi createRestApi() {
        return GroupedOpenApi.builder()
                .group("接口文档")
                .packagesToScan("com.yami.shop.api")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("online-shop接口文档")
                        .description("online-shop接口文档，openapi3.0 接口，用于前端对接")
                        .version("v0.0.1")
                        .license(new License().name("online-shop")));
    }
}
