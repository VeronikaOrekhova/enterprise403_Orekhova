package com.enterprise.CustomerManagement.controller;

import com.enterprise.CustomerManagement.model.dto.request.CreateCustomerRequest;
import com.enterprise.CustomerManagement.model.dto.request.UpdateCustomerRequest;
import com.enterprise.CustomerManagement.model.dto.response.CustomerResponse;
import com.enterprise.CustomerManagement.model.dto.response.PageResponse;
import com.enterprise.CustomerManagement.model.entity.Customer;
import com.enterprise.CustomerManagement.model.mapper.CustomerMapper;
import com.enterprise.CustomerManagement.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.BindException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("{customerId:\\d+}")
    public ResponseEntity<?> getCustomerById(@PathVariable("customerId") Long customerId) {
        Customer customer = this.customerService.getCustomerById(customerId);
        CustomerResponse response = this.customerMapper.toCustomerResponse(customer);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/simple")
    public ResponseEntity<?> getCustomers() {
        List<CustomerResponse> response = this.customerService.getListCustomers().stream().map(customerMapper::toCustomerResponse).toList();
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{customerId:\\d+}")
    public ResponseEntity<?> updateCustomer(@PathVariable("customerId") Long customerId, @Valid @RequestBody UpdateCustomerRequest request) {
        this.customerService.update(customerId, request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{customerId:\\d+}")
    public ResponseEntity<?> deleteCustomer(@PathVariable("customerId") Long customerId) {
        this.customerService.delete(customerId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<CustomerResponse>> getCustomers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,

            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Page<CustomerResponse> resultPage = customerService
                .getCustomers(firstName, lastName, email, pageable)
                .map(customerMapper::toCustomerResponse);

        return ResponseEntity.ok(PageResponse.of(resultPage));
    }
}
