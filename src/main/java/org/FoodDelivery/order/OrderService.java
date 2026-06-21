package org.FoodDelivery.order;

import lombok.RequiredArgsConstructor;
import org.FoodDelivery.common.exception.BusinessRuleException;
import org.FoodDelivery.common.exception.ConflictException;
import org.FoodDelivery.common.exception.InvalidStateTransitionException;
import org.FoodDelivery.common.exception.ResourceNotFoundException;
import org.FoodDelivery.customer.CartItem;
import org.FoodDelivery.customer.CartService;
import org.FoodDelivery.customer.Customer;
import org.FoodDelivery.customer.CustomerService;
import org.FoodDelivery.menu.MenuItem;
import org.FoodDelivery.menu.MenuItemRepository;
import org.FoodDelivery.order.event.OrderAcceptedEvent;
import org.FoodDelivery.order.event.OrderTerminatedEvent;
import org.FoodDelivery.restaurant.Restaurant;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Owns the order lifecycle and the transactional rules around placement.
 *
 * <p>The legal state graph lives in {@link OrderStatus}; this service only <em>applies</em>
 * transitions and the side effects each one implies (reserving/returning stock, publishing events).
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private static final String ROLE_CUSTOMER = "CUSTOMER";
    private static final String ROLE_RESTAURANT = "RESTAURANT_OWNER";
    private static final String ROLE_PARTNER = "DELIVERY_PARTNER";

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final CartService cartService;
    private final CustomerService customerService;
    private final ApplicationEventPublisher events;

    /**
     * Atomically place an order from the customer's cart.
     *
     * <p>Within a single transaction we snapshot each line, reserve stock with a conditional atomic
     * decrement, compute totals (delivery charge follows the restaurant's location), and persist the
     * order in {@code PENDING_PAYMENT}. If any item lacks stock the whole transaction rolls back, so
     * no partial reservation can leak (no oversell).
     */
    @Transactional
    public Order checkout(Long userId) {
        Customer customer = customerService.getByUserId(userId);
        List<CartItem> cartItems = cartService.view(userId);
        if (cartItems.isEmpty()) {
            throw new BusinessRuleException("Cart is empty");
        }

        Restaurant restaurant = cartItems.get(0).getMenuItem().getRestaurant();
        if (!restaurant.isActive()) {
            throw new BusinessRuleException("Restaurant is not currently accepting orders");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        for (CartItem cartItem : cartItems) {
            MenuItem menuItem = cartItem.getMenuItem();
            int qty = cartItem.getQuantity();

            int updated = menuItemRepository.decrementStock(menuItem.getId(), qty);
            if (updated == 0) {
                // Either unavailable or not enough stock — fail the whole placement.
                throw new ConflictException(
                        "Insufficient stock for \"" + menuItem.getName() + "\". Please adjust your cart.");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setItemName(menuItem.getName());
            orderItem.setUnitPrice(menuItem.getPrice());
            orderItem.setQuantity(qty);
            orderItem.setLineTotal(menuItem.getPrice().multiply(BigDecimal.valueOf(qty)));
            order.addItem(orderItem);
        }

        order.setDeliveryCharge(restaurant.getLocation().getDeliveryCharge());
        order.recalculateTotals();
        Order saved = orderRepository.save(order);

        // Cart consumed once the order exists.
        cartService.clear(userId);
        return saved;
    }

    /** Called by the payment module once payment succeeds: PENDING_PAYMENT -> PLACED. */
    @Transactional
    public Order markPaid(Long orderId) {
        Order order = getById(orderId);
        applyTransition(order, OrderStatus.PLACED);
        publish(order, "Payment successful. Your order has been placed.", restaurantAndCustomer(order));
        return order;
    }

    @Transactional
    public Order accept(Long orderId, Long ownerUserId, int estimatedCookTimeMinutes) {
        Order order = getById(orderId);
        assertRestaurantOwner(order, ownerUserId);
        if (estimatedCookTimeMinutes <= 0) {
            throw new BusinessRuleException("Estimated cook time must be positive");
        }
        applyTransition(order, OrderStatus.ACCEPTED);
        order.setEstimatedCookTimeMinutes(estimatedCookTimeMinutes);
        publish(
                order,
                "Restaurant accepted your order (ready in ~" + estimatedCookTimeMinutes + " min).",
                restaurantAndCustomer(order));
        // Trigger delivery partner assignment outside this transaction.
        events.publishEvent(new OrderAcceptedEvent(order.getId()));
        return order;
    }

    @Transactional
    public Order reject(Long orderId, Long ownerUserId) {
        Order order = getById(orderId);
        assertRestaurantOwner(order, ownerUserId);
        applyTransition(order, OrderStatus.REJECTED);
        releaseStock(order);
        publish(order, "Restaurant could not accept your order.", restaurantAndCustomer(order));
        events.publishEvent(new OrderTerminatedEvent(order.getId(), OrderStatus.REJECTED));
        return order;
    }

    @Transactional
    public Order startPreparing(Long orderId, Long ownerUserId) {
        Order order = getById(orderId);
        assertRestaurantOwner(order, ownerUserId);
        applyTransition(order, OrderStatus.PREPARING);
        publish(order, "Your food is being prepared.", restaurantAndCustomer(order));
        return order;
    }

    @Transactional
    public Order cancelByCustomer(Long orderId, Long customerUserId) {
        Order order = getById(orderId);
        Customer customer = customerService.getByUserId(customerUserId);
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessRuleException("You can only cancel your own orders");
        }
        if (!order.getStatus().canTransitionTo(OrderStatus.CANCELLED)) {
            throw new InvalidStateTransitionException(
                    "Order can no longer be cancelled (status " + order.getStatus() + ")");
        }
        applyTransition(order, OrderStatus.CANCELLED);
        releaseStock(order);
        publish(order, "Order cancelled.", restaurantAndCustomer(order));
        events.publishEvent(new OrderTerminatedEvent(order.getId(), OrderStatus.CANCELLED));
        return order;
    }

    /** Delivery-driven transition: partner picked up the order. */
    @Transactional
    public Order markOutForDelivery(Long orderId, Long partnerUserId) {
        Order order = getById(orderId);
        applyTransition(order, OrderStatus.OUT_FOR_DELIVERY);
        publish(order, "Your order is out for delivery.", allParties(order, partnerUserId));
        return order;
    }

    /** Delivery-driven transition: partner delivered the order. */
    @Transactional
    public Order markDelivered(Long orderId, Long partnerUserId) {
        Order order = getById(orderId);
        applyTransition(order, OrderStatus.DELIVERED);
        publish(order, "Your order has been delivered. Enjoy!", allParties(order, partnerUserId));
        return order;
    }

    @Transactional(readOnly = true)
    public Order getById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Order", id));
    }

    @Transactional(readOnly = true)
    public Order getWithItems(Long id) {
        return orderRepository
                .findByIdWithItems(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", id));
    }

    @Transactional(readOnly = true)
    public List<Order> listForCustomer(Long customerUserId) {
        Customer customer = customerService.getByUserId(customerUserId);
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId());
    }

    @Transactional(readOnly = true)
    public List<Order> listForRestaurant(Long restaurantId) {
        return orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);
    }

    // ----- internals -------------------------------------------------------

    private void applyTransition(Order order, OrderStatus target) {
        OrderStatus current = order.getStatus();
        if (!current.canTransitionTo(target)) {
            throw new InvalidStateTransitionException(
                    "Cannot move order " + order.getId() + " from " + current + " to " + target);
        }
        order.setStatus(target);
        orderRepository.save(order);
    }

    private void releaseStock(Order order) {
        for (OrderItem item : order.getItems()) {
            menuItemRepository.incrementStock(item.getMenuItem().getId(), item.getQuantity());
        }
    }

    private void assertRestaurantOwner(Order order, Long ownerUserId) {
        if (!order.getRestaurant().getOwner().getId().equals(ownerUserId)) {
            throw new BusinessRuleException("You do not own the restaurant for this order");
        }
    }

    private List<OrderEvent.Recipient> restaurantAndCustomer(Order order) {
        List<OrderEvent.Recipient> recipients = new ArrayList<>();
        recipients.add(OrderEvent.recipient(order.getCustomer().getUser().getId(), ROLE_CUSTOMER));
        recipients.add(OrderEvent.recipient(order.getRestaurant().getOwner().getId(), ROLE_RESTAURANT));
        return recipients;
    }

    private List<OrderEvent.Recipient> allParties(Order order, Long partnerUserId) {
        List<OrderEvent.Recipient> recipients = restaurantAndCustomer(order);
        if (partnerUserId != null) {
            recipients.add(OrderEvent.recipient(partnerUserId, ROLE_PARTNER));
        }
        return recipients;
    }

    private void publish(Order order, String summary, List<OrderEvent.Recipient> recipients) {
        events.publishEvent(new OrderEvent(order.getId(), order.getStatus(), summary, recipients));
    }
}

