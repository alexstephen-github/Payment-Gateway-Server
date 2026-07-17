package com.paygateway.mock.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class CreatePaymentRequest {
    public Integer amount;
    public String currency;
    public String paymentMethod;
    public String customerId;
    public String captureMethod;
    public String description;
    public String statementDescriptor;
    public String statementDescriptorSuffix;
    public String receiptEmail;
    public String returnUrl;
    public Boolean confirm;
    public String setupFutureUsage;
    public Integer applicationFeeAmount;
    public String onBehalfOf;
    public Map<String, String> metadata;

    private final Map<String, Object> extra = new HashMap<>();

    @JsonAnySetter
    public void setExtra(String key, Object value) { extra.put(key, value); }

    @JsonAnyGetter
    public Map<String, Object> getExtra() { return extra; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>(extra);
        if (amount != null) m.put("amount", amount);
        if (currency != null) m.put("currency", currency);
        if (paymentMethod != null) m.put("payment_method", paymentMethod);
        if (customerId != null) m.put("customer_id", customerId);
        if (captureMethod != null) m.put("capture_method", captureMethod);
        if (description != null) m.put("description", description);
        if (statementDescriptor != null) m.put("statement_descriptor", statementDescriptor);
        if (statementDescriptorSuffix != null) m.put("statement_descriptor_suffix", statementDescriptorSuffix);
        if (receiptEmail != null) m.put("receipt_email", receiptEmail);
        if (returnUrl != null) m.put("return_url", returnUrl);
        if (confirm != null) m.put("confirm", confirm);
        if (setupFutureUsage != null) m.put("setup_future_usage", setupFutureUsage);
        if (applicationFeeAmount != null) m.put("application_fee_amount", applicationFeeAmount);
        if (onBehalfOf != null) m.put("on_behalf_of", onBehalfOf);
        if (metadata != null) m.put("metadata", metadata);
        return m;
    }
}
