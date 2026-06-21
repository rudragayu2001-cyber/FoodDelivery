package org.FoodDelivery.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.FoodDelivery.review.dto.ReviewRequest;
import org.FoodDelivery.review.dto.ReviewResponse;
import org.FoodDelivery.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Post-delivery ratings for food and delivery.")
public class ReviewController {

    private final ReviewService reviewService;
    private final CurrentUserService currentUserService;

    @PostMapping("/orders/{orderId}/review")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "[CUSTOMER] Review a delivered order (once)")
    public ReviewResponse submit(@PathVariable Long orderId, @Valid @RequestBody ReviewRequest request) {
        Long userId = currentUserService.requireCurrentUserId();
        return ReviewResponse.from(reviewService.submit(orderId, userId, request));
    }

    @GetMapping("/orders/{orderId}/review")
    @Operation(summary = "[ANY] Get the review for an order")
    public ReviewResponse byOrder(@PathVariable Long orderId) {
        return ReviewResponse.from(reviewService.getByOrderId(orderId));
    }

    @GetMapping("/restaurants/{restaurantId}/reviews")
    @Operation(summary = "[ANY] List reviews for a restaurant")
    public List<ReviewResponse> forRestaurant(@PathVariable Long restaurantId) {
        return reviewService.listForRestaurant(restaurantId).stream().map(ReviewResponse::from).toList();
    }
}

