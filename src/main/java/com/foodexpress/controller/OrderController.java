package com.foodexpress.controller;

import com.foodexpress.entity.Order;
import com.foodexpress.entity.User;
import com.foodexpress.service.OrderService;
import com.foodexpress.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/orders/confirmation/{trackingNumber}")
    public String orderConfirmation(@PathVariable("trackingNumber") String trackingNumber,
                                    Authentication authentication,
                                    Model model) {
        Optional<Order> optOrder = orderService.getOrderByTrackingNumber(trackingNumber);
        if (optOrder.isEmpty()) {
            return "redirect:/my-orders";
        }

        Order order = optOrder.get();
        // Ensure user can only view their own order (or admin)
        if (authentication != null && !hasAccess(order, authentication)) {
            return "redirect:/my-orders";
        }

        model.addAttribute("order", order);
        return "order-confirmation";
    }

    @GetMapping("/my-orders")
    public String myOrders(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        List<Order> orders = orderService.getOrdersByUser(user.getId());
        model.addAttribute("orders", orders);
        return "my-orders";
    }

    @GetMapping("/track/{trackingNumber}")
    public String trackOrder(@PathVariable("trackingNumber") String trackingNumber,
                             Authentication authentication,
                             Model model) {
        Optional<Order> optOrder = orderService.getOrderByTrackingNumber(trackingNumber);
        if (optOrder.isEmpty()) {
            return "redirect:/my-orders";
        }

        Order order = optOrder.get();
        if (authentication != null && !hasAccess(order, authentication)) {
            return "redirect:/my-orders";
        }

        model.addAttribute("order", order);
        return "order-tracking";
    }

    private boolean hasAccess(Order order, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));
        return isAdmin || (order.getUser() != null && order.getUser().getEmail().equalsIgnoreCase(auth.getName()));
    }
}
