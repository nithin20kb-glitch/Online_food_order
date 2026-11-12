package com.foodexpress.service;

import com.foodexpress.dto.Cart;
import com.foodexpress.dto.CartItemDto;
import com.foodexpress.entity.FoodItem;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    public static final String SESSION_CART_KEY = "FOODEXPRESS_SHOPPING_CART";

    private final FoodItemService foodItemService;
    private final CouponService couponService;

    @Autowired
    public CartService(FoodItemService foodItemService, CouponService couponService) {
        this.foodItemService = foodItemService;
        this.couponService = couponService;
    }

    public Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(SESSION_CART_KEY);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(SESSION_CART_KEY, cart);
        }
        syncCartWithDatabase(cart);
        return cart;
    }

    public void addItem(HttpSession session, Long foodId, int quantity) {
        Cart cart = getCart(session);
        Optional<FoodItem> optFood = foodItemService.getById(foodId);
        if (optFood.isPresent()) {
            FoodItem food = optFood.get();
            if (food.isAvailable()) {
                cart.addItem(food, quantity);
            }
        }
        recalculateCouponIfPresent(cart);
        session.setAttribute(SESSION_CART_KEY, cart);
    }

    public void updateQuantity(HttpSession session, Long foodId, int quantity) {
        Cart cart = getCart(session);
        cart.updateQuantity(foodId, quantity);
        recalculateCouponIfPresent(cart);
        session.setAttribute(SESSION_CART_KEY, cart);
    }

    public void removeItem(HttpSession session, Long foodId) {
        Cart cart = getCart(session);
        cart.removeItem(foodId);
        recalculateCouponIfPresent(cart);
        session.setAttribute(SESSION_CART_KEY, cart);
    }

    public void clearCart(HttpSession session) {
        Cart cart = getCart(session);
        cart.clear();
        session.setAttribute(SESSION_CART_KEY, cart);
    }

    public boolean applyCoupon(HttpSession session, String couponCode) {
        Cart cart = getCart(session);
        boolean applied = couponService.applyCouponToCart(cart, couponCode);
        session.setAttribute(SESSION_CART_KEY, cart);
        return applied;
    }

    public void removeCoupon(HttpSession session) {
        Cart cart = getCart(session);
        couponService.removeCouponFromCart(cart);
        session.setAttribute(SESSION_CART_KEY, cart);
    }

    private void syncCartWithDatabase(Cart cart) {
        if (cart.isEmpty()) return;
        for (CartItemDto item : cart.getItemsList()) {
            foodItemService.getById(item.getFoodId()).ifPresentOrElse(
                    freshFood -> {
                        item.setAvailable(freshFood.isAvailable());
                        item.setPrice(freshFood.getPrice());
                        item.setFoodName(freshFood.getName());
                    },
                    () -> item.setAvailable(false)
            );
        }
    }

    private void recalculateCouponIfPresent(Cart cart) {
        if (cart.getAppliedCoupon() != null) {
            if (cart.getSubtotal() < cart.getAppliedCoupon().getMinOrderValue()) {
                cart.setCouponMessage("Coupon removed: order subtotal dropped below minimum ₹" + 
                        cart.getAppliedCoupon().getMinOrderValue());
                cart.setAppliedCoupon(null);
            }
        }
    }
}
