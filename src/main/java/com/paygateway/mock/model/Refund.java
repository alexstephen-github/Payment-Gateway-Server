package com.paygateway.mock.model;

import java.time.Instant;
import java.util.Map;

public class Refund {
    public String id;
    public String object = "refund";
    public Integer amount;
    public String currency;
    public String paymentId;
    public String status;
    public String reason;
    public String failureReason;
    public String balanceTransaction;
    public String receiptNumber;
    public Map<String, String> metadata;
    public Instant createdAt;
}
