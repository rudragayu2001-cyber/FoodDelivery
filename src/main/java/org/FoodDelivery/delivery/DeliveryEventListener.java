package org.FoodDelivery.delivery;

import org.FoodDelivery.order.event.OrderAcceptedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** Starts delivery-partner assignment once an order has been accepted (after commit). */
@Component
@RequiredArgsConstructor
public class DeliveryEventListener {

    private final DeliveryService deliveryService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderAccepted(OrderAcceptedEvent event) {
        deliveryService.createOfferForOrder(event.orderId());
    }
}
