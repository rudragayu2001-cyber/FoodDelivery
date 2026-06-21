package org.FoodDelivery.user.dto;


import org.FoodDelivery.user.Role;
import org.FoodDelivery.user.User;

public record UserResponse(
        Long id, String username, String fullName, String email, String phone, Role role, boolean enabled) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.isEnabled());
    }
}

