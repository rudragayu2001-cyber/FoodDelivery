package org.FoodDelivery.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterDeliveryPartnerRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 6, message = "password must be at least 6 characters") String password,
        @NotBlank String fullName,
        @NotBlank @Email String email,
        String phone,
        @NotNull Long locationId) {}
