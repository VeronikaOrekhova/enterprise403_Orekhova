package com.enterprise.CustomerManagement.model.dto.response;

import java.time.LocalDateTime;

public record CustomerResponse (
        Long id,
        String firstName,
        String lastName,
        String email,
        LocalDateTime createdAt
){
}
