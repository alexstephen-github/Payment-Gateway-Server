package com.paygateway.mock.controller;

import com.paygateway.mock.dto.CreateCustomerRequest;
import com.paygateway.mock.dto.UpdateCustomerRequest;
import com.paygateway.mock.model.Customer;
import com.paygateway.mock.model.CustomerList;
import com.paygateway.mock.support.MockFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments/customers")
@Tag(name = "Customers", description = "Customer profile management")
public class CustomerController {

    private final MockFactory factory;

    public CustomerController(MockFactory factory) {
        this.factory = factory;
    }

    @Operation(summary = "Create a customer",
               description = "Creates a new customer profile with the provided details. Optionally attach a payment method on creation.")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Customer> create(@RequestBody(required = false) CreateCustomerRequest request) {
        Map<String, Object> body = request != null ? request.toMap() : null;
        Customer customer = factory.customer(body);
        return ResponseEntity.created(URI.create("/payments/customers/" + customer.id)).body(customer);
    }

    @Operation(summary = "List customers",
               description = "Returns a paginated list of customers. Optionally filter by email address.")
    @GetMapping
    public CustomerList list(@RequestParam(name = "email", required = false) String email,
                             @RequestParam(name = "limit", defaultValue = "20") int limit) {
        Map<String, Object> filters = new HashMap<>();
        if (email != null) {
            filters.put("email", email);
        }
        return factory.customerList(limit, filters);
    }

    @Operation(summary = "Retrieve a customer",
               description = "Retrieves the details of an existing customer by their unique identifier.")
    @GetMapping("/{customerId}")
    public Customer get(@PathVariable String customerId) {
        Customer customer = factory.customer(null);
        customer.id = customerId;
        return customer;
    }

    @Operation(summary = "Update a customer",
               description = "Updates the specified customer by setting the values of the parameters passed. Any parameters not provided will be left unchanged.")
    @PutMapping(value = "/{customerId}", consumes = "application/json")
    public Customer update(@PathVariable String customerId,
                           @RequestBody(required = false) UpdateCustomerRequest request) {
        Map<String, Object> body = request != null ? request.toMap() : null;
        Customer customer = factory.customer(body);
        customer.id = customerId;
        return customer;
    }

    @Operation(summary = "Delete a customer",
               description = "Permanently deletes a customer and their associated data. This action cannot be undone.")
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> delete(@PathVariable String customerId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
