package org.FoodDelivery.browse;

import org.FoodDelivery.common.exception.BusinessRuleException;
import org.FoodDelivery.location.Location;
import org.FoodDelivery.location.LocationService;
import org.FoodDelivery.location.dto.LocationResponse;
import org.FoodDelivery.menu.MenuService;
import org.FoodDelivery.menu.dto.MenuItemResponse;
import org.FoodDelivery.restaurant.RestaurantService;
import org.FoodDelivery.restaurant.dto.RestaurantResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Customer-facing discovery: browse serviceable locations, the active restaurants in a location,
 * and a restaurant's available menu. Read-only aggregation over the catalogue services.
 */
@RestController
@RequestMapping("/api/browse")
@RequiredArgsConstructor
@Tag(name = "Browse (customer)", description = "Discovery: serviceable areas, restaurants, menus.")
public class BrowseController {

    private final LocationService locationService;
    private final RestaurantService restaurantService;
    private final MenuService menuService;

    @GetMapping("/locations")
    @Operation(summary = "[ANY] Browse serviceable locations")
    public List<LocationResponse> locations() {
        return locationService.listServiceable().stream().map(LocationResponse::from).toList();
    }

    @GetMapping("/restaurants")
    @Operation(summary = "[ANY] Browse active restaurants in a location")
    public List<RestaurantResponse> restaurants(@RequestParam Long locationId) {
        Location location = locationService.getById(locationId);
        if (!location.isServiceable()) {
            throw new BusinessRuleException("Location is not currently serviceable");
        }
        return restaurantService.listByLocation(locationId).stream()
                .map(RestaurantResponse::from)
                .toList();
    }

    @GetMapping("/restaurants/{restaurantId}/menu")
    @Operation(summary = "[ANY] Browse a restaurant's available menu")
    public List<MenuItemResponse> menu(@PathVariable Long restaurantId) {
        return menuService.listForRestaurant(restaurantId, true).stream()
                .map(MenuItemResponse::from)
                .toList();
    }
}
