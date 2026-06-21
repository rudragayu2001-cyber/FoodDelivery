package org.FoodDelivery.payment.dto;

import org.FoodDelivery.payment.Payment;
import org.FoodDelivery.payment.PaymentMethod;
import org.FoodDelivery.payment.PaymentStatus;
import java.math.BigDecimal;

public record PaymentResponse(
        Long id,
        Long orderId,
        BigDecimal amount,
        PaymentMethod method,
        PaymentStatus status,
        String transactionRef) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getTransactionRef());
    }
}
