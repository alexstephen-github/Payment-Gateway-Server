package com.paygateway.mock.model;

import java.time.Instant;
import java.util.Map;

public class Customer {
    public String id;
    public String object = "customer";
    public String email;
    public String name;
    public String phone;
    public String description;
    public Address address;
    public String defaultPaymentMethodId;
    public Integer balance;
    public String currency;
    public Boolean delinquent;
    public String taxExempt;
    public Map<String, String> metadata;
    public Instant createdAt;
}
