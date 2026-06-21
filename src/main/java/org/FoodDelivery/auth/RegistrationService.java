package org.FoodDelivery.auth;

import org.FoodDelivery.auth.dto.RegisterCustomerRequest;
import org.FoodDelivery.auth.dto.RegisterDeliveryPartnerRequest;
import org.FoodDelivery.customer.CustomerService;
import org.FoodDelivery.delivery.DeliveryPartnerService;
import org.FoodDelivery.user.Role;
import org.FoodDelivery.user.User;
import org.FoodDelivery.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Coordinates self-service signup: creates the {@link User} and its role-specific profile in one
 * transaction. Keeps the {@code user} module free of dependencies on customer/delivery modules.
 */
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final CustomerService customerService;
    private final DeliveryPartnerService partnerService;

    @Transactional
    public User registerCustomer(RegisterCustomerRequest request) {
        User user = userService.createUser(
                request.username(),
                request.password(),
                request.fullName(),
                request.email(),
                request.phone(),
                Role.CUSTOMER);
        customerService.createProfile(user, request.locationId(), request.deliveryAddress());
        return user;
    }

    @Transactional
    public User registerDeliveryPartner(RegisterDeliveryPartnerRequest request) {
        User user = userService.createUser(
                request.username(),
                request.password(),
                request.fullName(),
                request.email(),
                request.phone(),
                Role.DELIVERY_PARTNER);
        partnerService.createProfile(user, request.locationId());
        return user;
    }
}
