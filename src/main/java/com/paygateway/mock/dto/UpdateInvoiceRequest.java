package com.paygateway.mock.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class UpdateInvoiceRequest {
    public Boolean autoAdvance;
    public String collectionMethod;
    public String dueDate;
    public String description;
    public String footer;
    public Map<String, String> metadata;

    private final Map<String, Object> extra = new HashMap<>();

    @JsonAnySetter
    public void setExtra(String key, Object value) { extra.put(key, value); }

    @JsonAnyGetter
    public Map<String, Object> getExtra() { return extra; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>(extra);
        if (autoAdvance != null) m.put("auto_advance", autoAdvance);
        if (collectionMethod != null) m.put("collection_method", collectionMethod);
        if (dueDate != null) m.put("due_date", dueDate);
        if (description != null) m.put("description", description);
        if (footer != null) m.put("footer", footer);
        if (metadata != null) m.put("metadata", metadata);
        return m;
    }
}
