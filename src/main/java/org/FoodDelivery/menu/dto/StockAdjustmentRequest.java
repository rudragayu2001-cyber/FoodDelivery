package org.FoodDelivery.menu.dto;


import jakarta.validation.constraints.Min;

/** Sets an item's absolute stock level (e.g. restock at the start of the day). */
public record StockAdjustmentRequest(@Min(value = 0, message = "stockQuantity cannot be negative") int stockQuantity) {}
