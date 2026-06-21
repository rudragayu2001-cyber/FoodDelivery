package org.FoodDelivery.restaurant.dto;

import org.FoodDelivery.restaurant.Restaurant;

public record RestaurantResponse(
        Long id,
        String name,
        String address,
        Long locationId,
        String city,
        Long ownerId,
        boolean active) {

    public static RestaurantResponse from(Restaurant r) {
        return new RestaurantResponse(
                r.getId(),
                r.getName(),
                r.getAddress(),
                r.getLocation().getId(),
                r.getLocation().getCity(),
                r.getOwner().getId(),
                r.isActive());
    }
}

