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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final FoodItemService foodItemService;
    private final CategoryService categoryService;
    private final RestaurantService restaurantService;

    @Autowired
    public HomeController(FoodItemService foodItemService,
                          CategoryService categoryService,
                          RestaurantService restaurantService) {
        this.foodItemService = foodItemService;
        this.categoryService = categoryService;
        this.restaurantService = restaurantService;
    }

    @GetMapping({"/", "/home"})
    public String home(@RequestParam(value = "location", required = false) String location, Model model) {
        List<Category> categories = categoryService.getActiveCategories();
        List<FoodItem> featuredDishes = foodItemService.getFeaturedFoodItems();
        List<Restaurant> restaurants = restaurantService.getRestaurantsByLocation(location);

        model.addAttribute("categories", categories);
        model.addAttribute("featuredDishes", featuredDishes);
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("selectedLocation", (location != null && !location.isBlank()) ? location : "All Locations");

        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About FoodExpress");
        return "about";
    }
}
