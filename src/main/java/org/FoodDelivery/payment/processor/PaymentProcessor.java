package org.FoodDelivery.payment.processor;

import org.FoodDelivery.payment.PaymentMethod;
import java.math.BigDecimal;

/**
 * Strategy for charging via a specific {@link PaymentMethod}.
 *
 * <p>Adding a new method = adding a new bean implementing this interface; nothing else changes
 * (OCP). The {@code PaymentService} selects the right strategy at runtime via the registry.
 */
public interface PaymentProcessor {

    PaymentMethod method();

    /** Whether the charge is captured up-front. Cash-on-delivery defers collection to delivery. */
    default boolean capturedUpfront() {
        return true;
    }

    PaymentResult charge(Long orderId, BigDecimal amount);
}
