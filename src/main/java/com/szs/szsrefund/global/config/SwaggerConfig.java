package com.szs.szsrefund.global.config;

import com.szs.szsrefund.global.config.common.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .consumes(createConsumeContentTypes())
                .produces(createProduceContentTypes())
                .apiInfo(createSwaggerInfo()).select()
                .apis(RequestHandlerSelectors.basePackage(Constants.CONTROLLER_PATH))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false);
    }

    private ApiInfo createSwaggerInfo() {
        return new ApiInfoBuilder()
                .title("삼쩜삼 환급금 조회 APi")
                .description("자비스앤빌런즈 과제 테스트")
                .version("1.0")
                .build();
    }

    private Set<String> createConsumeContentTypes() {
        Set<String> consumes = new HashSet<>();
        consumes.add(Constants.CONTENT_TYPE);
        return consumes;
    }

    private Set<String> createProduceContentTypes() {
        Set<String> produces = new HashSet<>();
        produces.add(Constants.CONTENT_TYPE);
        return produces;
    }

}
