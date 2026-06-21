package org.FoodDelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RestaurantRequest(
        @NotBlank String name,
        @NotBlank String address,
        @NotNull Long locationId,
        @NotNull Long ownerId) {}

