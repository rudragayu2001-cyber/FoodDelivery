package org.FoodDelivery.auth;

import org.FoodDelivery.auth.dto.RegisterCustomerRequest;
import org.FoodDelivery.auth.dto.RegisterDeliveryPartnerRequest;
import org.FoodDelivery.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth (public)", description = "Self-service registration. No authentication required.")
public class AuthController {

    private final RegistrationService registrationService;

    @PostMapping("/register/customer")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "[PUBLIC] Register a customer (creates the User + Customer profile)")
    public UserResponse registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
        return UserResponse.from(registrationService.registerCustomer(request));
    }

    @PostMapping("/register/delivery-partner")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "[PUBLIC] Register a delivery partner (creates the User + DeliveryPartner profile)")
    public UserResponse registerDeliveryPartner(@Valid @RequestBody RegisterDeliveryPartnerRequest request) {
        return UserResponse.from(registrationService.registerDeliveryPartner(request));
    }
}
