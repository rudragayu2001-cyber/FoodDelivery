package org.FoodDelivery.location;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByServiceableTrue();

    boolean existsByCityIgnoreCaseAndStateIgnoreCase(String city, String state);
}