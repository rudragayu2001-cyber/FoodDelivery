package org.FoodDelivery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/** Enables {@code @CreatedDate}/{@code @LastModifiedDate} auditing used by {@code BaseEntity}. */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
