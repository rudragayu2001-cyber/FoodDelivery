package org.FoodDelivery.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByRestaurantId(Long restaurantId);

    List<MenuItem> findByRestaurantIdAndAvailableTrue(Long restaurantId);

    /**
     * Atomically reserve {@code qty} units only if enough stock is on hand.
     *
     * <p>The {@code stockQuantity >= :qty} predicate is evaluated by the database under a row lock,
     * so two concurrent orders for the last unit cannot both succeed. Returns the number of rows
     * updated: {@code 1} means the reservation succeeded, {@code 0} means insufficient stock.
     */
    @Modifying
    @Query("UPDATE MenuItem m SET m.stockQuantity = m.stockQuantity - :qty "
            + "WHERE m.id = :id AND m.available = true AND m.stockQuantity >= :qty")
    int decrementStock(@Param("id") Long id, @Param("qty") int qty);

    /** Compensating increment used when an order is cancelled/rejected and stock must be returned. */
    @Modifying
    @Query("UPDATE MenuItem m SET m.stockQuantity = m.stockQuantity + :qty WHERE m.id = :id")
    int incrementStock(@Param("id") Long id, @Param("qty") int qty);
}
