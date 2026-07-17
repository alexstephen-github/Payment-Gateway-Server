package com.paygateway.mock.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class CreateInvoiceRequest {
    public String customerId;
    public String subscriptionId;
    public Boolean autoAdvance;
    public String collectionMethod;
    public String dueDate;
    public String description;
    public String footer;
    public String statementDescriptor;
    public String paymentMethodId;
    public Map<String, String> metadata;

    private final Map<String, Object> extra = new HashMap<>();

    @JsonAnySetter
    public void setExtra(String key, Object value) { extra.put(key, value); }

    @JsonAnyGetter
    public Map<String, Object> getExtra() { return extra; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>(extra);
        if (customerId != null) m.put("customer_id", customerId);
        if (subscriptionId != null) m.put("subscription_id", subscriptionId);
        if (autoAdvance != null) m.put("auto_advance", autoAdvance);
        if (collectionMethod != null) m.put("collection_method", collectionMethod);
        if (dueDate != null) m.put("due_date", dueDate);
        if (description != null) m.put("description", description);
        if (footer != null) m.put("footer", footer);
        if (statementDescriptor != null) m.put("statement_descriptor", statementDescriptor);
        if (paymentMethodId != null) m.put("payment_method_id", paymentMethodId);
        if (metadata != null) m.put("metadata", metadata);
        return m;
    }
}
