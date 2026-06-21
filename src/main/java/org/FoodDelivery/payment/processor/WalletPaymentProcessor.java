package org.FoodDelivery.payment.processor;

import org.FoodDelivery.payment.PaymentMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/** Simulated in-app wallet. */
@Component
public class WalletPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentMethod method() {
        return PaymentMethod.WALLET;
    }

    @Override
    public PaymentResult charge(Long orderId, BigDecimal amount) {
        return PaymentResult.ok("WALLET-" + UUID.randomUUID());
    }
}
