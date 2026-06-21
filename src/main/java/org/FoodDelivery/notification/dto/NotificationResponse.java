package org.FoodDelivery.notification.dto;

import org.FoodDelivery.notification.Notification;
import java.time.Instant;

public record NotificationResponse(
        Long id, String recipientRole, Long orderId, String message, boolean read, Instant createdAt) {

    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getRecipientRole(), n.getOrderId(), n.getMessage(), n.isRead(), n.getCreatedAt());
    }
}
