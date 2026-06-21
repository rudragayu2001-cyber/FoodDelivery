package org.FoodDelivery.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface DeliveryAssignmentRepository extends JpaRepository<DeliveryAssignment, Long> {

    Optional<DeliveryAssignment> findByOrderId(Long orderId);

    List<DeliveryAssignment> findByStatusOrderByCreatedAtAsc(AssignmentStatus status);

    List<DeliveryAssignment> findByDeliveryPartnerIdOrderByCreatedAtDesc(Long deliveryPartnerId);

    /** Offers in a given operating area that are still up for grabs. */
    @Query("SELECT a FROM DeliveryAssignment a "
            + "WHERE a.status = org.FoodDelivery.delivery.AssignmentStatus.OFFERED "
            + "AND a.order.restaurant.location.id = :locationId "
            + "ORDER BY a.createdAt ASC")
    List<DeliveryAssignment> findOpenOffersInLocation(@Param("locationId") Long locationId);

    /**
     * Atomically claim an OFFERED assignment for a partner. The {@code status = OFFERED} predicate
     * means only one of many concurrent acceptances can succeed; the rest see {@code 0} rows
     * updated and are told the order was already taken.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE DeliveryAssignment a "
            + "SET a.deliveryPartner = :partner, a.status = org.FoodDelivery.delivery.AssignmentStatus.ACCEPTED, "
            + "a.assignedAt = :now "
            + "WHERE a.id = :assignmentId AND a.status = org.FoodDelivery.delivery.AssignmentStatus.OFFERED")
    int claimOffer(
            @Param("assignmentId") Long assignmentId,
            @Param("partner") DeliveryPartner partner,
            @Param("now") Instant now);
}
