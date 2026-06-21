package org.FoodDelivery.notification.event;

import org.FoodDelivery.order.OrderStatus;
import java.util.List;

/**
 * Domain event published whenever something noteworthy happens to an order (status change,
 * delivery partner assigned, etc.). Carries everything a listener needs so it never has to reach
 * back into other modules' internals (keeps the notification module decoupled — DIP).
 */
public record OrderEvent(Long orderId, OrderStatus status, String summary, List<Recipient> recipients) {

    /** A single user who should hear about this event, tagged with the role they hold here. */
    public record Recipient(Long userId, String role) {}

    public static Recipient recipient(Long userId, String role) {
        return new Recipient(userId, role);
    }
}
