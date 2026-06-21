package org.FoodDelivery.payment.dto;

import org.FoodDelivery.payment.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(@NotNull PaymentMethod method) {}
