package org.FoodDelivery.menu.dto;

import org.FoodDelivery.menu.MenuItem;

import java.math.BigDecimal;

public record MenuItemResponse(
        Long id,
        Long restaurantId,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        boolean available) {

    public static MenuItemResponse from(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getRestaurant().getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getStockQuantity(),
                item.isAvailable());
    }
}

