package com.paygateway.mock.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class CreateRefundRequest {
    public Integer amount;
    public String reason;
    public Boolean refundApplicationFee;
    public Boolean reverseTransfer;
    public Map<String, String> metadata;

    private final Map<String, Object> extra = new HashMap<>();

    @JsonAnySetter
    public void setExtra(String key, Object value) { extra.put(key, value); }

    @JsonAnyGetter
    public Map<String, Object> getExtra() { return extra; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>(extra);
        if (amount != null) m.put("amount", amount);
        if (reason != null) m.put("reason", reason);
        if (refundApplicationFee != null) m.put("refund_application_fee", refundApplicationFee);
        if (reverseTransfer != null) m.put("reverse_transfer", reverseTransfer);
        if (metadata != null) m.put("metadata", metadata);
        return m;
    }
}
