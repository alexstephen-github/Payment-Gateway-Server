package com.paygateway.mock.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentGatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payment Gateway API")
                        .version("2.0.0")
                        .description("Mock server for the Payment Gateway API covering payments, "
                                + "refunds, customers, payment methods, invoices, and balance.")
                        .contact(new Contact()
                                .name("Payment Gateway Support")
                                .email("api-support@paygateway.example.com"))
                        .license(new License()
                                .name("Proprietary")));
    }
}
