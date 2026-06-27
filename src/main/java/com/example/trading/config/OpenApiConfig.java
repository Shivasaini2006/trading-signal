package com.example.trading.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tradingSignalOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Trading Signal API")
                .description("API for tracking and evaluating trading signals")
                .version("v1.0.0"));
    }
}
