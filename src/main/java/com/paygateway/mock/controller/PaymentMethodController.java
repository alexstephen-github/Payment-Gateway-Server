package com.paygateway.mock.controller;

import com.paygateway.mock.model.Customer;
import com.paygateway.mock.model.PaymentMethod;
import com.paygateway.mock.model.PaymentMethodList;
import com.paygateway.mock.model.PaymentMethodValidationResult;
import com.paygateway.mock.support.MockFactory;
import com.paygateway.mock.support.Req;
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

    @PostMapping("/payment-methods/tokenize")
    public ResponseEntity<PaymentMethod> tokenize(@RequestBody(required = false) String raw) {
        PaymentMethod pm = factory.paymentMethod(Req.str(Req.asMap(raw), "type"), null);
        return ResponseEntity.created(URI.create("/v1/payment-methods/" + pm.id)).body(pm);
    }

    @PostMapping("/payment-methods/validate")
    public PaymentMethodValidationResult validate(@RequestBody(required = false) String raw) {
        return factory.validation();
    }

    /* ---- Customer-scoped payment methods ---- */

    @PostMapping("/customers/{customerId}/payment-methods")
    public PaymentMethod attach(@PathVariable String customerId,
                                @RequestBody(required = false) String raw) {
        PaymentMethod pm = factory.paymentMethod(null, customerId);
        String id = Req.str(Req.asMap(raw), "payment_method_id");
        if (id != null) {
            pm.id = id;
        }
        return pm;
    }

    @GetMapping("/customers/{customerId}/payment-methods")
    public PaymentMethodList list(@PathVariable String customerId,
                                  @RequestParam(name = "type", required = false) String type) {
        return factory.paymentMethodList(customerId, type, 3);
    }

    @GetMapping("/customers/{customerId}/payment-methods/{methodId}")
    public PaymentMethod get(@PathVariable String customerId, @PathVariable String methodId) {
        PaymentMethod pm = factory.paymentMethod(null, customerId);
        pm.id = methodId;
        return pm;
    }

    @PutMapping("/customers/{customerId}/payment-methods/{methodId}")
    public PaymentMethod update(@PathVariable String customerId, @PathVariable String methodId,
                                @RequestBody(required = false) String raw) {
        PaymentMethod pm = factory.paymentMethod(null, customerId);
        pm.id = methodId;
        return pm;
    }

    @DeleteMapping("/customers/{customerId}/payment-methods/{methodId}")
    public ResponseEntity<Void> detach(@PathVariable String customerId, @PathVariable String methodId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/customers/{customerId}/payment-methods/{methodId}/default")
    public Customer setDefault(@PathVariable String customerId, @PathVariable String methodId) {
        Customer customer = factory.customer(null);
        customer.id = customerId;
        customer.defaultPaymentMethodId = methodId;
        return customer;
    }
}
