package com.paygateway.mock.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class InvoiceItem {
    public String id;
    public String object = "invoice_item";
    public String customerId;
    public String invoiceId;
    public Integer amount;
    public String currency;
    public String description;
    public Integer quantity;
    public Integer unitAmount;
    public String priceId;
    public List<String> taxRates;
    public Boolean discountable;
    public Map<String, String> metadata;
    public Instant createdAt;
}
