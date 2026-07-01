package com.paygateway.mock.model;

import java.util.List;

public class Balance {
    public String object = "balance";
    public List<BalanceFund> available;
    public List<BalanceFund> pending;
    public List<BalanceFund> reserved;
}
