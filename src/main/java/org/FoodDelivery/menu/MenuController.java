package org.FoodDelivery.menu;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.FoodDelivery.menu.dto.MenuItemRequest;
import org.FoodDelivery.menu.dto.MenuItemResponse;
import org.FoodDelivery.menu.dto.StockAdjustmentRequest;
import org.FoodDelivery.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Menu & inventory", description = "Restaurant owners manage menu items and stock.")
public class MenuController {

    private final MenuService menuService;
    private final CurrentUserService currentUserService;

    @PostMapping("/restaurants/{restaurantId}/menu-items")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "[OWNER] Add a menu item to my restaurant")
    public MenuItemResponse add(
            @PathVariable Long restaurantId, @Valid @RequestBody MenuItemRequest request) {
        Long ownerId = currentUserService.requireCurrentUserId();
        return MenuItemResponse.from(menuService.addItem(restaurantId, ownerId, request));
    }

    @PutMapping("/menu-items/{itemId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "[OWNER] Update a menu item")
    public MenuItemResponse update(@PathVariable Long itemId, @Valid @RequestBody MenuItemRequest request) {
        Long ownerId = currentUserService.requireCurrentUserId();
        return MenuItemResponse.from(menuService.updateItem(itemId, ownerId, request));
    }

    @PostMapping("/menu-items/{itemId}/stock")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "[OWNER] Set absolute stock level for an item")
    public MenuItemResponse setStock(
            @PathVariable Long itemId, @Valid @RequestBody StockAdjustmentRequest request) {
        Long ownerId = currentUserService.requireCurrentUserId();
        return MenuItemResponse.from(menuService.setStock(itemId, ownerId, request.stockQuantity()));
    }

    @GetMapping("/restaurants/{restaurantId}/menu-items")
    @Operation(summary = "[ANY] List a restaurant's menu items")
    public List<MenuItemResponse> list(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "true") boolean availableOnly) {
        return menuService.listForRestaurant(restaurantId, availableOnly).stream()
                .map(MenuItemResponse::from)
                .toList();
    }
}
