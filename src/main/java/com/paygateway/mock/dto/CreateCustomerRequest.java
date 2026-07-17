package com.paygateway.mock.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class CreateCustomerRequest {
    public String email;
    public String name;
    public String phone;
    public String description;
    public String paymentMethod;
    public String taxExempt;
    public Map<String, String> metadata;

    private final Map<String, Object> extra = new HashMap<>();

    @JsonAnySetter
    public void setExtra(String key, Object value) { extra.put(key, value); }

    @JsonAnyGetter
    public Map<String, Object> getExtra() { return extra; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>(extra);
        if (email != null) m.put("email", email);
        if (name != null) m.put("name", name);
        if (phone != null) m.put("phone", phone);
        if (description != null) m.put("description", description);
        if (paymentMethod != null) m.put("payment_method", paymentMethod);
        if (taxExempt != null) m.put("tax_exempt", taxExempt);
        if (metadata != null) m.put("metadata", metadata);
        return m;
    }
}
