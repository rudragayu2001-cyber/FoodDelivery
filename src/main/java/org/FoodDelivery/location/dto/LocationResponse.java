package org.FoodDelivery.location.dto;

import org.FoodDelivery.location.Location;

import java.math.BigDecimal;

public record LocationResponse(
        Long id, String city, String state, boolean serviceable, BigDecimal deliveryCharge) {

    public static LocationResponse from(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getCity(),
                location.getState(),
                location.isServiceable(),
                location.getDeliveryCharge());
    }
}