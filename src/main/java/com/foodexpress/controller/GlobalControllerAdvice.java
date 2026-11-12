package com.foodexpress.controller;

import com.foodexpress.dto.Cart;
import com.foodexpress.entity.User;
import com.foodexpress.service.CartService;
import com.foodexpress.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final CartService cartService;
    private final UserService userService;

    @Autowired
    public GlobalControllerAdvice(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @ModelAttribute("cartItemCount")
    public int getCartItemCount(HttpSession session) {
        Cart cart = cartService.getCart(session);
        return cart != null ? cart.getTotalItemCount() : 0;
    }

    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equalsIgnoreCase("anonymousUser")) {
            Optional<User> opt = userService.findByEmail(auth.getName());
            return opt.orElse(null);
        }
        return null;
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));
        }
        return false;
    }
}
