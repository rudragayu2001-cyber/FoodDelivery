package org.FoodDelivery.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.FoodDelivery.user.Role;

/** Admin-only: create RESTAURANT_OWNER / ADMIN (or any role) accounts. */
public record CreateUserRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 6, message = "password must be at least 6 characters") String password,
        @NotBlank String fullName,
        @NotBlank @Email String email,
        String phone,
        @NotNull Role role) {}

