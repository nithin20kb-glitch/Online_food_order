package com.foodexpress.controller;

import com.foodexpress.dto.Cart;
import com.foodexpress.entity.Coupon;
import com.foodexpress.entity.FoodItem;
import com.foodexpress.service.CartService;
import com.foodexpress.service.CouponService;
import com.foodexpress.service.FoodItemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final FoodItemService foodItemService;
    private final CouponService couponService;

    @Autowired
    public CartController(CartService cartService,
                          FoodItemService foodItemService,
                          CouponService couponService) {
        this.cartService = cartService;
        this.foodItemService = foodItemService;
        this.couponService = couponService;
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Cart cart = cartService.getCart(session);
        List<Coupon> availableCoupons = couponService.getAllActiveCoupons();

        model.addAttribute("cart", cart);
        model.addAttribute("availableCoupons", availableCoupons);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("foodId") Long foodId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            HttpServletRequest request,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        Optional<FoodItem> optFood = foodItemService.getById(foodId);
        if (optFood.isPresent()) {
            FoodItem food = optFood.get();
            if (!food.isAvailable()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Sorry, '" + food.getName() + "' is currently out of stock.");
            } else {
                cartService.addItem(session, foodId, Math.max(1, quantity));
                redirectAttributes.addFlashAttribute("successMessage", "Added " + food.getName() + " to your cart!");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Food item not found.");
        }

        // Navigate customer directly to shopping cart to view their order and proceed to checkout
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam("foodId") Long foodId,
                                 @RequestParam("quantity") int quantity,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        cartService.updateQuantity(session, foodId, quantity);
        redirectAttributes.addFlashAttribute("infoMessage", "Cart updated successfully.");
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam("foodId") Long foodId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        cartService.removeItem(session, foodId);
        redirectAttributes.addFlashAttribute("infoMessage", "Item removed from cart.");
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        cartService.clearCart(session);
        redirectAttributes.addFlashAttribute("infoMessage", "Cart has been cleared.");
        return "redirect:/cart";
    }

    @PostMapping("/coupon/apply")
    public String applyCoupon(@RequestParam("couponCode") String couponCode,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        boolean applied = cartService.applyCoupon(session, couponCode);
        Cart cart = cartService.getCart(session);
        if (applied) {
            redirectAttributes.addFlashAttribute("successMessage", cart.getCouponMessage());
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", cart.getCouponMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/coupon/remove")
    public String removeCoupon(HttpSession session, RedirectAttributes redirectAttributes) {
        cartService.removeCoupon(session);
        redirectAttributes.addFlashAttribute("infoMessage", "Coupon removed.");
        return "redirect:/cart";
    }
}
