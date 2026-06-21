package org.FoodDelivery.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.FoodDelivery.order.dto.OrderResponse;
import org.FoodDelivery.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order placement and lifecycle (customer + restaurant owner actions).")
public class OrderController {

    private final OrderService orderService;
    private final CurrentUserService currentUserService;

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "[CUSTOMER] Atomically place an order from the cart (reserves stock) -> PENDING_PAYMENT")
    public OrderResponse checkout() {
        Long userId = currentUserService.requireCurrentUserId();
        return OrderResponse.from(orderService.checkout(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "[ANY] Get an order with line items")
    public OrderResponse get(@PathVariable Long id) {
        return OrderResponse.from(orderService.getWithItems(id));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "[CUSTOMER] List my orders")
    public List<OrderResponse> mine() {
        Long userId = currentUserService.requireCurrentUserId();
        return orderService.listForCustomer(userId).stream().map(OrderResponse::from).toList();
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER','ADMIN')")
    @Operation(summary = "[OWNER/ADMIN] List a restaurant's orders")
    public List<OrderResponse> forRestaurant(@PathVariable Long restaurantId) {
        return orderService.listForRestaurant(restaurantId).stream().map(OrderResponse::from).toList();
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "[CUSTOMER] Cancel my order (before preparing) -> CANCELLED; returns stock + refunds")
    public OrderResponse cancel(@PathVariable Long id) {
        Long userId = currentUserService.requireCurrentUserId();
        return OrderResponse.from(orderService.cancelByCustomer(id, userId));
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "[OWNER] Accept order with cook time -> ACCEPTED (kicks off delivery offer)")
    public OrderResponse accept(@PathVariable Long id, @Valid @RequestBody AcceptOrderRequest request) {
        Long userId = currentUserService.requireCurrentUserId();
        return OrderResponse.from(orderService.accept(id, userId, request.estimatedCookTimeMinutes()));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "[OWNER] Reject order -> REJECTED; returns stock + refunds")
    public OrderResponse reject(@PathVariable Long id) {
        Long userId = currentUserService.requireCurrentUserId();
        return OrderResponse.from(orderService.reject(id, userId));
    }

    @PostMapping("/{id}/prepare")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "[OWNER] Start preparing -> PREPARING")
    public OrderResponse prepare(@PathVariable Long id) {
        Long userId = currentUserService.requireCurrentUserId();
        return OrderResponse.from(orderService.startPreparing(id, userId));
    }
}

