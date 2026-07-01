package com.paygateway.mock.model;

import java.time.Instant;
import java.util.Map;

public class Payment {
    public String id;
    public String object = "payment";
    public Integer amount;
    public Integer amountCapturable;
    public Integer amountReceived;
    public String currency;
    public String status;
    public String captureMethod;
    public String customerId;
    public String paymentMethodId;
    public PaymentMethodDetails paymentMethodDetails;
    public String description;
    public String statementDescriptor;
    public String receiptEmail;
    public String receiptUrl;
    public String failureCode;
    public String failureMessage;
    public NextAction nextAction;
    public RefundList refunds;
    public String balanceTransaction;
    public String onBehalfOf;
    public Integer applicationFeeAmount;
    public Integer riskScore;
    public Map<String, String> metadata;
    public Instant createdAt;
    public Instant updatedAt;
}
