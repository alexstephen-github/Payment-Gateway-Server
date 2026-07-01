package com.paygateway.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Payment Gateway mock server.
 *
 * <p>The server implements every path declared in {@code payment-gateway-api-openapi.yaml}
 * (Payments, Refunds, Customers, Payment Methods, Invoices and Balance) and answers each
 * request with randomly generated data that matches the response schema. Where the request
 * body supplies meaningful values (amount, currency, email, ...) those values are echoed
 * back so the responses feel coherent.
 */
@SpringBootApplication
public class PaymentGatewayMockApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentGatewayMockApplication.class, args);
    }
}
