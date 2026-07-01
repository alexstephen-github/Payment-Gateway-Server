package com.paygateway.mock.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Invoice {
    public String id;
    public String object = "invoice";
    public String status;
    public String customerId;
    public String subscriptionId;
    public String number;
    public Integer amountDue;
    public Integer amountPaid;
    public Integer amountRemaining;
    public Integer subtotal;
    public Integer tax;
    public Integer total;
    public String currency;
    public Lines lines;
    public LocalDate dueDate;
    public Instant paidAt;
    public Instant voidedAt;
    public String hostedInvoiceUrl;
    public String invoicePdf;
    public String paymentIntentId;
    public String collectionMethod;
    public String footer;
    public Map<String, String> metadata;
    public Instant createdAt;

    public static class Lines {
        public List<InvoiceItem> data;
    }
}
