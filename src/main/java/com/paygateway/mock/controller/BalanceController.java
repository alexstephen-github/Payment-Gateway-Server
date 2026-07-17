package com.paygateway.mock.controller;

import com.paygateway.mock.model.Balance;
import com.paygateway.mock.model.BalanceTransaction;
import com.paygateway.mock.model.BalanceTransactionList;
import com.paygateway.mock.support.MockFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments/balance")
@Tag(name = "Balance", description = "Account balance and transaction history")
public class BalanceController {

    private final MockFactory factory;

    public BalanceController(MockFactory factory) {
        this.factory = factory;
    }

    @Operation(summary = "Retrieve current account balance",
               description = "Returns the current account balance broken down by available, pending, and reserved funds across all currencies.")
    @GetMapping
    public Balance get() {
        return factory.balance();
    }

    @Operation(summary = "List balance transactions",
               description = "Returns a paginated list of balance transactions. Optionally filter by transaction type or currency.")
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

    @Operation(summary = "Retrieve a balance transaction",
               description = "Retrieves the details of a specific balance transaction by its unique identifier.")
    @GetMapping("/transactions/{txnId}")
    public BalanceTransaction getTransaction(@PathVariable String txnId) {
        return factory.balanceTransaction(txnId, null);
    }
}
