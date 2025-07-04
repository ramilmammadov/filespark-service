package com.filespark.storage.infra;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI storageServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Filespark Storage Service API")
                        .description("REST API for uploading, managing, and downloading files")
                        .version("1.0.0"));
    }
}
