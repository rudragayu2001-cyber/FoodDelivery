package org.FoodDelivery.restaurant;

import lombok.RequiredArgsConstructor;
import org.FoodDelivery.common.exception.BusinessRuleException;
import org.FoodDelivery.common.exception.ResourceNotFoundException;
import org.FoodDelivery.location.Location;
import org.FoodDelivery.location.LocationService;
import org.FoodDelivery.restaurant.dto.RestaurantRequest;
import org.FoodDelivery.user.Role;
import org.FoodDelivery.user.User;
import org.FoodDelivery.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final LocationService locationService;
    private final UserRepository userRepository;

    @Transactional
    public Restaurant create(RestaurantRequest request) {
        Location location = locationService.getById(request.locationId());
        User owner = userRepository
                .findById(request.ownerId())
                .orElseThrow(() -> ResourceNotFoundException.of("User", request.ownerId()));
        if (owner.getRole() != Role.RESTAURANT_OWNER) {
            throw new BusinessRuleException("Assigned owner must have role RESTAURANT_OWNER");
        }
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.name());
        restaurant.setAddress(request.address());
        restaurant.setLocation(location);
        restaurant.setOwner(owner);
        restaurant.setActive(true);
        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public Restaurant setActive(Long id, boolean active) {
        Restaurant restaurant = getById(id);
        restaurant.setActive(active);
        return restaurantRepository.save(restaurant);
    }

    @Transactional(readOnly = true)
    public Restaurant getById(Long id) {
        return restaurantRepository
                .findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Restaurant", id));
    }

    @Transactional(readOnly = true)
    public List<Restaurant> listActive() {
        return restaurantRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Restaurant> listByLocation(Long locationId) {
        return restaurantRepository.findByLocationIdAndActiveTrue(locationId);
    }

    @Transactional(readOnly = true)
    public List<Restaurant> listOwnedBy(Long ownerId) {
        return restaurantRepository.findByOwnerId(ownerId);
    }

    /** Authorization helper: ensures the given user owns the restaurant (or is admin elsewhere). */
    public void assertOwnedBy(Restaurant restaurant, Long userId) {
        if (!restaurant.getOwner().getId().equals(userId)) {
            throw new BusinessRuleException("You do not own this restaurant");
        }
    }
}

