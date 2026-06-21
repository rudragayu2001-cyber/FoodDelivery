package org.FoodDelivery.order.event;

import org.FoodDelivery.order.OrderStatus;

/**
 * Raised when an order ends without fulfilment (REJECTED or CANCELLED). The payment module listens
 * to issue a refund for any successful payment, again avoiding an order → payment dependency.
 */
public record OrderTerminatedEvent(Long orderId, OrderStatus status) {}
