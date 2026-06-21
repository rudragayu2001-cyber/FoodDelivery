package org.FoodDelivery.customer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.FoodDelivery.customer.dto.CartItemRequest;
import org.FoodDelivery.customer.dto.CartResponse;
import org.FoodDelivery.security.CurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
@Tag(name = "Cart (customer)", description = "Single-restaurant cart for the logged-in customer.")
public class CartController {

    private final CartService cartService;
    private final CurrentUserService currentUserService;

    @GetMapping
    @Operation(summary = "[CUSTOMER] View my cart")
    public CartResponse view() {
        Long userId = currentUserService.requireCurrentUserId();
        return CartResponse.from(cartService.view(userId));
    }

    @PostMapping("/items")
    @Operation(summary = "[CUSTOMER] Add/update a cart line (upsert)")
    public CartResponse addOrUpdate(@Valid @RequestBody CartItemRequest request) {
        Long userId = currentUserService.requireCurrentUserId();
        return CartResponse.from(cartService.addOrUpdateItem(userId, request.menuItemId(), request.quantity()));
    }

    @DeleteMapping("/items/{menuItemId}")
    @Operation(summary = "[CUSTOMER] Remove a cart line")
    public CartResponse remove(@PathVariable Long menuItemId) {
        Long userId = currentUserService.requireCurrentUserId();
        cartService.removeItem(userId, menuItemId);
        return CartResponse.from(cartService.view(userId));
    }

    @DeleteMapping
    @Operation(summary = "[CUSTOMER] Clear my cart")
    public ResponseEntity<Void> clear() {
        Long userId = currentUserService.requireCurrentUserId();
        cartService.clear(userId);
        return ResponseEntity.noContent().build();
    }
}
