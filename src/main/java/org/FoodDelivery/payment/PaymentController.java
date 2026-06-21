package org.FoodDelivery.payment;

import org.FoodDelivery.payment.dto.PaymentRequest;
import org.FoodDelivery.payment.dto.PaymentResponse;
import org.FoodDelivery.security.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/{orderId}/payment")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Simulated payment that confirms the order (PENDING_PAYMENT -> PLACED).")
public class PaymentController {

    private final PaymentService paymentService;
    private final CurrentUserService currentUserService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "[CUSTOMER] Pay for an order (CARD/UPI/WALLET/CASH_ON_DELIVERY) -> PLACED")
    public PaymentResponse pay(@PathVariable Long orderId, @Valid @RequestBody PaymentRequest request) {
        Long userId = currentUserService.requireCurrentUserId();
        return PaymentResponse.from(paymentService.pay(orderId, userId, request.method()));
    }

    @GetMapping
    @Operation(summary = "[ANY] Get the payment for an order")
    public PaymentResponse get(@PathVariable Long orderId) {
        return PaymentResponse.from(paymentService.getByOrderId(orderId));
    }
}
