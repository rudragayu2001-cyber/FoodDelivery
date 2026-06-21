package org.FoodDelivery.location.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LocationRequest(
        @NotBlank String city,
        @NotBlank String state,
        boolean serviceable,
        @NotNull @DecimalMin(value = "0.0", message = "deliveryCharge cannot be negative")
        BigDecimal deliveryCharge) {}