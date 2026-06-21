package org.FoodDelivery.common.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

/** Uniform error payload returned for every failed request. Immutable record. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldViolation> violations) {

    public record FieldViolation(String field, String message) {}

    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(Instant.now(), status, error, message, path, null);
    }

    public static ApiError of(
            int status, String error, String message, String path, List<FieldViolation> violations) {
        return new ApiError(Instant.now(), status, error, message, path, violations);
    }
}
