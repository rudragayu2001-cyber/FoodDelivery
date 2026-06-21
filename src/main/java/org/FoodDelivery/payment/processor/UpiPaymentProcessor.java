package org.FoodDelivery.payment.processor;

import org.FoodDelivery.payment.PaymentMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/** Simulated UPI gateway. */
@Component
public class UpiPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentMethod method() {
        return PaymentMethod.UPI;
    }

    @Override
    public PaymentResult charge(Long orderId, BigDecimal amount) {
        return PaymentResult.ok("UPI-" + UUID.randomUUID());
    }
}
