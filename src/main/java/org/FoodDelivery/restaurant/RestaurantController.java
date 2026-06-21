package org.FoodDelivery.restaurant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.FoodDelivery.restaurant.dto.RestaurantRequest;
import org.FoodDelivery.restaurant.dto.RestaurantResponse;
import org.FoodDelivery.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurants", description = "Restaurant creation (admin) and ownership/visibility.")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final CurrentUserService currentUserService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Create a restaurant and assign it to an owner")
    public RestaurantResponse create(@Valid @RequestBody RestaurantRequest request) {
        return RestaurantResponse.from(restaurantService.create(request));
    }

    @PostMapping("/{id}/active")
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_OWNER')")
    @Operation(summary = "[ADMIN/OWNER] Open or close a restaurant for ordering")
    public RestaurantResponse setActive(@PathVariable Long id, @RequestParam boolean value) {
        return RestaurantResponse.from(restaurantService.setActive(id, value));
    }

    @GetMapping
    @Operation(summary = "[ANY] List active restaurants")
    public List<RestaurantResponse> list() {
        return restaurantService.listActive().stream().map(RestaurantResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "[ANY] Get a restaurant by id")
    public RestaurantResponse get(@PathVariable Long id) {
        return RestaurantResponse.from(restaurantService.getById(id));
    }

    /** Restaurants owned by the calling RESTAURANT_OWNER. */
    @GetMapping("/mine")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "[OWNER] List my restaurants")
    public List<RestaurantResponse> mine() {
        Long ownerId = currentUserService.requireCurrentUserId();
        return restaurantService.listOwnedBy(ownerId).stream().map(RestaurantResponse::from).toList();
    }
}

