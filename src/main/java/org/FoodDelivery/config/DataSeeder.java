package org.FoodDelivery.config;

import org.FoodDelivery.auth.RegistrationService;
import org.FoodDelivery.auth.dto.RegisterCustomerRequest;
import org.FoodDelivery.auth.dto.RegisterDeliveryPartnerRequest;
import org.FoodDelivery.location.Location;
import org.FoodDelivery.location.LocationService;
import org.FoodDelivery.location.dto.LocationRequest;
import org.FoodDelivery.menu.MenuService;
import org.FoodDelivery.menu.dto.MenuItemRequest;
import org.FoodDelivery.restaurant.Restaurant;
import org.FoodDelivery.restaurant.RestaurantService;
import org.FoodDelivery.restaurant.dto.RestaurantRequest;
import org.FoodDelivery.user.Role;
import org.FoodDelivery.user.User;
import org.FoodDelivery.user.UserRepository;
import org.FoodDelivery.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds a ready-to-demo dataset on first start (skipped under the {@code test} profile so tests get
 * a clean schema). All credentials below are for local demo only.
 */
@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;
    private final LocationService locationService;
    private final RestaurantService restaurantService;
    private final MenuService menuService;
    private final RegistrationService registrationService;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }
        log.info("Seeding demo data...");

        userService.createUser("admin", "admin123", "Platform Admin", "admin@fd.local", "0000000000", Role.ADMIN);

        Location blr = locationService.create(new LocationRequest("Bengaluru", "Karnataka", true, new BigDecimal("40.00")));
        locationService.create(new LocationRequest("Mumbai", "Maharashtra", true, new BigDecimal("55.00")));

        User owner = userService.createUser(
                "owner1", "owner123", "Ravi Owner", "owner1@fd.local", "1111111111", Role.RESTAURANT_OWNER);

        User owner2 = userService.createUser(
                "owner2", "owner234", "Ankit Owner", "owner2@fd.local", "2222222222", Role.RESTAURANT_OWNER);

        Restaurant restaurant = restaurantService.create(
                new RestaurantRequest("Spice Hub", "12 MG Road", blr.getId(), owner.getId()));
        Restaurant restaurant1 = restaurantService.create(
                new RestaurantRequest("Mani", "HSR Layout", blr.getId(), owner2.getId()));

        menuService.addItem(restaurant.getId(), owner.getId(),
                new MenuItemRequest("Paneer Butter Masala", "Creamy cottage cheese curry", new BigDecimal("220.00"), 25, true));
        menuService.addItem(restaurant.getId(), owner.getId(),
                new MenuItemRequest("Veg Biryani", "Fragrant basmati rice", new BigDecimal("180.00"), 30, true));
        menuService.addItem(restaurant.getId(), owner.getId(),
                new MenuItemRequest("Gulab Jamun (2 pcs)", "Warm dessert", new BigDecimal("60.00"), 50, true));

        registrationService.registerCustomer(new RegisterCustomerRequest(
                "customer1", "customer123", "Asha Customer", "customer1@fd.local", "2222222222", blr.getId(), "45 Indiranagar"));

        registrationService.registerDeliveryPartner(new RegisterDeliveryPartnerRequest(
                "partner1", "partner123", "Vijay Rider", "partner1@fd.local", "3333333333", blr.getId()));

        log.info("Seed complete. Demo users: admin/admin123, owner1/owner123, customer1/customer123, partner1/partner123");
    }
}
