package com.paygateway.mock.controller;

import com.paygateway.mock.dto.AttachPaymentMethodRequest;
import com.paygateway.mock.dto.TokenizePaymentMethodRequest;
import com.paygateway.mock.dto.UpdatePaymentMethodRequest;
import com.paygateway.mock.dto.ValidatePaymentMethodRequest;
import com.paygateway.mock.model.Customer;
import com.paygateway.mock.model.PaymentMethod;
import com.paygateway.mock.model.PaymentMethodList;
import com.paygateway.mock.model.PaymentMethodValidationResult;
import com.paygateway.mock.support.MockFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class PaymentMethodController {

    private final MockFactory factory;

    public PaymentMethodController(MockFactory factory) {
        this.factory = factory;
    }

    /* ---- Standalone tokenization / validation ---- */

    @PostMapping(value = "/payments/payment-methods/tokenize", consumes = "application/json")
    public ResponseEntity<PaymentMethod> tokenize(
            @RequestBody(required = false) TokenizePaymentMethodRequest request) {
        String type = request != null ? request.type : null;
        PaymentMethod pm = factory.paymentMethod(type, null);
        return ResponseEntity.created(URI.create("/payments/payment-methods/" + pm.id)).body(pm);
    }

    @PostMapping(value = "/payments/payment-methods/validate", consumes = "application/json")
    public PaymentMethodValidationResult validate(
            @RequestBody(required = false) ValidatePaymentMethodRequest request) {
        return factory.validation();
    }

    /* ---- Customer-scoped payment methods ---- */

    @PostMapping(value = "/payments/customers/{customerId}/payment-methods", consumes = "application/json")
    public PaymentMethod attach(@PathVariable String customerId,
                                @RequestBody(required = false) AttachPaymentMethodRequest request) {
        PaymentMethod pm = factory.paymentMethod(null, customerId);
        if (request != null && request.paymentMethodId != null) {
            pm.id = request.paymentMethodId;
        }
        return pm;
    }

    @GetMapping("/payments/customers/{customerId}/payment-methods")
    public PaymentMethodList list(@PathVariable String customerId,
                                  @RequestParam(name = "type", required = false) String type) {
        return factory.paymentMethodList(customerId, type, 3);
    }

    @GetMapping("/payments/customers/{customerId}/payment-methods/{methodId}")
    public PaymentMethod get(@PathVariable String customerId, @PathVariable String methodId) {
        PaymentMethod pm = factory.paymentMethod(null, customerId);
        pm.id = methodId;
        return pm;
    }

    @PutMapping(value = "/payments/customers/{customerId}/payment-methods/{methodId}", consumes = "application/json")
    public PaymentMethod update(@PathVariable String customerId, @PathVariable String methodId,
                                @RequestBody(required = false) UpdatePaymentMethodRequest request) {
        PaymentMethod pm = factory.paymentMethod(null, customerId);
        pm.id = methodId;
        return pm;
    }

    @DeleteMapping("/payments/customers/{customerId}/payment-methods/{methodId}")
    public ResponseEntity<Void> detach(@PathVariable String customerId, @PathVariable String methodId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/payments/customers/{customerId}/payment-methods/{methodId}/default")
    public Customer setDefault(@PathVariable String customerId, @PathVariable String methodId) {
        Customer customer = factory.customer(null);
        customer.id = customerId;
        customer.defaultPaymentMethodId = methodId;
        return customer;
    }
}
