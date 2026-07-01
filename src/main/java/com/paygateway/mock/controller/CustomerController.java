package com.paygateway.mock.controller;

import com.paygateway.mock.model.Customer;
import com.paygateway.mock.model.CustomerList;
import com.paygateway.mock.support.MockFactory;
import com.paygateway.mock.support.Req;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final MockFactory factory;

    public CustomerController(MockFactory factory) {
        this.factory = factory;
    }

    @PostMapping
    public ResponseEntity<Customer> create(@RequestBody(required = false) String raw) {
        Customer customer = factory.customer(Req.asMap(raw));
        return ResponseEntity.created(URI.create("/v1/customers/" + customer.id)).body(customer);
    }

    @GetMapping
    public CustomerList list(@RequestParam(name = "email", required = false) String email,
                             @RequestParam(name = "limit", defaultValue = "20") int limit) {
        Map<String, Object> filters = new HashMap<>();
        if (email != null) {
            filters.put("email", email);
        }
        return factory.customerList(limit, filters);
    }

    @GetMapping("/{customerId}")
    public Customer get(@PathVariable String customerId) {
        Customer customer = factory.customer(null);
        customer.id = customerId;
        return customer;
    }

    @PutMapping("/{customerId}")
    public Customer update(@PathVariable String customerId,
                           @RequestBody(required = false) String raw) {
        Customer customer = factory.customer(Req.asMap(raw));
        customer.id = customerId;
        return customer;
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> delete(@PathVariable String customerId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
