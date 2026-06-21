package org.FoodDelivery.customer;

import lombok.RequiredArgsConstructor;
import org.FoodDelivery.common.exception.BusinessRuleException;
import org.FoodDelivery.menu.MenuItem;
import org.FoodDelivery.menu.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Manages a customer's cart. A cart may only contain items from a single restaurant; attempting to
 * add an item from a different restaurant is rejected so the resulting order maps to one restaurant.
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final CustomerService customerService;
    private final MenuService menuService;

    @Transactional
    public List<CartItem> addOrUpdateItem(Long userId, Long menuItemId, int quantity) {
        Customer customer = customerService.getByUserId(userId);
        MenuItem menuItem = menuService.getById(menuItemId);
        if (!menuItem.isAvailable()) {
            throw new BusinessRuleException("Item is not available: " + menuItem.getName());
        }
        List<CartItem> existing = cartItemRepository.findByCustomerId(customer.getId());
        if (!existing.isEmpty()) {
            Long currentRestaurant = existing.get(0).getMenuItem().getRestaurant().getId();
            Long newRestaurant = menuItem.getRestaurant().getId();
            if (!currentRestaurant.equals(newRestaurant)) {
                throw new BusinessRuleException(
                        "Cart already contains items from another restaurant. Clear the cart first.");
            }
        }
        CartItem item = cartItemRepository
                .findByCustomerIdAndMenuItemId(customer.getId(), menuItemId)
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setCustomer(customer);
                    ci.setMenuItem(menuItem);
                    return ci;
                });
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return cartItemRepository.findByCustomerId(customer.getId());
    }

    @Transactional
    public void removeItem(Long userId, Long menuItemId) {
        Customer customer = customerService.getByUserId(userId);
        cartItemRepository
                .findByCustomerIdAndMenuItemId(customer.getId(), menuItemId)
                .ifPresent(cartItemRepository::delete);
    }

    @Transactional
    public void clear(Long userId) {
        Customer customer = customerService.getByUserId(userId);
        cartItemRepository.deleteByCustomerId(customer.getId());
    }

    @Transactional(readOnly = true)
    public List<CartItem> view(Long userId) {
        Customer customer = customerService.getByUserId(userId);
        return cartItemRepository.findByCustomerId(customer.getId());
    }
}
