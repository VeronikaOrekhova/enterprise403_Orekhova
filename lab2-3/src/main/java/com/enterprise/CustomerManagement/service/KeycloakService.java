package com.enterprise.CustomerManagement.service;

import com.enterprise.CustomerManagement.model.dto.request.LoginRequest;
import com.enterprise.CustomerManagement.model.dto.request.RegisterRequest;
import com.enterprise.CustomerManagement.model.dto.response.TokenResponse;

public interface KeycloakService {
    public TokenResponse login(LoginRequest request);
    public void register(RegisterRequest request);
}
