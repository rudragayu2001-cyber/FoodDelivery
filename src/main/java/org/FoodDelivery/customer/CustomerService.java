package org.FoodDelivery.customer;

import lombok.RequiredArgsConstructor;
import org.FoodDelivery.common.exception.BusinessRuleException;
import org.FoodDelivery.common.exception.ResourceNotFoundException;
import org.FoodDelivery.location.Location;
import org.FoodDelivery.location.LocationService;
import org.FoodDelivery.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final LocationService locationService;

    /** Creates the customer profile for a freshly registered CUSTOMER user. */
    @Transactional
    public Customer createProfile(User user, Long locationId, String deliveryAddress) {
        if (customerRepository.findByUserId(user.getId()).isPresent()) {
            throw new BusinessRuleException("Customer profile already exists");
        }
        Location location = locationService.getById(locationId);
        Customer customer = new Customer();
        customer.setUser(user);
        customer.setLocation(location);
        customer.setDeliveryAddress(deliveryAddress);
        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public Customer getByUserId(Long userId) {
        return customerRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No customer profile for user " + userId));
    }

    @Transactional(readOnly = true)
    public Customer getById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Customer", id));
    }
}

