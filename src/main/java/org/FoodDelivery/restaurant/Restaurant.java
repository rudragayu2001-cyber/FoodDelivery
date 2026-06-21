package org.FoodDelivery.restaurant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.FoodDelivery.common.entity.BaseEntity;
import org.FoodDelivery.location.Location;
import org.FoodDelivery.user.User;

@Getter
@Setter
@Entity
@Table(name = "restaurants")
public class Restaurant extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private boolean active = true;
}
