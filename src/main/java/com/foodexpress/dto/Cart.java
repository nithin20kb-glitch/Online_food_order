package com.foodexpress.dto;

import com.foodexpress.entity.Coupon;
import com.foodexpress.entity.FoodItem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final double DEFAULT_DELIVERY_FEE = 40.0;
    public static final double FREE_DELIVERY_THRESHOLD = 500.0;
    public static final double TAX_RATE = 0.05; // 5% GST

    private final Map<Long, CartItemDto> items = new LinkedHashMap<>();
    private Coupon appliedCoupon;
    private String couponMessage;

    public Cart() {
    }

    public synchronized void addItem(FoodItem food, int quantity) {
        if (food == null || quantity <= 0) return;
        
        CartItemDto existing = items.get(food.getId());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            existing.setAvailable(food.isAvailable());
        } else {
            String restaurantName = (food.getRestaurant() != null) ? food.getRestaurant().getName() : "FoodExpress Kitchen";
            CartItemDto dto = new CartItemDto(
                food.getId(),
                food.getName(),
                food.getPrice(),
                quantity,
                food.getImageUrl(),
                food.isVegetarian(),
                restaurantName,
                food.isAvailable()
            );
            items.put(food.getId(), dto);
        }
    }

    public synchronized void updateQuantity(Long foodId, int quantity) {
        if (!items.containsKey(foodId)) return;
        if (quantity <= 0) {
            items.remove(foodId);
        } else {
            items.get(foodId).setQuantity(quantity);
        }
    }

    public synchronized void removeItem(Long foodId) {
        items.remove(foodId);
    }

    public synchronized void clear() {
        items.clear();
        appliedCoupon = null;
        couponMessage = null;
    }

    public List<CartItemDto> getItemsList() {
        return new ArrayList<>(items.values());
    }

    public int getTotalItemCount() {
        return items.values().stream().mapToInt(CartItemDto::getQuantity).sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double getSubtotal() {
        double subtotal = items.values().stream()
                .mapToDouble(CartItemDto::getSubtotal)
                .sum();
        return round(subtotal);
    }

    public double getTaxAmount() {
        double tax = getSubtotal() * TAX_RATE;
        return round(tax);
    }

    public double getDeliveryFee() {
        if (isEmpty()) return 0.0;
        
        // If coupon provides free delivery
        if (appliedCoupon != null && "FREE_DELIVERY".equalsIgnoreCase(appliedCoupon.getDiscountType())) {
            return 0.0;
        }
        
        // Free delivery on orders above threshold
        if (getSubtotal() >= FREE_DELIVERY_THRESHOLD) {
            return 0.0;
        }
        
        return DEFAULT_DELIVERY_FEE;
    }

    public double getDiscountAmount() {
        if (appliedCoupon == null || isEmpty()) return 0.0;

        double subtotal = getSubtotal();
        if (subtotal < appliedCoupon.getMinOrderValue()) {
            return 0.0;
        }

        double discount = 0.0;
        if ("PERCENTAGE".equalsIgnoreCase(appliedCoupon.getDiscountType())) {
            discount = subtotal * (appliedCoupon.getDiscountValue() / 100.0);
            if (appliedCoupon.getMaxDiscount() != null && discount > appliedCoupon.getMaxDiscount()) {
                discount = appliedCoupon.getMaxDiscount();
            }
        } else if ("FLAT".equalsIgnoreCase(appliedCoupon.getDiscountType())) {
            discount = appliedCoupon.getDiscountValue();
        } else if ("FREE_DELIVERY".equalsIgnoreCase(appliedCoupon.getDiscountType())) {
            discount = DEFAULT_DELIVERY_FEE;
        }

        if (discount > subtotal) {
            discount = subtotal;
        }

        return round(discount);
    }

    public double getGrandTotal() {
        if (isEmpty()) return 0.0;
        double total = getSubtotal() + getTaxAmount() + getDeliveryFee() - getDiscountAmount();
        return round(Math.max(0.0, total));
    }

    public boolean hasUnavailableItems() {
        return items.values().stream().anyMatch(item -> !item.isAvailable());
    }

    private double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public Map<Long, CartItemDto> getItems() {
        return items;
    }

    public Coupon getAppliedCoupon() {
        return appliedCoupon;
    }

    public void setAppliedCoupon(Coupon appliedCoupon) {
        this.appliedCoupon = appliedCoupon;
    }

    public String getCouponMessage() {
        return couponMessage;
    }

    public void setCouponMessage(String couponMessage) {
        this.couponMessage = couponMessage;
    }
}
