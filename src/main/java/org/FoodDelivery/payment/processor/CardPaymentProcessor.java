package org.FoodDelivery.payment.processor;

import org.FoodDelivery.payment.PaymentMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/** Simulated card gateway: always authorises in this take-home. */
@Component
public class CardPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentMethod method() {
        return PaymentMethod.CARD;
    }

    @Override
    public PaymentResult charge(Long orderId, BigDecimal amount) {
        return PaymentResult.ok("CARD-" + UUID.randomUUID());
    }
}
