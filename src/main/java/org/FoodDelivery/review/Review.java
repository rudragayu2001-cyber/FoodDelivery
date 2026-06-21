package org.FoodDelivery.review;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.FoodDelivery.common.entity.BaseEntity;
import org.FoodDelivery.customer.Customer;
import org.FoodDelivery.order.Order;

@Getter
@Setter
@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private int restaurantRating;

    @Column(length = 1000)
    private String restaurantComment;

    @Column(nullable = false)
    private int deliveryRating;

    @Column(length = 1000)
    private String deliveryComment;
}

