package com.enterprise.CustomerManagement.service;

import com.enterprise.CustomerManagement.model.dto.request.CreateCustomerRequest;
import com.enterprise.CustomerManagement.model.dto.request.UpdateCustomerRequest;
import com.enterprise.CustomerManagement.model.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    Customer create(CreateCustomerRequest request);
    void delete(Long id);
    void update(Long id, UpdateCustomerRequest request);
    Customer getCustomerById(Long id);
    List<Customer> getListCustomers();
    Page<Customer> getCustomers(String firstName, String lastName, String email, Pageable pageable);

}
