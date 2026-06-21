package org.FoodDelivery.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration.
 *
 * <p>Declares HTTP Basic as the single security scheme and applies it globally, so the Swagger UI
 * "Authorize" button lets you log in as any seeded role (admin / owner1 / customer1 / partner1) and
 * then exercise the endpoints that role is allowed to call. Public endpoints (registration, health)
 * still work without credentials.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Food Delivery Order Management API",
                version = "v1",
                description = "Multi-restaurant food delivery: browse → cart → checkout → pay → accept → "
                        + "prepare → assign partner → pick up → deliver → review. Authenticate with HTTP Basic "
                        + "using a seeded user (admin/admin123, owner1/owner123, customer1/customer123, "
                        + "partner1/partner123). Each operation summary is prefixed with the role(s) allowed to call it.",
                contact = @Contact(name = "Food Delivery")),
        security = @SecurityRequirement(name = "basicAuth"))
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic",
        description = "HTTP Basic. Use a seeded username/password.")
public class OpenApiConfig {
}
