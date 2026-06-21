package org.FoodDelivery.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when concurrent access loses a race: stock just sold out, an order was already taken by
 * another delivery partner, or an optimistic-lock conflict occurred. Maps to 409.
 */
public class ConflictException extends ApiException {

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
