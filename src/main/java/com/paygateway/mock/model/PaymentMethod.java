package com.paygateway.mock.model;

import java.time.Instant;

public class PaymentMethod {
    public String id;
    public String object = "payment_method";
    public String type;
    public CardDetails card;
    public BankAccountDetails bankAccount;
    public BillingDetails billingDetails;
    public String customerId;
    public String fingerprint;
    public Boolean isDefault;
    public Instant createdAt;
}
