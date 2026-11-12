package com.foodexpress.service;

import com.foodexpress.entity.FoodItem;
import com.foodexpress.repository.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;

    @Autowired
    public FoodItemService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    public List<FoodItem> getAvailableFoodItems() {
        return foodItemRepository.findByAvailableTrue();
    }

    public List<FoodItem> getFeaturedFoodItems() {
        return foodItemRepository.findByFeaturedTrueAndAvailableTrue();
    }

    public List<FoodItem> getFoodItemsByCategory(Long categoryId) {
        return foodItemRepository.findByCategoryIdAndAvailableTrue(categoryId);
    }

    public List<FoodItem> getFoodItemsByRestaurant(Long restaurantId) {
        return foodItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId);
    }

    public List<FoodItem> searchFoodItems(String query) {
        if (query == null || query.isBlank()) {
            return getAvailableFoodItems();
        }
        return foodItemRepository.searchAvailableFood(query.trim());
    }

    public List<FoodItem> searchFoodItemsAdmin(String query) {
        if (query == null || query.isBlank()) {
            return getAllFoodItems();
        }
        return foodItemRepository.searchAllFoodAdmin(query.trim());
    }

    public List<FoodItem> getAllFoodItems() {
        return foodItemRepository.findAll();
    }

    public Optional<FoodItem> getById(Long id) {
        return foodItemRepository.findById(id);
    }

    public FoodItem saveFoodItem(FoodItem foodItem) {
        return foodItemRepository.save(foodItem);
    }

    public FoodItem toggleAvailability(Long id) {
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Food item not found with ID: " + id));
        item.setAvailable(!item.isAvailable());
        return foodItemRepository.save(item);
    }

    public void deleteFoodItem(Long id) {
        foodItemRepository.deleteById(id);
    }

    public long getTotalFoodItemsCount() {
        return foodItemRepository.count();
    }

    public long getAvailableFoodItemsCount() {
        return foodItemRepository.countByAvailableTrue();
    }
}
