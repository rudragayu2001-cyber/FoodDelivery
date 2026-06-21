package org.FoodDelivery.payment.processor;

/** Outcome of a (simulated) charge attempt. */
public record PaymentResult(boolean success, String transactionRef, String failureReason) {

    public static PaymentResult ok(String transactionRef) {
        return new PaymentResult(true, transactionRef, null);
    }

    public static PaymentResult failed(String reason) {
        return new PaymentResult(false, null, reason);
    }
}
