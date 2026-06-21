package org.FoodDelivery.payment;

import org.FoodDelivery.common.exception.BusinessRuleException;
import org.FoodDelivery.common.exception.ResourceNotFoundException;
import org.FoodDelivery.customer.Customer;
import org.FoodDelivery.customer.CustomerService;
import org.FoodDelivery.order.Order;
import org.FoodDelivery.order.OrderService;
import org.FoodDelivery.order.OrderStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.FoodDelivery.payment.processor.PaymentProcessor;
import org.FoodDelivery.payment.processor.PaymentProcessorRegistry;
import org.FoodDelivery.payment.processor.PaymentResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Simulated payment, plus the half of "atomic placement" that confirms the order.
 *
 * <p>A single transaction creates/settles the {@link Payment} and, on success, drives the order
 * from {@code PENDING_PAYMENT} to {@code PLACED} via {@link OrderService}. Stock was already
 * reserved at checkout, so a successful payment cannot leave the order over-committed.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProcessorRegistry processorRegistry;
    private final OrderService orderService;
    private final CustomerService customerService;

    @Transactional
    public Payment pay(Long orderId, Long customerUserId, PaymentMethod method) {
        Order order = orderService.getById(orderId);

        Customer customer = customerService.getByUserId(customerUserId);
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessRuleException("You can only pay for your own orders");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessRuleException("Order is not awaiting payment (status " + order.getStatus() + ")");
        }
        paymentRepository.findByOrderId(orderId).ifPresent(existing -> {
            if (existing.getStatus() == PaymentStatus.SUCCESS) {
                throw new BusinessRuleException("Order has already been paid");
            }
        });

        Payment payment = paymentRepository.findByOrderId(orderId).orElseGet(Payment::new);
        payment.setOrder(order);
        payment.setAmount(order.getGrandTotal());
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        PaymentProcessor processor = processorRegistry.forMethod(method);
        PaymentResult result = processor.charge(orderId, order.getGrandTotal());

        if (!result.success()) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new BusinessRuleException("Payment failed: " + result.failureReason());
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionRef(result.transactionRef());
        paymentRepository.save(payment);

        // Confirm the order in the same transaction.
        orderService.markPaid(orderId);
        return payment;
    }

    /**
     * Refund a captured payment when the order ends without fulfilment. Invoked after the order
     * transaction commits, in its own transaction, so a refund failure never rolls back the
     * cancellation/rejection itself.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void refundForTerminatedOrder(Long orderId) {
        paymentRepository
                .findByOrderId(orderId)
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.REFUNDED);
                    paymentRepository.save(payment);
                    log.info("Refunded payment {} for terminated order {}", payment.getId(), orderId);
                });
    }

    @Transactional(readOnly = true)
    public Payment getByOrderId(Long orderId) {
        return paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment for order " + orderId));
    }
}
