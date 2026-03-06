package com.enterprise.CustomerManagement.controller;

import com.enterprise.CustomerManagement.model.dto.request.CreateCustomerRequest;
import com.enterprise.CustomerManagement.model.dto.request.UpdateCustomerRequest;
import com.enterprise.CustomerManagement.model.dto.response.CustomerResponse;
import com.enterprise.CustomerManagement.model.entity.Customer;
import com.enterprise.CustomerManagement.model.mapper.CustomerMapper;
import com.enterprise.CustomerManagement.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.BindException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerMapper customerMapper;
    @GetMapping("{customerId:\\d+}")
    public ResponseEntity<?> getCustomerById(@PathVariable("customerId") Long customerId) {
        Customer customer = this.customerService.getCustomerById(customerId);
        CustomerResponse response = this.customerMapper.toCustomerResponse(customer);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<?> getCustomers() {
        List<CustomerResponse> response = this.customerService.getListCustomers().stream().map(customerMapper::toCustomerResponse).toList();
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CreateCustomerRequest request, UriComponentsBuilder uriComponentsBuilder) throws BindException {
        Customer customer = this.customerService.create(request);
        CustomerResponse response = this.customerMapper.toCustomerResponse(customer);
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("/api/customers/{customerId}")
                        .build(Map.of("customerId", customer.getId())))
                .body(response);
    }
    @PutMapping("{customerId:\\d+}")
    public ResponseEntity<?> updateCustomer(@PathVariable("customerId") Long customerId, @Valid @RequestBody UpdateCustomerRequest request) {
        this.customerService.update(customerId, request);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("{customerId:\\d+}")
    public ResponseEntity<?> deleteCustomer(@PathVariable("customerId") Long customerId) {
        this.customerService.delete(customerId);
        return ResponseEntity.noContent().build();
    }
}
