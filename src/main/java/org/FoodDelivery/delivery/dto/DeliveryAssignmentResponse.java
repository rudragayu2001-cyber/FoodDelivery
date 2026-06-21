package org.FoodDelivery.delivery.dto;

import org.FoodDelivery.delivery.DeliveryAssignment;
import org.FoodDelivery.delivery.DeliveryPartner;
import java.time.Instant;

public record DeliveryAssignmentResponse(
        Long id,
        Long orderId,
        Long partnerUserId,
        String partnerName,
        String status,
        Instant assignedAt,
        Instant pickedUpAt,
        Instant deliveredAt) {

    public static DeliveryAssignmentResponse from(DeliveryAssignment a) {
        DeliveryPartner partner = a.getDeliveryPartner();
        return new DeliveryAssignmentResponse(
                a.getId(),
                a.getOrder().getId(),
                partner == null ? null : partner.getUser().getId(),
                partner == null ? null : partner.getUser().getFullName(),
                a.getStatus().name(),
                a.getAssignedAt(),
                a.getPickedUpAt(),
                a.getDeliveredAt());
    }
}
