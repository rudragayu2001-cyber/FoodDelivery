package org.FoodDelivery.customer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.FoodDelivery.common.entity.BaseEntity;
import org.FoodDelivery.menu.MenuItem;

@Getter
@Setter
@Entity
@Table(name = "cart_items")
public class CartItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(nullable = false)
    private int quantity;
}

