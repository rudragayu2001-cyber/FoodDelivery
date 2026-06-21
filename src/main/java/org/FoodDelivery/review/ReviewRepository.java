package org.FoodDelivery.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByOrderId(Long orderId);

    Optional<Review> findByOrderId(Long orderId);

    @Query("SELECT r FROM Review r WHERE r.order.restaurant.id = :restaurantId ORDER BY r.createdAt DESC")
    List<Review> findByRestaurantId(@Param("restaurantId") Long restaurantId);
}