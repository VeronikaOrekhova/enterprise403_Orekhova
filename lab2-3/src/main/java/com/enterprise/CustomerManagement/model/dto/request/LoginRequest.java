package com.enterprise.CustomerManagement.model.dto.request;

public record LoginRequest(
        String username,
        String password
) {}