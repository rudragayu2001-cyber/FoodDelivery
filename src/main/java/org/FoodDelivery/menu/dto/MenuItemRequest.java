package org.FoodDelivery.menu.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MenuItemRequest(
        @NotBlank String name,
        String description,
        @NotNull @DecimalMin(value = "0.0", inclusive = false, message = "price must be positive")
        BigDecimal price,
        @Min(value = 0, message = "stockQuantity cannot be negative") int stockQuantity,
        boolean available) {}
