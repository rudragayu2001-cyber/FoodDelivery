package org.FoodDelivery.location;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.FoodDelivery.location.dto.LocationRequest;
import org.FoodDelivery.location.dto.LocationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Tag(name = "Locations (cities)", description = "Serviceable areas; their delivery charge is billed on orders.")
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Create a serviceable location")
    public LocationResponse create(@Valid @RequestBody LocationRequest request) {
        return LocationResponse.from(locationService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Update a location (charge / serviceability)")
    public LocationResponse update(@PathVariable Long id, @Valid @RequestBody LocationRequest request) {
        return LocationResponse.from(locationService.update(id, request));
    }

    @GetMapping
    @Operation(summary = "[ANY] List all locations")
    public List<LocationResponse> list() {
        return locationService.listAll().stream().map(LocationResponse::from).toList();
    }

    @GetMapping("/serviceable")
    @Operation(summary = "[ANY] List serviceable locations")
    public List<LocationResponse> listServiceable() {
        return locationService.listServiceable().stream().map(LocationResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "[ANY] Get a location by id")
    public LocationResponse get(@PathVariable Long id) {
        return LocationResponse.from(locationService.getById(id));
    }
}
