package org.FoodDelivery.order.event;

/**
 * Raised when a restaurant accepts an order. The delivery module listens for this (after commit)
 * to begin offering the order to available delivery partners — keeping order and delivery modules
 * decoupled (no direct call from order → delivery).
 */
public record OrderAcceptedEvent(Long orderId) {}
