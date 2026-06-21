package org.FoodDelivery.payment.processor;

import org.FoodDelivery.common.exception.BusinessRuleException;
import org.FoodDelivery.payment.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves the right {@link PaymentProcessor} for a {@link PaymentMethod}. Spring injects every
 * processor bean, so registering a new method requires no edit here (OCP).
 */
@Component
public class PaymentProcessorRegistry {

    private final Map<PaymentMethod, PaymentProcessor> processors = new EnumMap<>(PaymentMethod.class);

    public PaymentProcessorRegistry(List<PaymentProcessor> available) {
        for (PaymentProcessor processor : available) {
            processors.put(processor.method(), processor);
        }
    }

    public PaymentProcessor forMethod(PaymentMethod method) {
        PaymentProcessor processor = processors.get(method);
        if (processor == null) {
            throw new BusinessRuleException("Unsupported payment method: " + method);
        }
        return processor;
    }
}
