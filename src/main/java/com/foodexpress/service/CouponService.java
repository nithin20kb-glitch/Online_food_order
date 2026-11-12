package com.foodexpress.service;

import com.foodexpress.dto.Cart;
import com.foodexpress.entity.Coupon;
import com.foodexpress.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;

    @Autowired
    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public List<Coupon> getAllActiveCoupons() {
        return couponRepository.findAll().stream().filter(Coupon::isActive).toList();
    }

    public Optional<Coupon> findByCode(String code) {
        if (code == null || code.isBlank()) return Optional.empty();
        return couponRepository.findByCodeIgnoreCaseAndActiveTrue(code.trim());
    }

    public boolean applyCouponToCart(Cart cart, String couponCode) {
        if (cart == null || cart.isEmpty()) {
            return false;
        }

        Optional<Coupon> opt = findByCode(couponCode);
        if (opt.isEmpty()) {
            cart.setAppliedCoupon(null);
            cart.setCouponMessage("Invalid or expired coupon code: " + couponCode);
            return false;
        }

        Coupon coupon = opt.get();
        if (cart.getSubtotal() < coupon.getMinOrderValue()) {
            cart.setAppliedCoupon(null);
            cart.setCouponMessage(String.format("Coupon '%s' requires a minimum order value of ₹%.2f (Current: ₹%.2f)",
                    coupon.getCode(), coupon.getMinOrderValue(), cart.getSubtotal()));
            return false;
        }

        cart.setAppliedCoupon(coupon);
        cart.setCouponMessage(String.format("Coupon '%s' applied successfully! %s", coupon.getCode(), coupon.getDescription()));
        return true;
    }

    public void removeCouponFromCart(Cart cart) {
        if (cart != null) {
            cart.setAppliedCoupon(null);
            cart.setCouponMessage(null);
        }
    }

    public Coupon saveCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }
}
