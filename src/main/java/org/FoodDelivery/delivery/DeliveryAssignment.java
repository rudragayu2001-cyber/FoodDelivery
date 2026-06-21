package org.FoodDelivery.delivery;

import jakarta.persistence.*;
import org.FoodDelivery.common.entity.BaseEntity;
import org.FoodDelivery.order.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Immutable record of which partner won an order assignment. Exactly one row per
 * order (enforced by the unique order association).
 */
@Getter
@Setter
@Entity
@Table(name = "delivery_assignments")
public class DeliveryAssignment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_partner_id")
    private DeliveryPartner deliveryPartner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status = AssignmentStatus.OFFERED;

    private Instant assignedAt;
    private Instant pickedUpAt;
    private Instant deliveredAt;

    @Version private Long version;
}
