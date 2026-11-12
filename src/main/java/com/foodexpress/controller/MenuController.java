package com.foodexpress.controller;

import com.foodexpress.entity.Category;
import com.foodexpress.entity.FoodItem;
import com.foodexpress.entity.Restaurant;
import com.foodexpress.service.CategoryService;
import com.foodexpress.service.FoodItemService;
import com.foodexpress.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class MenuController {

    private final FoodItemService foodItemService;
    private final CategoryService categoryService;
    private final RestaurantService restaurantService;

    @Autowired
    public MenuController(FoodItemService foodItemService,
                          CategoryService categoryService,
                          RestaurantService restaurantService) {
        this.foodItemService = foodItemService;
        this.categoryService = categoryService;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/menu")
    public String browseMenu(@RequestParam(value = "category", required = false) String categorySlug,
                             @RequestParam(value = "restaurant", required = false) Long restaurantId,
                             @RequestParam(value = "location", required = false) String location,
                             @RequestParam(value = "q", required = false) String searchQuery,
                             @RequestParam(value = "vegOnly", required = false, defaultValue = "false") boolean vegOnly,
                             Model model) {

        List<FoodItem> items;

        if (searchQuery != null && !searchQuery.isBlank()) {
            items = foodItemService.searchFoodItems(searchQuery);
        } else if (categorySlug != null && !categorySlug.isBlank() && !"all".equalsIgnoreCase(categorySlug)) {
            Optional<Category> optCat = categoryService.getBySlug(categorySlug);
            if (optCat.isPresent()) {
                items = foodItemService.getFoodItemsByCategory(optCat.get().getId());
                model.addAttribute("selectedCategory", optCat.get());
            } else {
                items = foodItemService.getAvailableFoodItems();
            }
        } else if (restaurantId != null) {
            items = foodItemService.getFoodItemsByRestaurant(restaurantId);
            restaurantService.getById(restaurantId).ifPresent(r -> model.addAttribute("selectedRestaurant", r));
        } else {
            items = foodItemService.getAvailableFoodItems();
        }

        // Apply location filtering on items if location is specified
        if (location != null && !location.isBlank() && !"all".equalsIgnoreCase(location)) {
            items = items.stream()
                    .filter(i -> i.getRestaurant() != null &&
                            i.getRestaurant().getLocationArea().equalsIgnoreCase(location.trim()))
                    .collect(Collectors.toList());
        }

        // Apply veg only filter if checked
        if (vegOnly) {
            items = items.stream().filter(FoodItem::isVegetarian).collect(Collectors.toList());
        }

        model.addAttribute("foodItems", items);
        model.addAttribute("categories", categoryService.getActiveCategories());
        model.addAttribute("restaurants", restaurantService.getOpenRestaurants());
        model.addAttribute("currentCategorySlug", categorySlug);
        model.addAttribute("currentRestaurantId", restaurantId);
        model.addAttribute("currentLocation", location);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("vegOnly", vegOnly);

        return "menu";
    }

    @GetMapping("/food/{id}")
    public String foodDetail(@PathVariable("id") Long id, Model model) {
        FoodItem foodItem = foodItemService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Food item not found with ID: " + id));

        // Related items in the same category
        List<FoodItem> related = foodItemService.getFoodItemsByCategory(foodItem.getCategory().getId())
                .stream()
                .filter(i -> !i.getId().equals(id))
                .limit(4)
                .collect(Collectors.toList());

        model.addAttribute("food", foodItem);
        model.addAttribute("relatedItems", related);

        return "food-detail";
    }
}
