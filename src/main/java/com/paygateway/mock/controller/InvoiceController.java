package com.paygateway.mock.controller;

import com.paygateway.mock.dto.CreateInvoiceRequest;
import com.paygateway.mock.dto.PayInvoiceRequest;
import com.paygateway.mock.dto.UpdateInvoiceRequest;
import com.paygateway.mock.model.Invoice;
import com.paygateway.mock.model.InvoiceList;
import com.paygateway.mock.support.MockFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments/invoices")
@Tag(name = "Invoices", description = "Invoice lifecycle management")
public class InvoiceController {

    private final MockFactory factory;

    public InvoiceController(MockFactory factory) {
        this.factory = factory;
    }

    @Operation(summary = "Create a draft invoice",
               description = "Creates a new invoice in draft status for a customer. Add line items and finalize when ready to send.")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Invoice> create(@RequestBody(required = false) CreateInvoiceRequest request) {
        Map<String, Object> body = request != null ? request.toMap() : null;
        Invoice invoice = factory.invoice(body, "draft");
        return ResponseEntity.created(URI.create("/payments/invoices/" + invoice.id)).body(invoice);
    }

    @Operation(summary = "List invoices",
               description = "Returns a paginated list of invoices. Optionally filter by customer ID or status.")
    @GetMapping
    public InvoiceList list(@RequestParam(name = "customer_id", required = false) String customerId,
                            @RequestParam(name = "status", required = false) String status,
                            @RequestParam(name = "limit", defaultValue = "20") int limit) {
        Map<String, Object> filters = new HashMap<>();
        if (customerId != null) {
            filters.put("customer_id", customerId);
        }
        return factory.invoiceList(limit, filters);
    }

    @Operation(summary = "Retrieve an invoice",
               description = "Retrieves the details of an existing invoice by its unique identifier.")
    @GetMapping("/{invoiceId}")
    public Invoice get(@PathVariable String invoiceId) {
        return withId(factory.invoice(null, "open"), invoiceId);
    }

    @Operation(summary = "Update a draft invoice",
               description = "Updates the specified draft invoice. Only invoices in draft status can be updated.")
    @PutMapping(value = "/{invoiceId}", consumes = "application/json")
    public Invoice update(@PathVariable String invoiceId,
                          @RequestBody(required = false) UpdateInvoiceRequest request) {
        Map<String, Object> body = request != null ? request.toMap() : null;
        return withId(factory.invoice(body, "draft"), invoiceId);
    }

    @Operation(summary = "Delete a draft invoice",
               description = "Permanently deletes a draft invoice. Only invoices in draft status can be deleted.")
    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<Void> delete(@PathVariable String invoiceId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Finalize an invoice",
               description = "Finalizes a draft invoice, transitioning it from draft to open status. Once finalized, it can be sent to the customer for payment.")
    @PostMapping("/{invoiceId}/finalize")
    public Invoice finalize(@PathVariable String invoiceId) {
        return withId(factory.invoice(null, "open"), invoiceId);
    }

    @Operation(summary = "Pay an open invoice",
               description = "Attempts to pay an open invoice using the specified payment method or the customer's default payment method.")
    @PostMapping(value = "/{invoiceId}/pay", consumes = "application/json")
    public Invoice pay(@PathVariable String invoiceId,
                       @RequestBody(required = false) PayInvoiceRequest request) {
        return withId(factory.invoice(null, "paid"), invoiceId);
    }

    @Operation(summary = "Send an invoice by email",
               description = "Sends an open invoice to the customer via email with a hosted payment page link.")
    @PostMapping("/{invoiceId}/send")
    public Invoice send(@PathVariable String invoiceId) {
        return withId(factory.invoice(null, "open"), invoiceId);
    }

    @Operation(summary = "Void an open invoice",
               description = "Voids an open invoice. A voided invoice can no longer be paid and is effectively cancelled.")
    @PostMapping("/{invoiceId}/void")
    public Invoice voidInvoice(@PathVariable String invoiceId) {
        return withId(factory.invoice(null, "void"), invoiceId);
    }

    private Invoice withId(Invoice invoice, String id) {
        invoice.id = id;
        return invoice;
    }
}
