package org.example.delivery;

import org.example.common.domain.BaseEntity;
import org.example.order.Order;
import org.example.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Immutable record of which partner won an order assignment. Exactly one row per
 * order (enforced by the unique order association).
 */
@Entity
@Table(name = "delivery_assignments")
@Getter
@Setter
@NoArgsConstructor
public class DeliveryAssignment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partner_id", nullable = false)
    private User partner;

    private Instant acceptedAt;
}
