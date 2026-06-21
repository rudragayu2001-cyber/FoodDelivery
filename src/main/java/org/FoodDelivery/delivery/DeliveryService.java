package org.FoodDelivery.delivery;

import org.FoodDelivery.common.exception.BusinessRuleException;
import org.FoodDelivery.common.exception.ConflictException;
import org.FoodDelivery.common.exception.ResourceNotFoundException;

import org.FoodDelivery.order.Order;
import org.FoodDelivery.order.OrderService;
import org.FoodDelivery.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Owns delivery-partner assignment and the pickup → delivered transitions.
 *
 * <p>The interesting part is contention: when a restaurant accepts an order we create a single
 * {@code OFFERED} assignment. Many partners may try to accept it at once; acceptance is a
 * conditional atomic update ({@code claimOffer}) so exactly one wins and the rest get a clear 409.
 */
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private static final String ROLE_CUSTOMER = "CUSTOMER";
    private static final String ROLE_PARTNER = "DELIVERY_PARTNER";

    private final DeliveryAssignmentRepository assignmentRepository;
    private final DeliveryPartnerRepository partnerRepository;
    private final DeliveryPartnerService partnerService;
    private final OrderService orderService;
    private final ApplicationEventPublisher events;

    /**
     * Create the OFFERED assignment once a restaurant accepts the order (idempotent). Runs in a new
     * transaction because it is invoked from an {@code AFTER_COMMIT} event callback.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createOfferForOrder(Long orderId) {
        if (assignmentRepository.findByOrderId(orderId).isPresent()) {
            return;
        }
        Order order = orderService.getById(orderId);
        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setOrder(order);
        assignment.setStatus(AssignmentStatus.OFFERED);
        assignmentRepository.save(assignment);
    }

    @Transactional(readOnly = true)
    public List<DeliveryAssignment> openOffersFor(Long partnerUserId) {
        DeliveryPartner partner = partnerService.getByUserId(partnerUserId);
        return assignmentRepository.findOpenOffersInLocation(partner.getLocation().getId());
    }

    @Transactional(readOnly = true)
    public List<DeliveryAssignment> myAssignments(Long partnerUserId) {
        DeliveryPartner partner = partnerService.getByUserId(partnerUserId);
        return assignmentRepository.findByDeliveryPartnerIdOrderByCreatedAtDesc(partner.getId());
    }

    /** Attempt to claim an offered order. First writer wins; everyone else gets a conflict. */
    @Transactional
    public DeliveryAssignment acceptOffer(Long assignmentId, Long partnerUserId) {
        DeliveryPartner partner = partnerService.getByUserId(partnerUserId);
        if (partner.getStatus() != PartnerStatus.AVAILABLE) {
            throw new BusinessRuleException("Set your status to AVAILABLE before accepting orders");
        }
        DeliveryAssignment assignment = getById(assignmentId);
        if (assignment.getStatus() != AssignmentStatus.OFFERED) {
            throw new ConflictException("This order has already been assigned to another partner");
        }
        if (!assignment.getOrder().getRestaurant().getLocation().getId().equals(partner.getLocation().getId())) {
            throw new BusinessRuleException("Order is outside your operating area");
        }

        int claimed = assignmentRepository.claimOffer(assignmentId, partner, Instant.now());
        if (claimed == 0) {
            throw new ConflictException("This order was just assigned to another partner");
        }

        // claimOffer cleared the persistence context, so re-attach before using lazy associations.
        DeliveryPartner managedPartner = partnerRepository.findById(partner.getId()).orElseThrow();
        managedPartner.setStatus(PartnerStatus.BUSY);
        partnerRepository.save(managedPartner);

        DeliveryAssignment claimedAssignment = getById(assignmentId);
        notifyAssignment(claimedAssignment, managedPartner);
        return claimedAssignment;
    }

    @Transactional
    public DeliveryAssignment pickUp(Long assignmentId, Long partnerUserId) {
        DeliveryAssignment assignment = getOwnedAssignment(assignmentId, partnerUserId);
        if (assignment.getStatus() != AssignmentStatus.ACCEPTED) {
            throw new BusinessRuleException("Assignment is not ready for pickup");
        }
        Order order = assignment.getOrder();
        if (order.getStatus() != OrderStatus.PREPARING) {
            throw new BusinessRuleException("Food is not ready for pickup yet");
        }
        assignment.setStatus(AssignmentStatus.PICKED_UP);
        assignment.setPickedUpAt(Instant.now());
        assignmentRepository.save(assignment);

        orderService.markOutForDelivery(order.getId(), partnerUserId);
        return assignment;
    }

    @Transactional
    public DeliveryAssignment deliver(Long assignmentId, Long partnerUserId) {
        DeliveryAssignment assignment = getOwnedAssignment(assignmentId, partnerUserId);
        if (assignment.getStatus() != AssignmentStatus.PICKED_UP) {
            throw new BusinessRuleException("Assignment has not been picked up");
        }
        assignment.setStatus(AssignmentStatus.DELIVERED);
        assignment.setDeliveredAt(Instant.now());
        assignmentRepository.save(assignment);

        DeliveryPartner partner = assignment.getDeliveryPartner();
        partner.setStatus(PartnerStatus.AVAILABLE);
        partnerRepository.save(partner);

        orderService.markDelivered(assignment.getOrder().getId(), partnerUserId);
        return assignment;
    }

    @Transactional(readOnly = true)
    public DeliveryAssignment getByOrderId(Long orderId) {
        return assignmentRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No delivery assignment for order " + orderId));
    }

    // ----- internals -------------------------------------------------------

    private DeliveryAssignment getById(Long id) {
        return assignmentRepository
                .findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("DeliveryAssignment", id));
    }

    private DeliveryAssignment getOwnedAssignment(Long assignmentId, Long partnerUserId) {
        DeliveryAssignment assignment = getById(assignmentId);
        if (assignment.getDeliveryPartner() == null
                || !assignment.getDeliveryPartner().getUser().getId().equals(partnerUserId)) {
            throw new BusinessRuleException("This assignment is not yours");
        }
        return assignment;
    }

    private void notifyAssignment(DeliveryAssignment assignment, DeliveryPartner partner) {
        Order order = assignment.getOrder();
        List<OrderEvent.Recipient> recipients = List.of(
                OrderEvent.recipient(order.getCustomer().getUser().getId(), ROLE_CUSTOMER),
                OrderEvent.recipient(partner.getUser().getId(), ROLE_PARTNER));
        events.publishEvent(new OrderEvent(
                order.getId(),
                order.getStatus(),
                "Delivery partner " + partner.getUser().getFullName() + " is assigned to your order.",
                recipients));
    }
}
