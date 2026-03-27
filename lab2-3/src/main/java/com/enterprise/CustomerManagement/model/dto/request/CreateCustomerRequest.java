package com.enterprise.CustomerManagement.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest (
        @NotNull()
        @Size(min = 1, max = 30)
        @Pattern(regexp = "^[A-Za-zА-Яа-яЁё-]+$")
        String firstName,
        @NotNull()
        @Size(min = 1, max = 30)
        @Pattern(regexp = "^[A-Za-zА-Яа-яЁё-]+$")
        String lastName,
        @NotNull()
        String email
) {
}
