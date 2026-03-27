package com.enterprise.CustomerManagement.spec;

import com.enterprise.CustomerManagement.model.entity.Customer;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecification {

    public static Specification<Customer> hasFirstName(String firstName) {
        return (root, query, cb) -> firstName == null ? null :
                cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<Customer> hasLastName(String lastName) {
        return (root, query, cb) -> lastName == null ? null :
                cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<Customer> hasEmail(String email) {
        return (root, query, cb) -> email == null ? null :
                cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }
}