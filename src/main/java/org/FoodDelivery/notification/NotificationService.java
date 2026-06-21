package org.FoodDelivery.notification;

import org.FoodDelivery.common.exception.BusinessRuleException;
import org.FoodDelivery.notification.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void recordFor(OrderEvent event) {
        for (OrderEvent.Recipient recipient : event.recipients()) {
            Notification notification = new Notification();
            notification.setRecipientUserId(recipient.userId());
            notification.setRecipientRole(recipient.role());
            notification.setOrderId(event.orderId());
            notification.setMessage(event.summary());
            notificationRepository.save(notification);
        }
    }

    @Transactional(readOnly = true)
    public List<Notification> listFor(Long userId, boolean unreadOnly) {
        return unreadOnly
                ? notificationRepository.findByRecipientUserIdAndReadFalseOrderByCreatedAtDesc(userId)
                : notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new BusinessRuleException("Notification not found"));
        if (!notification.getRecipientUserId().equals(userId)) {
            throw new BusinessRuleException("Notification does not belong to you");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
