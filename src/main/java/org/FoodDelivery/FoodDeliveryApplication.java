package org.FoodDelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Entry point for the Food Delivery Order Management System.
 *
 * <p>The application is organised package-by-feature (city, restaurant, menu, customer,
 * cart, order, payment, delivery, review, notification, user) with cross-cutting concerns
 * under {@code common}, {@code config} and {@code security}.
 */
@SpringBootApplication
@EnableAsync
public class FoodDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryApplication.class, args);
    }
}