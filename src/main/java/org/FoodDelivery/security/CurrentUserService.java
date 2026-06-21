package org.FoodDelivery.security;

import org.FoodDelivery.common.exception.ApiException;
import org.FoodDelivery.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Convenience accessor for the authenticated {@link User} of the current request. */
@Component
public class CurrentUserService {

    public User requireCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppUserPrincipal principal)) {
            throw new NotAuthenticatedException();
        }
        return principal.getUser();
    }

    public Long requireCurrentUserId() {
        return requireCurrentUser().getId();
    }

    /** Raised when no authenticated principal is present. Maps to 401. */
    public static class NotAuthenticatedException extends ApiException {
        public NotAuthenticatedException() {
            super(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
    }
}

