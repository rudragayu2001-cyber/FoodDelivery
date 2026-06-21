package org.FoodDelivery.order.dto;

import org.FoodDelivery.order.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long menuItemId, String itemName, BigDecimal unitPrice, int quantity, BigDecimal lineTotal) {

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getMenuItem().getId(),
                item.getItemName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getLineTotal());
    }
}
