package org.FoodDelivery.restaurant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByActiveTrue();

    List<Restaurant> findByLocationIdAndActiveTrue(Long locationId);

    List<Restaurant> findByOwnerId(Long ownerId);
}
