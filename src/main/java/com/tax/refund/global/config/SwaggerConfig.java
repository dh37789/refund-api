package com.tax.refund.global.config;

import com.tax.refund.global.config.common.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .consumes(createConsumeContentTypes())
                .produces(createProduceContentTypes())
                .apiInfo(createSwaggerInfo()).select()
                .apis(RequestHandlerSelectors.basePackage(Constants.CONTROLLER_PATH))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false);

    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Bearer +accessToken", authorizationScopes));
    }

    private ApiKey apiKey() {
        return new ApiKey("Bearer +accessToken", HttpHeaders.AUTHORIZATION, "header");
    }

    private ApiInfo createSwaggerInfo() {
        return new ApiInfoBuilder()
                .title("연말정산 환급금 조회 APi")
                .description("환급 조회 api 테스트")
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
