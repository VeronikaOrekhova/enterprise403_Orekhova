package com.enterprise.CustomerManagement.controller;

import com.enterprise.CustomerManagement.model.dto.request.LoginRequest;
import com.enterprise.CustomerManagement.model.dto.request.RegisterRequest;
import com.enterprise.CustomerManagement.model.dto.response.TokenResponse;
import com.enterprise.CustomerManagement.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakService keycloakService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        keycloakService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь успешно зарегистрирован");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse token = keycloakService.login(request);
        return ResponseEntity.ok(token);
    }
}