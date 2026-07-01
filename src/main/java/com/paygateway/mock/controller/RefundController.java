package com.paygateway.mock.controller;

import com.paygateway.mock.model.Refund;
import com.paygateway.mock.model.RefundList;
import com.paygateway.mock.support.MockFactory;
import com.paygateway.mock.support.Req;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class RefundController {

    private final MockFactory factory;

    public RefundController(MockFactory factory) {
        this.factory = factory;
    }

    @PostMapping("/payments/{paymentId}/refunds")
    public ResponseEntity<Refund> create(@PathVariable String paymentId,
                                         @RequestBody(required = false) String raw) {
        Refund refund = factory.refund(paymentId, Req.asMap(raw));
        return ResponseEntity.created(URI.create("/v1/refunds/" + refund.id)).body(refund);
    }

    @GetMapping("/payments/{paymentId}/refunds")
    public RefundList listForPayment(@PathVariable String paymentId,
                                     @RequestParam(name = "limit", defaultValue = "20") int limit) {
        return factory.refundList(paymentId, limit);
    }

    @GetMapping("/refunds")
    public RefundList list(@RequestParam(name = "limit", defaultValue = "20") int limit) {
        return factory.refundList(null, limit);
    }

    @GetMapping("/refunds/{refundId}")
    public Refund get(@PathVariable String refundId) {
        Refund refund = factory.refund(null, null);
        refund.id = refundId;
        return refund;
    }
}
