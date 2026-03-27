package com.enterprise.CustomerManagement.model.mapper;

import com.enterprise.CustomerManagement.model.dto.response.CustomerResponse;
import com.enterprise.CustomerManagement.model.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public CustomerResponse toCustomerResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getCreatedAt()
        );
    }
}
