package org.FoodDelivery.menu;

import lombok.RequiredArgsConstructor;
import org.FoodDelivery.common.exception.ResourceNotFoundException;
import org.FoodDelivery.menu.dto.MenuItemRequest;
import org.FoodDelivery.restaurant.Restaurant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantService restaurantService;

    @Transactional
    public MenuItem addItem(Long restaurantId, Long ownerId, MenuItemRequest request) {
        Restaurant restaurant = restaurantService.getById(restaurantId);
        restaurantService.assertOwnedBy(restaurant, ownerId);
        MenuItem item = new MenuItem();
        item.setRestaurant(restaurant);
        applyRequest(item, request);
        return menuItemRepository.save(item);
    }

    @Transactional
    public MenuItem updateItem(Long itemId, Long ownerId, MenuItemRequest request) {
        MenuItem item = getById(itemId);
        restaurantService.assertOwnedBy(item.getRestaurant(), ownerId);
        applyRequest(item, request);
        return menuItemRepository.save(item);
    }

    @Transactional
    public MenuItem setStock(Long itemId, Long ownerId, int stockQuantity) {
        MenuItem item = getById(itemId);
        restaurantService.assertOwnedBy(item.getRestaurant(), ownerId);
        item.setStockQuantity(stockQuantity);
        return menuItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public MenuItem getById(Long id) {
        return menuItemRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("MenuItem", id));
    }

    @Transactional(readOnly = true)
    public List<MenuItem> listForRestaurant(Long restaurantId, boolean availableOnly) {
        return availableOnly
                ? menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId)
                : menuItemRepository.findByRestaurantId(restaurantId);
    }

    private void applyRequest(MenuItem item, MenuItemRequest request) {
        item.setName(request.name());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setStockQuantity(request.stockQuantity());
        item.setAvailable(request.available());
    }
}

