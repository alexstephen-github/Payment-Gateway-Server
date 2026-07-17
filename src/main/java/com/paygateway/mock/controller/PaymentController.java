package com.paygateway.mock.controller;

import com.paygateway.mock.dto.*;
import com.paygateway.mock.model.Payment;
import com.paygateway.mock.model.PaymentList;
import com.paygateway.mock.support.MockFactory;
import com.paygateway.mock.support.Req;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/payments/payments")
public class PaymentController {

    private final MockFactory factory;

    public PaymentController(MockFactory factory) {
        this.factory = factory;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Payment> create(@RequestBody(required = false) CreatePaymentRequest request) {
        Map<String, Object> body = request != null ? request.toMap() : null;
        Payment payment = factory.payment(body);
        if (body != null && Boolean.TRUE.equals(body.get("confirm"))) {
            payment.status = "succeeded";
        }
        return ResponseEntity.created(URI.create("/payments/payments/" + payment.id)).body(payment);
    }

    @GetMapping
    public PaymentList list(@RequestParam(name = "customer_id", required = false) String customerId,
                            @RequestParam(name = "status", required = false) String status,
                            @RequestParam(name = "limit", defaultValue = "20") int limit) {
        Map<String, Object> filters = new java.util.HashMap<>();
        if (customerId != null) {
            filters.put("customer_id", customerId);
        }
        return factory.paymentList(limit, filters);
    }

    @GetMapping("/{paymentId}")
    public Payment get(@PathVariable String paymentId) {
        Payment payment = factory.payment(null);
        payment.id = paymentId;
        return payment;
    }

    @PostMapping(value = "/{paymentId}/capture", consumes = "application/json")
    public Payment capture(@PathVariable String paymentId,
                           @RequestBody(required = false) CapturePaymentRequest request) {
        Map<String, Object> body = request != null ? request.toMap() : null;
        Payment payment = factory.payment(null);
        payment.id = paymentId;
        Integer toCapture = Req.integer(body, "amount_to_capture");
        if (toCapture != null) {
            payment.amount = toCapture;
        }
        payment.status = "captured";
        payment.amountReceived = payment.amount;
        payment.amountCapturable = 0;
        return payment;
    }

    @PostMapping(value = "/{paymentId}/void", consumes = "application/json")
    public Payment voidPayment(@PathVariable String paymentId,
                               @RequestBody(required = false) VoidPaymentRequest request) {
        return terminal(paymentId, "cancelled");
    }

    @PostMapping(value = "/{paymentId}/cancel", consumes = "application/json")
    public Payment cancel(@PathVariable String paymentId,
                          @RequestBody(required = false) CancelPaymentRequest request) {
        return terminal(paymentId, "cancelled");
    }

    @PostMapping(value = "/{paymentId}/confirm", consumes = "application/json")
    public Payment confirm(@PathVariable String paymentId,
                           @RequestBody(required = false) ConfirmPaymentRequest request) {
        Payment payment = factory.payment(null);
        payment.id = paymentId;
        payment.status = "succeeded";
        payment.amountReceived = payment.amount;
        payment.amountCapturable = 0;
        return payment;
    }

    private Payment terminal(String paymentId, String status) {
        Payment payment = factory.payment(null);
        payment.id = paymentId;
        payment.status = status;
        payment.amountReceived = 0;
        payment.amountCapturable = 0;
        return payment;
    }
}
