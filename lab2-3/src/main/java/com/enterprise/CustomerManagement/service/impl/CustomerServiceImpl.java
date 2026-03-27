package com.enterprise.CustomerManagement.service.impl;

import com.enterprise.CustomerManagement.jms.NotificationProducer;
import com.enterprise.CustomerManagement.model.dto.request.CreateCustomerRequest;
import com.enterprise.CustomerManagement.model.dto.request.UpdateCustomerRequest;
import com.enterprise.CustomerManagement.model.entity.Customer;
import com.enterprise.CustomerManagement.repository.CustomerRepository;
import com.enterprise.CustomerManagement.service.CustomerService;
import com.enterprise.CustomerManagement.spec.CustomerSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final NotificationProducer notificationProducer;

    @Override
    @CacheEvict(value = {"customers", "customersList"}, allEntries = true)
    @Transactional(
            rollbackOn = Exception.class
    )
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

        Customer saved = this.customerRepository.save(customer);

        // Асинхронно отправляем приветственное письмо через JMS очередь
        log.info("Отправка приветственного письма для клиента ID: {}", saved.getId());
        notificationProducer.sendWelcomeEmail(
                saved.getId(),
                saved.getEmail(),
                saved.getFirstName()
        );

        log.info("Клиент создан с ID: {}", saved.getId());
        return saved;
    }

    @Override
    @CacheEvict(value = {"customer", "customers", "customersList"}, allEntries = true)
    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("find_customer_error"));
        customerRepository.delete(customer);
        log.info("Клиент удалён с ID: {}", id);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"customer", "customers", "customersList"}, allEntries = true)
    public void update(Long id, UpdateCustomerRequest request) {
        this.customerRepository.findById(id)
                .ifPresentOrElse(newCustomer -> {
                    newCustomer.setFirstName(request.firstName());
                    newCustomer.setLastName(request.lastName());
                    newCustomer.setEmail(request.email());
                    log.info("Клиент обновлён с ID: {}", id);
                }, () -> {
                    throw new NoSuchElementException("find_customer_error");
                });
    }

    @Override
    @Cacheable(value = "customer", key = "#id")
    public Customer getCustomerById(Long id) {
        log.info("Поиск клиента по ID: {}", id);
        return this.customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("find_customer_error"));
    }

    @Override
    @Cacheable(value = "customersList")
    public List<Customer> getListCustomers() {
        return this.customerRepository.findAll();
    }

    @Override
    @Cacheable(value = "customers", key = "{#firstName, #lastName, #email, #pageable.pageNumber, #pageable.pageSize, #pageable.sort}")
    public Page<Customer> getCustomers(String firstName, String lastName,
                                       String email, Pageable pageable) {
        log.info("Получение списка клиентов: страница {}, размер {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Specification<Customer> spec = Specification
                .where(CustomerSpecification.hasFirstName(firstName))
                .and(CustomerSpecification.hasLastName(lastName))
                .and(CustomerSpecification.hasEmail(email));

        return customerRepository.findAll(spec, pageable);
    }
}