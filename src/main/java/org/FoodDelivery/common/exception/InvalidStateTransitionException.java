package org.FoodDelivery.common.exception;

import org.springframework.http.HttpStatus;

/** Thrown when an order is asked to move to a status that the state machine does not allow. */
public class InvalidStateTransitionException extends ApiException {

    public InvalidStateTransitionException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
