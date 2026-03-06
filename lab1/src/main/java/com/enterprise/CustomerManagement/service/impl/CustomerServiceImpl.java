package com.enterprise.CustomerManagement.service.impl;

import com.enterprise.CustomerManagement.model.dto.request.CreateCustomerRequest;
import com.enterprise.CustomerManagement.model.dto.request.UpdateCustomerRequest;
import com.enterprise.CustomerManagement.model.entity.Customer;
import com.enterprise.CustomerManagement.repository.CustomerRepository;
import com.enterprise.CustomerManagement.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    @Override
    public Customer create(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Клиент с email '" + request.email() + "' уже существует"
            );
        }
        var customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        return this.customerRepository.save(customer);
    }

    @Override
    public void delete(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new NoSuchElementException("find_customer_error"));
        customerRepository.delete(customer);
    }

    @Override
    @Transactional
    public void update(Long id, UpdateCustomerRequest request) {
        this.customerRepository.findById(id)
                .ifPresentOrElse(newCustomer -> {
                    newCustomer.setFirstName(request.firstName());
                    newCustomer.setLastName(request.lastName());
                    newCustomer.setEmail(request.email());
                }, () -> {
                    throw new NoSuchElementException("find_customer_error");
                });
    }

    @Override
    public Customer getCustomerById(Long id) {
        return this.customerRepository.findById(id).orElseThrow(() -> new NoSuchElementException("find_customer_error"));
    }

    @Override
    public List<Customer> getListCustomers() {
        return this.customerRepository.findAll();
    }
}
