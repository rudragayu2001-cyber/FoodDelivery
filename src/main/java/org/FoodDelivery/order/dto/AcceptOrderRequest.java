package org.FoodDelivery.order.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record AcceptOrderRequest(
        @Min(value = 1, message = "estimatedCookTimeMinutes must be at least 1")
        @Max(value = 240, message = "estimatedCookTimeMinutes is unrealistically large")
        int estimatedCookTimeMinutes) {}
