package org.FoodDelivery.delivery;

import org.FoodDelivery.delivery.dto.DeliveryAssignmentResponse;
import org.FoodDelivery.delivery.dto.PartnerStatusRequest;
import org.FoodDelivery.security.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
@Tag(name = "Delivery", description = "Partner availability, contested assignment, pickup/delivery, tracking.")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final DeliveryPartnerService partnerService;
    private final CurrentUserService currentUserService;

    @PostMapping("/status")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    @Operation(summary = "[PARTNER] Set my availability (AVAILABLE/BUSY/OFFLINE)")
    public String setStatus(@Valid @RequestBody PartnerStatusRequest request) {
        Long userId = currentUserService.requireCurrentUserId();
        return partnerService.setStatus(userId, request.status()).getStatus().name();
    }

    @GetMapping("/offers")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    @Operation(summary = "[PARTNER] List open offers in my operating area")
    public List<DeliveryAssignmentResponse> openOffers() {
        Long userId = currentUserService.requireCurrentUserId();
        return deliveryService.openOffersFor(userId).stream()
                .map(DeliveryAssignmentResponse::from)
                .toList();
    }

    @GetMapping("/assignments/mine")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    @Operation(summary = "[PARTNER] My assignment history")
    public List<DeliveryAssignmentResponse> myAssignments() {
        Long userId = currentUserService.requireCurrentUserId();
        return deliveryService.myAssignments(userId).stream()
                .map(DeliveryAssignmentResponse::from)
                .toList();
    }

    @PostMapping("/assignments/{id}/accept")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    @Operation(summary = "[PARTNER] Claim an offered order (first to win; others get 409)")
    public DeliveryAssignmentResponse accept(@PathVariable Long id) {
        Long userId = currentUserService.requireCurrentUserId();
        return DeliveryAssignmentResponse.from(deliveryService.acceptOffer(id, userId));
    }

    @PostMapping("/assignments/{id}/pickup")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    @Operation(summary = "[PARTNER] Pick up the order -> OUT_FOR_DELIVERY")
    public DeliveryAssignmentResponse pickUp(@PathVariable Long id) {
        Long userId = currentUserService.requireCurrentUserId();
        return DeliveryAssignmentResponse.from(deliveryService.pickUp(id, userId));
    }

    @PostMapping("/assignments/{id}/deliver")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    @Operation(summary = "[PARTNER] Mark delivered -> DELIVERED")
    public DeliveryAssignmentResponse deliver(@PathVariable Long id) {
        Long userId = currentUserService.requireCurrentUserId();
        return DeliveryAssignmentResponse.from(deliveryService.deliver(id, userId));
    }

    /** Order tracking: who is delivering my order. */
    @GetMapping("/order/{orderId}")
    @Operation(summary = "[ANY] Track delivery for an order")
    public DeliveryAssignmentResponse byOrder(@PathVariable Long orderId) {
        return DeliveryAssignmentResponse.from(deliveryService.getByOrderId(orderId));
    }
}
