package org.FoodDelivery.delivery;

import org.FoodDelivery.common.exception.BusinessRuleException;
import org.FoodDelivery.common.exception.ResourceNotFoundException;
import org.FoodDelivery.location.Location;
import org.FoodDelivery.location.LocationService;
import org.FoodDelivery.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryPartnerService {

    private final DeliveryPartnerRepository partnerRepository;
    private final LocationService locationService;

    @Transactional
    public DeliveryPartner createProfile(User user, Long locationId) {
        if (partnerRepository.findByUserId(user.getId()).isPresent()) {
            throw new BusinessRuleException("Delivery partner profile already exists");
        }
        Location location = locationService.getById(locationId);
        DeliveryPartner partner = new DeliveryPartner();
        partner.setUser(user);
        partner.setLocation(location);
        partner.setStatus(PartnerStatus.OFFLINE);
        return partnerRepository.save(partner);
    }

    @Transactional
    public DeliveryPartner setStatus(Long userId, PartnerStatus status) {
        DeliveryPartner partner = getByUserId(userId);
        if (status == PartnerStatus.OFFLINE && partner.getStatus() == PartnerStatus.BUSY) {
            throw new BusinessRuleException("Cannot go offline while a delivery is in progress");
        }
        partner.setStatus(status);
        return partnerRepository.save(partner);
    }

    @Transactional(readOnly = true)
    public DeliveryPartner getByUserId(Long userId) {
        return partnerRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No delivery partner profile for user " + userId));
    }
}
