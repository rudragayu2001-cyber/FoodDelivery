package org.FoodDelivery.customer.dto;

import org.FoodDelivery.customer.CartItem;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long restaurantId, String restaurantName, List<Line> items, BigDecimal itemTotal) {

    public record Line(
            Long menuItemId, String name, BigDecimal unitPrice, int quantity, BigDecimal lineTotal) {}

    public static CartResponse from(List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            return new CartResponse(null, null, List.of(), BigDecimal.ZERO);
        }
        var restaurant = cartItems.get(0).getMenuItem().getRestaurant();
        List<Line> lines = cartItems.stream()
                .map(ci -> {
                    BigDecimal unit = ci.getMenuItem().getPrice();
                    BigDecimal lineTotal = unit.multiply(BigDecimal.valueOf(ci.getQuantity()));
                    return new Line(
                            ci.getMenuItem().getId(),
                            ci.getMenuItem().getName(),
                            unit,
                            ci.getQuantity(),
                            lineTotal);
                })
                .toList();
        BigDecimal total = lines.stream().map(Line::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(restaurant.getId(), restaurant.getName(), lines, total);
    }
}