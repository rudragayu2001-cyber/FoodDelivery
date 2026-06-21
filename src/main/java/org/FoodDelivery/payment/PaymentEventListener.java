package org.FoodDelivery.payment;

import org.FoodDelivery.order.event.OrderTerminatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** Triggers refunds when an order is cancelled or rejected, decoupled from the order module. */
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentService paymentService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderTerminated(OrderTerminatedEvent event) {
        paymentService.refundForTerminatedOrder(event.orderId());
    }
}
