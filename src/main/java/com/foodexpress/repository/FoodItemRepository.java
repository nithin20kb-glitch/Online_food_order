package com.foodexpress.repository;

import com.foodexpress.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByAvailableTrue();
    List<FoodItem> findByFeaturedTrueAndAvailableTrue();
    List<FoodItem> findByCategoryIdAndAvailableTrue(Long categoryId);
    List<FoodItem> findByRestaurantIdAndAvailableTrue(Long restaurantId);
    
    @Query("SELECT f FROM FoodItem f WHERE f.available = true AND " +
           "(LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.category.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<FoodItem> searchAvailableFood(@Param("query") String query);

    @Query("SELECT f FROM FoodItem f WHERE " +
           "(LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.category.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<FoodItem> searchAllFoodAdmin(@Param("query") String query);

    long countByAvailableTrue();
}
