package org.FoodDelivery.customer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemRequest(
        @NotNull Long menuItemId, @Min(value = 1, message = "quantity must be at least 1") int quantity) {}
