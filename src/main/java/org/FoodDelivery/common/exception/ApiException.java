package org.FoodDelivery.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base type for all domain exceptions that map cleanly to an HTTP status.
 *
 * <p>Carrying the status on the exception lets the {@code GlobalExceptionHandler} translate any
 * subtype uniformly, so adding a new failure mode does not require touching the handler (OCP).
 */
@Getter
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;

    protected ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
