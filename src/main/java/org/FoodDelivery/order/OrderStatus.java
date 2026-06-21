package org.FoodDelivery.order;

import java.util.*;

public enum OrderStatus {
    PENDING_PAYMENT,
    PLACED,
    ACCEPTED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    REJECTED,
    CANCELLED;

    private static final Map<OrderStatus, Set<OrderStatus>> TRANSITIONS = new EnumMap<>(OrderStatus.class);

    static {
        TRANSITIONS.put(PENDING_PAYMENT, EnumSet.of(PLACED, CANCELLED));
        TRANSITIONS.put(PLACED, EnumSet.of(ACCEPTED, REJECTED, CANCELLED));
        TRANSITIONS.put(ACCEPTED, EnumSet.of(PREPARING, CANCELLED));
        TRANSITIONS.put(PREPARING, EnumSet.of(OUT_FOR_DELIVERY));
        TRANSITIONS.put(OUT_FOR_DELIVERY, EnumSet.of(DELIVERED));
        TRANSITIONS.put(DELIVERED, Collections.emptySet());
        TRANSITIONS.put(REJECTED, Collections.emptySet());
        TRANSITIONS.put(CANCELLED, Collections.emptySet());
    }

    public boolean canTransitionTo(OrderStatus target) {
        return TRANSITIONS.getOrDefault(this, Collections.emptySet()).contains(target);
    }

    public Set<OrderStatus> allowedNext() {
        return Collections.unmodifiableSet(TRANSITIONS.getOrDefault(this, Collections.emptySet()));
    }

    public boolean isTerminal() {
        return TRANSITIONS.getOrDefault(this, Collections.emptySet()).isEmpty();
    }

    /** Stock should be returned to inventory only when an order ends without being fulfilled. */
    public boolean releasesStock() {
        return this == CANCELLED || this == REJECTED;
    }
}
