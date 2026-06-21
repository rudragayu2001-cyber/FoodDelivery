package org.FoodDelivery.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.FoodDelivery.security.CurrentUserService;
import org.FoodDelivery.user.dto.CreateUserRequest;
import org.FoodDelivery.user.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Account provisioning and self lookup.")
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    /** Admin provisions restaurant owners (and other staff accounts). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Create a staff account (e.g. RESTAURANT_OWNER)")
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(
                request.username(),
                request.password(),
                request.fullName(),
                request.email(),
                request.phone(),
                request.role());
        return UserResponse.from(user);
    }

    @GetMapping("/me")
    @Operation(summary = "[ANY] Get the currently authenticated user")
    public UserResponse me() {
        return UserResponse.from(currentUserService.requireCurrentUser());
    }
}

