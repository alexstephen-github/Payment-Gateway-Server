package com.paygateway.mock.model;

import java.time.Instant;

public class BalanceTransaction {
    public String id;
    public String object = "balance_transaction";
    public Integer amount;
    public Integer fee;
    public Integer net;
    public String currency;
    public String type;
    public String status;
    public String source;
    public String description;
    public String reportingCategory;
    public Instant createdAt;
    public Instant availableOn;
}
