package org.FoodDelivery.delivery.dto;

import org.FoodDelivery.delivery.PartnerStatus;
import jakarta.validation.constraints.NotNull;

public record PartnerStatusRequest(@NotNull PartnerStatus status) {}
