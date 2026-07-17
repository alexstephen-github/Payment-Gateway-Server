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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@Tag(name = "Payment Methods", description = "Card tokenization and saved payment methods")
public class PaymentMethodController {

    private final MockFactory factory;

    public PaymentMethodController(MockFactory factory) {
        this.factory = factory;
    }

    @Operation(summary = "Tokenize a card or bank account",
               description = "Client-side tokenization of a payment method. Use a publishable key for authentication. No raw card data is stored on the server.")
    @PostMapping(value = "/payments/payment-methods/tokenize", consumes = "application/json")
    public ResponseEntity<PaymentMethod> tokenize(
            @RequestBody(required = false) TokenizePaymentMethodRequest request) {
        String type = request != null ? request.type : null;
        PaymentMethod pm = factory.paymentMethod(type, null);
        return ResponseEntity.created(URI.create("/payments/payment-methods/" + pm.id)).body(pm);
    }

    @Operation(summary = "Validate a payment method token",
               description = "Validates whether a payment method token is still valid and usable for transactions.")
    @PostMapping(value = "/payments/payment-methods/validate", consumes = "application/json")
    public PaymentMethodValidationResult validate(
            @RequestBody(required = false) ValidatePaymentMethodRequest request) {
        return factory.validation();
    }

    @Operation(summary = "Attach a payment method to a customer",
               description = "Attaches an existing payment method token to a customer, making it available for future payments.")
    @PostMapping(value = "/payments/customers/{customerId}/payment-methods", consumes = "application/json")
    public PaymentMethod attach(@PathVariable String customerId,
                                @RequestBody(required = false) AttachPaymentMethodRequest request) {
        PaymentMethod pm = factory.paymentMethod(null, customerId);
        if (request != null && request.paymentMethodId != null) {
            pm.id = request.paymentMethodId;
        }
        return pm;
    }

    @Operation(summary = "List customer payment methods",
               description = "Returns all payment methods attached to a customer. Optionally filter by type (card, bank_account, wallet).")
    @GetMapping("/payments/customers/{customerId}/payment-methods")
    public PaymentMethodList list(@PathVariable String customerId,
                                  @RequestParam(name = "type", required = false) String type) {
        return factory.paymentMethodList(customerId, type, 3);
    }

    @Operation(summary = "Retrieve a payment method",
               description = "Retrieves the details of a specific payment method attached to a customer.")
    @GetMapping("/payments/customers/{customerId}/payment-methods/{methodId}")
    public PaymentMethod get(@PathVariable String customerId, @PathVariable String methodId) {
        PaymentMethod pm = factory.paymentMethod(null, customerId);
        pm.id = methodId;
        return pm;
    }

    @Operation(summary = "Update a payment method",
               description = "Updates the billing details or metadata of an existing payment method.")
    @PutMapping(value = "/payments/customers/{customerId}/payment-methods/{methodId}", consumes = "application/json")
    public PaymentMethod update(@PathVariable String customerId, @PathVariable String methodId,
                                @RequestBody(required = false) UpdatePaymentMethodRequest request) {
        PaymentMethod pm = factory.paymentMethod(null, customerId);
        pm.id = methodId;
        return pm;
    }

    @Operation(summary = "Detach a payment method from customer",
               description = "Detaches a payment method from a customer. The payment method can no longer be used for payments by this customer.")
    @DeleteMapping("/payments/customers/{customerId}/payment-methods/{methodId}")
    public ResponseEntity<Void> detach(@PathVariable String customerId, @PathVariable String methodId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Set default payment method for a customer",
               description = "Sets a specific payment method as the default for a customer. This method will be used automatically for recurring charges.")
    @PutMapping("/payments/customers/{customerId}/payment-methods/{methodId}/default")
    public Customer setDefault(@PathVariable String customerId, @PathVariable String methodId) {
        Customer customer = factory.customer(null);
        customer.id = customerId;
        customer.defaultPaymentMethodId = methodId;
        return customer;
    }
}
