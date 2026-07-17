package com.paygateway.mock.controller;

import com.paygateway.mock.model.Refund;
import com.paygateway.mock.model.RefundList;
import com.paygateway.mock.support.MockFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
public class RefundController {

    private final MockFactory factory;

    public RefundController(MockFactory factory) {
        this.factory = factory;
    }

    @PostMapping(value = "/payments/payments/{paymentId}/refunds", consumes = "application/json")
    public ResponseEntity<Refund> create(@PathVariable String paymentId,
                                         @RequestBody(required = false) Map<String, Object> body) {
        Refund refund = factory.refund(paymentId, body);
        return ResponseEntity.created(URI.create("/payments/refunds/" + refund.id)).body(refund);
    }

    @GetMapping("/payments/payments/{paymentId}/refunds")
    public RefundList listForPayment(@PathVariable String paymentId,
                                     @RequestParam(name = "limit", defaultValue = "20") int limit) {
        return factory.refundList(paymentId, limit);
    }

    @GetMapping("/payments/refunds")
    public RefundList list(@RequestParam(name = "limit", defaultValue = "20") int limit) {
        return factory.refundList(null, limit);
    }

    @GetMapping("/payments/refunds/{refundId}")
    public Refund get(@PathVariable String refundId) {
        Refund refund = factory.refund(null, null);
        refund.id = refundId;
        return refund;
    }
}
