package com.paygateway.mock.controller;

import com.paygateway.mock.dto.*;
import com.paygateway.mock.model.Payment;
import com.paygateway.mock.model.PaymentList;
import com.paygateway.mock.support.MockFactory;
import com.paygateway.mock.support.Req;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/payments/payments")
@Tag(name = "Payments", description = "Payment initiation, capture, void, and cancellation")
public class PaymentController {

    private final MockFactory factory;

    public PaymentController(MockFactory factory) {
        this.factory = factory;
    }

    @Operation(summary = "Create a payment",
               description = "Initiates a payment. Set capture_method to 'automatic' for immediate charge or 'manual' for a two-step authorize-then-capture flow.")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Payment> create(@RequestBody(required = false) CreatePaymentRequest request) {
        Map<String, Object> body = request != null ? request.toMap() : null;
        Payment payment = factory.payment(body);
        if (body != null && Boolean.TRUE.equals(body.get("confirm"))) {
            payment.status = "succeeded";
        }
        return ResponseEntity.created(URI.create("/payments/payments/" + payment.id)).body(payment);
    }

    @Operation(summary = "List payments",
               description = "Returns a paginated list of payments. Optionally filter by customer ID or status.")
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

    @Operation(summary = "Retrieve a payment",
               description = "Retrieves the details of an existing payment by its unique identifier.")
    @GetMapping("/{paymentId}")
    public Payment get(@PathVariable String paymentId) {
        Payment payment = factory.payment(null);
        payment.id = paymentId;
        return payment;
    }

    @Operation(summary = "Capture an authorized payment",
               description = "Captures a previously authorized payment. The amount to capture must be less than or equal to the authorized amount.")
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

    @Operation(summary = "Void an authorized payment",
               description = "Voids a previously authorized payment that has not yet been captured. The authorization is released back to the customer.")
    @PostMapping(value = "/{paymentId}/void", consumes = "application/json")
    public Payment voidPayment(@PathVariable String paymentId,
                               @RequestBody(required = false) VoidPaymentRequest request) {
        return terminal(paymentId, "cancelled");
    }

    @Operation(summary = "Cancel a payment",
               description = "Cancels a payment that is in a cancellable state. Once cancelled, no further actions can be taken on the payment.")
    @PostMapping(value = "/{paymentId}/cancel", consumes = "application/json")
    public Payment cancel(@PathVariable String paymentId,
                          @RequestBody(required = false) CancelPaymentRequest request) {
        return terminal(paymentId, "cancelled");
    }

    @Operation(summary = "Confirm a payment",
               description = "Confirms a payment that requires confirmation, such as after 3D Secure authentication or a redirect-based payment method.")
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
