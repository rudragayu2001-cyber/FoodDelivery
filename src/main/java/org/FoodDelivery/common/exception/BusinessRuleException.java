package org.FoodDelivery.common.exception;

import org.springframework.http.HttpStatus;

/** Thrown when a request is well-formed but violates a business rule. Maps to 400. */
public class BusinessRuleException extends ApiException {

    public BusinessRuleException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
