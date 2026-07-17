package com.paygateway.mock.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class CapturePaymentRequest {
    public Integer amountToCapture;
    public String statementDescriptorSuffix;
    public Map<String, String> metadata;

    private final Map<String, Object> extra = new HashMap<>();

    @JsonAnySetter
    public void setExtra(String key, Object value) { extra.put(key, value); }

    @JsonAnyGetter
    public Map<String, Object> getExtra() { return extra; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>(extra);
        if (amountToCapture != null) m.put("amount_to_capture", amountToCapture);
        if (statementDescriptorSuffix != null) m.put("statement_descriptor_suffix", statementDescriptorSuffix);
        if (metadata != null) m.put("metadata", metadata);
        return m;
    }
}
