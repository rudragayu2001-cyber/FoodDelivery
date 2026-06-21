package org.FoodDelivery.review.dto;

import org.FoodDelivery.review.Review;

import java.time.Instant;

public record ReviewResponse(
        Long id,
        Long orderId,
        Long restaurantId,
        int restaurantRating,
        String restaurantComment,
        int deliveryRating,
        String deliveryComment,
        Instant createdAt) {

    public static ReviewResponse from(Review r) {
        return new ReviewResponse(
                r.getId(),
                r.getOrder().getId(),
                r.getOrder().getRestaurant().getId(),
                r.getRestaurantRating(),
                r.getRestaurantComment(),
                r.getDeliveryRating(),
                r.getDeliveryComment(),
                r.getCreatedAt());
    }
}

