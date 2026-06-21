package org.FoodDelivery.payment.processor;

import org.FoodDelivery.payment.PaymentMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Cash on delivery: nothing is captured up-front, the order is allowed to proceed and cash is
 * collected by the partner on delivery.
 */
@Component
public class CashOnDeliveryProcessor implements PaymentProcessor {

    @Override
    public PaymentMethod method() {
        return PaymentMethod.CASH_ON_DELIVERY;
    }

    @Override
    public boolean capturedUpfront() {
        return false;
    }

    @Override
    public PaymentResult charge(Long orderId, BigDecimal amount) {
        return PaymentResult.ok("COD-" + UUID.randomUUID());
    }
}
