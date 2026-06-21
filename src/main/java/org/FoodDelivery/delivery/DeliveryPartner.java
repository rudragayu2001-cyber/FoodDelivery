package org.FoodDelivery.delivery;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.FoodDelivery.common.domain.BaseEntity;
import org.FoodDelivery.location.Location;
import org.FoodDelivery.user.User;

@Getter
@Setter
@Entity
@Table(name = "delivery_partners")
public class DeliveryPartner extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerStatus status = PartnerStatus.OFFLINE;

    @Version private Long version;
}
