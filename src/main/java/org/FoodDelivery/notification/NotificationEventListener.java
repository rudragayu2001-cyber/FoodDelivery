package org.FoodDelivery.notification;

import org.FoodDelivery.notification.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Fans an {@link OrderEvent} out to every recipient asynchronously.
 *
 * <p>{@code @TransactionalEventListener(AFTER_COMMIT)} guarantees the originating transaction has
 * committed (so the order/payment really happened) before anyone is notified; {@code @Async} runs
 * the fan-out on a dedicated pool so the customer/restaurant/partner flow that triggered it is
 * never blocked. A failure here is logged and swallowed — notifications must not roll back orders.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderEvent(OrderEvent event) {
        try {
            notificationService.recordFor(event);
            log.info(
                    "Notified {} recipient(s) about order {} -> {}",
                    event.recipients().size(),
                    event.orderId(),
                    event.status());
        } catch (Exception ex) {
            log.error("Failed to deliver notifications for order {}", event.orderId(), ex);
        }
    }
}
