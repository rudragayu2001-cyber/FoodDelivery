package org.FoodDelivery.common.exception;

import org.springframework.http.HttpStatus;

/** Thrown when a referenced entity does not exist. Maps to 404. */
public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public static ResourceNotFoundException of(String entity, Object id) {
        return new ResourceNotFoundException(entity + " not found with id " + id);
    }
}