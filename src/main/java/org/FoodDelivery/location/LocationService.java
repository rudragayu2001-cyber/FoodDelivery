package org.FoodDelivery.location;

import lombok.RequiredArgsConstructor;
import org.FoodDelivery.common.domain.exception.BusinessRuleException;
import org.FoodDelivery.common.domain.exception.ResourceNotFoundException;
import org.FoodDelivery.location.dto.LocationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    @Transactional
    public Location create(LocationRequest request) {
        if (locationRepository.existsByCityIgnoreCaseAndStateIgnoreCase(request.city(), request.state())) {
            throw new BusinessRuleException(
                    "Location already exists: " + request.city() + ", " + request.state());
        }
        Location location = new Location();
        location.setCity(request.city());
        location.setState(request.state());
        location.setServiceable(request.serviceable());
        location.setDeliveryCharge(request.deliveryCharge());
        return locationRepository.save(location);
    }

    @Transactional
    public Location update(Long id, LocationRequest request) {
        Location location = getById(id);
        location.setCity(request.city());
        location.setState(request.state());
        location.setServiceable(request.serviceable());
        location.setDeliveryCharge(request.deliveryCharge());
        return locationRepository.save(location);
    }

    @Transactional(readOnly = true)
    public Location getById(Long id) {
        return locationRepository
                .findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Location", id));
    }

    @Transactional(readOnly = true)
    public List<Location> listAll() {
        return locationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Location> listServiceable() {
        return locationRepository.findByServiceableTrue();
    }
}
