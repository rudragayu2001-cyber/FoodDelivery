package org.FoodDelivery.location;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.FoodDelivery.common.domain.BaseEntity;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "locations")
public class Location extends BaseEntity {

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    /** When false the area is not serviceable: no browsing/ordering allowed there. */
    @Column(nullable = false)
    private boolean serviceable = true;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal deliveryCharge = BigDecimal.ZERO;
}

