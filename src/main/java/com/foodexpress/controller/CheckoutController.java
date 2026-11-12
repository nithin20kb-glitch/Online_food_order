package com.foodexpress.controller;

import com.foodexpress.dto.Cart;
import com.foodexpress.dto.CheckoutForm;
import com.foodexpress.entity.Order;
import com.foodexpress.entity.User;
import com.foodexpress.service.CartService;
import com.foodexpress.service.OrderService;
import com.foodexpress.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public CheckoutController(CartService cartService,
                              OrderService orderService,
                              UserService userService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(HttpSession session,
                                   Authentication authentication,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        Cart cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Please add items to proceed to checkout.");
            return "redirect:/menu";
        }

        if (cart.hasUnavailableItems()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Some items in your cart are currently out of stock. Please remove them before checkout.");
            return "redirect:/cart";
        }

        if (!model.containsAttribute("checkoutForm")) {
            CheckoutForm form = new CheckoutForm();
            if (authentication != null) {
                userService.findByEmail(authentication.getName()).ifPresent(user -> {
                    form.setCustomerName(user.getFullName());
                    form.setCustomerPhone(user.getPhone());
                    form.setDeliveryAddress(user.getDeliveryAddress());
                    form.setDeliveryCity(user.getCity());
                    form.setDeliveryZip(user.getPostalCode());
                });
            }
            if (cart.getAppliedCoupon() != null) {
                form.setCouponCode(cart.getAppliedCoupon().getCode());
            }
            model.addAttribute("checkoutForm", form);
        }

        model.addAttribute("cart", cart);
        return "checkout";
    }

    @PostMapping("/checkout/place-order")
    public String placeOrder(@Valid @ModelAttribute("checkoutForm") CheckoutForm form,
                             BindingResult bindingResult,
                             HttpSession session,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        Cart cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty.");
            return "redirect:/menu";
        }

        if (cart.hasUnavailableItems()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Some items in your cart are currently out of stock. Please update your cart.");
            return "redirect:/cart";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("cart", cart);
            return "checkout";
        }

        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));

        try {
            Order order = orderService.placeOrder(user, cart, form);
            // Clear cart upon successful order creation
            cartService.clearCart(session);
            redirectAttributes.addFlashAttribute("successOrder", true);
            return "redirect:/orders/confirmation/" + order.getOrderTrackingNumber();
        } catch (Exception e) {
            model.addAttribute("cart", cart);
            model.addAttribute("errorMessage", "Could not place order: " + e.getMessage());
            return "checkout";
        }
    }
}
