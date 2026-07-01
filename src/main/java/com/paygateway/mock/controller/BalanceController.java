package com.paygateway.mock.controller;

import com.paygateway.mock.model.Balance;
import com.paygateway.mock.model.BalanceTransaction;
import com.paygateway.mock.model.BalanceTransactionList;
import com.paygateway.mock.support.MockFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/balance")
public class BalanceController {

    private final MockFactory factory;

    public BalanceController(MockFactory factory) {
        this.factory = factory;
    }

    @GetMapping
    public Balance get() {
        return factory.balance();
    }

    @GetMapping("/transactions")
    public BalanceTransactionList listTransactions(@RequestParam(name = "type", required = false) String type,
                                                   @RequestParam(name = "currency", required = false) String currency,
                                                   @RequestParam(name = "limit", defaultValue = "20") int limit) {
        Map<String, Object> filters = new HashMap<>();
        if (type != null) {
            filters.put("type", type);
        }
        if (currency != null) {
            filters.put("currency", currency);
        }
        return factory.balanceTransactionList(limit, filters);
    }

    @GetMapping("/transactions/{txnId}")
    public BalanceTransaction getTransaction(@PathVariable String txnId) {
        return factory.balanceTransaction(txnId, null);
    }
}
