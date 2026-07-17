package com.paygateway.mock.controller;

import com.paygateway.mock.dto.CreateRefundRequest;
import com.paygateway.mock.model.Refund;
import com.paygateway.mock.model.RefundList;
import com.paygateway.mock.support.MockFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@Tag(name = "Refunds", description = "Full and partial refund operations")
public class RefundController {

    private final MockFactory factory;

    public RefundController(MockFactory factory) {
        this.factory = factory;
    }

    @Operation(summary = "Create a refund for a payment",
               description = "Creates a refund for a specific payment. Omit the amount to issue a full refund, or specify an amount for a partial refund.")
    @PostMapping(value = "/payments/payments/{paymentId}/refunds", consumes = "application/json")
    public ResponseEntity<Refund> create(@PathVariable String paymentId,
                                         @RequestBody(required = false) CreateRefundRequest request) {
        Map<String, Object> body = request != null ? request.toMap() : null;
        Refund refund = factory.refund(paymentId, body);
        return ResponseEntity.created(URI.create("/payments/refunds/" + refund.id)).body(refund);
    }

    @Operation(summary = "List refunds for a payment",
               description = "Returns a paginated list of refunds associated with a specific payment.")
    @GetMapping("/payments/payments/{paymentId}/refunds")
    public RefundList listForPayment(@PathVariable String paymentId,
                                     @RequestParam(name = "limit", defaultValue = "20") int limit) {
        return factory.refundList(paymentId, limit);
    }

    @Operation(summary = "List all refunds",
               description = "Returns a paginated list of all refunds across all payments.")
    @GetMapping("/payments/refunds")
    public RefundList list(@RequestParam(name = "limit", defaultValue = "20") int limit) {
        return factory.refundList(null, limit);
    }

    @Operation(summary = "Retrieve a refund",
               description = "Retrieves the details of an existing refund by its unique identifier.")
    @GetMapping("/payments/refunds/{refundId}")
    public Refund get(@PathVariable String refundId) {
        Refund refund = factory.refund(null, null);
        refund.id = refundId;
        return refund;
    }
}
