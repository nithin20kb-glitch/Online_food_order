package com.foodexpress.controller;

import com.foodexpress.entity.*;
import com.foodexpress.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final FoodItemService foodItemService;
    private final CategoryService categoryService;
    private final RestaurantService restaurantService;
    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public AdminController(FoodItemService foodItemService,
                           CategoryService categoryService,
                           RestaurantService restaurantService,
                           OrderService orderService,
                           UserService userService) {
        this.foodItemService = foodItemService;
        this.categoryService = categoryService;
        this.restaurantService = restaurantService;
        this.orderService = orderService;
        this.userService = userService;
    }

    // 1. Dashboard
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        long totalOrders = orderService.getTotalOrdersCount();
        double totalSales = orderService.getTotalSalesAmount();
        long totalCustomers = userService.getTotalCustomersCount();
        long activeFoods = foodItemService.getAvailableFoodItemsCount();
        long totalFoods = foodItemService.getTotalFoodItemsCount();

        List<Order> recentOrders = orderService.getAllOrders().stream().limit(8).toList();

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalSales", totalSales);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("activeFoods", activeFoods);
        model.addAttribute("totalFoods", totalFoods);
        model.addAttribute("recentOrders", recentOrders);

        return "admin/dashboard";
    }

    // 2. Food Management (List, Search)
    @GetMapping("/foods")
    public String manageFoods(@RequestParam(value = "q", required = false) String search, Model model) {
        List<FoodItem> items = foodItemService.searchFoodItemsAdmin(search);
        model.addAttribute("foodItems", items);
        model.addAttribute("searchQuery", search);
        return "admin/foods";
    }

    // Add Food Form
    @GetMapping("/foods/new")
    public String newFoodForm(Model model) {
        FoodItem foodItem = new FoodItem();
        foodItem.setAvailable(true);
        model.addAttribute("foodItem", foodItem);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("isEdit", false);
        return "admin/food-form";
    }

    // Edit Food Form
    @GetMapping("/foods/edit/{id}")
    public String editFoodForm(@PathVariable("id") Long id, Model model) {
        FoodItem foodItem = foodItemService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Food item not found with ID: " + id));

        model.addAttribute("foodItem", foodItem);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("isEdit", true);
        return "admin/food-form";
    }

    // Save/Update Food
    @PostMapping("/foods/save")
    public String saveFood(@ModelAttribute("foodItem") FoodItem foodItem,
                           @RequestParam("categoryId") Long categoryId,
                           @RequestParam("restaurantId") Long restaurantId,
                           RedirectAttributes redirectAttributes) {

        Category category = categoryService.getById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));
        Restaurant restaurant = restaurantService.getById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with ID: " + restaurantId));

        foodItem.setCategory(category);
        foodItem.setRestaurant(restaurant);

        if (foodItem.getImageUrl() == null || foodItem.getImageUrl().isBlank()) {
            foodItem.setImageUrl("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=800&q=80");
        }

        foodItemService.saveFoodItem(foodItem);
        redirectAttributes.addFlashAttribute("successMessage", "Food item '" + foodItem.getName() + "' saved successfully!");
        return "redirect:/admin/foods";
    }

    // Toggle Food Availability
    @PostMapping("/foods/toggle/{id}")
    public String toggleAvailability(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        FoodItem item = foodItemService.toggleAvailability(id);
        String status = item.isAvailable() ? "In Stock" : "Out of Stock";
        redirectAttributes.addFlashAttribute("infoMessage", "'" + item.getName() + "' is now marked as " + status);
        return "redirect:/admin/foods";
    }

    // Delete Food
    @PostMapping("/foods/delete/{id}")
    public String deleteFood(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            foodItemService.deleteFoodItem(id);
            redirectAttributes.addFlashAttribute("successMessage", "Food item deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete item: " + e.getMessage());
        }
        return "redirect:/admin/foods";
    }

    // 3. Category Management
    @GetMapping("/categories")
    public String manageCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        if (!model.containsAttribute("categoryForm")) {
            model.addAttribute("categoryForm", new Category());
        }
        return "admin/categories";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute("categoryForm") Category category,
                               RedirectAttributes redirectAttributes) {
        categoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "Category '" + category.getName() + "' saved successfully!");
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete category with associated food items. Reassign items first.");
        }
        return "redirect:/admin/categories";
    }

    // 4. Order Management
    @GetMapping("/orders")
    public String manageOrders(@RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
                               Model model) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", status);
        return "admin/orders";
    }

    @PostMapping("/orders/update-status")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId,
                                    @RequestParam("status") String newStatus,
                                    @RequestParam(value = "reason", required = false) String reason,
                                    RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.updateOrderStatus(orderId, newStatus, reason);
            if ("REJECTED_REFUNDED".equalsIgnoreCase(newStatus)) {
                redirectAttributes.addFlashAttribute("warningMessage",
                        "Order #" + order.getOrderTrackingNumber() + " was rejected. Simulated refund of ₹" +
                        order.getTotalAmount() + " was initiated.");
            } else {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Order #" + order.getOrderTrackingNumber() + " status updated to " + newStatus);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update status: " + e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    // 5. Customer Information
    @GetMapping("/customers")
    public String manageCustomers(Model model) {
        List<User> customers = userService.getAllUsers().stream()
                .filter(u -> u.hasRole("ROLE_CUSTOMER"))
                .toList();
        model.addAttribute("customers", customers);
        return "admin/customers";
    }
}
