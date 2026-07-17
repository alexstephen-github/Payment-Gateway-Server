package com.paygateway.mock.controller;

import com.paygateway.mock.model.Invoice;
import com.paygateway.mock.model.InvoiceList;
import com.paygateway.mock.support.MockFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments/invoices")
public class InvoiceController {

    private final MockFactory factory;

    public InvoiceController(MockFactory factory) {
        this.factory = factory;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Invoice> create(@RequestBody(required = false) Map<String, Object> body) {
        Invoice invoice = factory.invoice(body, "draft");
        return ResponseEntity.created(URI.create("/payments/invoices/" + invoice.id)).body(invoice);
    }

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

    @GetMapping("/{invoiceId}")
    public Invoice get(@PathVariable String invoiceId) {
        return withId(factory.invoice(null, "open"), invoiceId);
    }

    @PutMapping(value = "/{invoiceId}", consumes = "application/json")
    public Invoice update(@PathVariable String invoiceId,
                          @RequestBody(required = false) Map<String, Object> body) {
        return withId(factory.invoice(body, "draft"), invoiceId);
    }

    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<Void> delete(@PathVariable String invoiceId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{invoiceId}/finalize")
    public Invoice finalize(@PathVariable String invoiceId) {
        return withId(factory.invoice(null, "open"), invoiceId);
    }

    @PostMapping(value = "/{invoiceId}/pay", consumes = "application/json")
    public Invoice pay(@PathVariable String invoiceId,
                       @RequestBody(required = false) Map<String, Object> body) {
        return withId(factory.invoice(null, "paid"), invoiceId);
    }

    @PostMapping("/{invoiceId}/send")
    public Invoice send(@PathVariable String invoiceId) {
        return withId(factory.invoice(null, "open"), invoiceId);
    }

    @PostMapping("/{invoiceId}/void")
    public Invoice voidInvoice(@PathVariable String invoiceId) {
        return withId(factory.invoice(null, "void"), invoiceId);
    }

    private Invoice withId(Invoice invoice, String id) {
        invoice.id = id;
        return invoice;
    }
}
