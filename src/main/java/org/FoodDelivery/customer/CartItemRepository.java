package org.FoodDelivery.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCustomerId(Long customerId);

    Optional<CartItem> findByCustomerIdAndMenuItemId(Long customerId, Long menuItemId);

    @Transactional
    void deleteByCustomerId(Long customerId);
}