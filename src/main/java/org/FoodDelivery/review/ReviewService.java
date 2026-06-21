package org.FoodDelivery.review;

import lombok.RequiredArgsConstructor;
import org.FoodDelivery.common.exception.BusinessRuleException;
import org.FoodDelivery.common.exception.ResourceNotFoundException;
import org.FoodDelivery.customer.Customer;
import org.FoodDelivery.customer.CustomerService;
import org.FoodDelivery.order.Order;
import org.FoodDelivery.review.dto.ReviewRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderService orderService;
    private final CustomerService customerService;

    @Transactional
    public Review submit(Long orderId, Long customerUserId, ReviewRequest request) {
        Order order = orderService.getById(orderId);
        Customer customer = customerService.getByUserId(customerUserId);

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessRuleException("You can only review your own orders");
        }
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessRuleException("Only delivered orders can be reviewed");
        }
        if (reviewRepository.existsByOrderId(orderId)) {
            throw new BusinessRuleException("This order has already been reviewed");
        }

        Review review = new Review();
        review.setOrder(order);
        review.setCustomer(customer);
        review.setRestaurantRating(request.restaurantRating());
        review.setRestaurantComment(request.restaurantComment());
        review.setDeliveryRating(request.deliveryRating());
        review.setDeliveryComment(request.deliveryComment());
        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public Review getByOrderId(Long orderId) {
        return reviewRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No review for order " + orderId));
    }

    @Transactional(readOnly = true)
    public List<Review> listForRestaurant(Long restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId);
    }
}

