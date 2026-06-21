package org.FoodDelivery.order.dto;

import org.FoodDelivery.order.Order;
import org.FoodDelivery.order.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Long customerId,
        Long restaurantId,
        String restaurantName,
        OrderStatus status,
        List<OrderItemResponse> items,
        BigDecimal itemTotal,
        BigDecimal deliveryCharge,
        BigDecimal grandTotal,
        Integer estimatedCookTimeMinutes,
        Instant createdAt,
        Instant updatedAt) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomer().getId(),
                order.getRestaurant().getId(),
                order.getRestaurant().getName(),
                order.getStatus(),
                order.getItems().stream().map(OrderItemResponse::from).toList(),
                order.getItemTotal(),
                order.getDeliveryCharge(),
                order.getGrandTotal(),
                order.getEstimatedCookTimeMinutes(),
                order.getCreatedAt(),
                order.getUpdatedAt());
    }
}

