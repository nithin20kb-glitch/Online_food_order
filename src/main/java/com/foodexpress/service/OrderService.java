package com.foodexpress.service;

import com.foodexpress.dto.Cart;
import com.foodexpress.dto.CartItemDto;
import com.foodexpress.dto.CheckoutForm;
import com.foodexpress.entity.FoodItem;
import com.foodexpress.entity.Order;
import com.foodexpress.entity.OrderItem;
import com.foodexpress.entity.User;
import com.foodexpress.repository.FoodItemRepository;
import com.foodexpress.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final FoodItemRepository foodItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, FoodItemRepository foodItemRepository) {
        this.orderRepository = orderRepository;
        this.foodItemRepository = foodItemRepository;
    }

    public Order placeOrder(User user, Cart cart, CheckoutForm form) {
        if (cart == null || cart.isEmpty()) {
            throw new IllegalStateException("Your shopping cart is empty.");
        }

        if (cart.hasUnavailableItems()) {
            throw new IllegalStateException("Some items in your cart are currently out of stock. Please remove them before placing your order.");
        }

        // Generate human-friendly Unique Order Tracking Number
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 9999);
        String trackingNumber = "FX-ORD-" + datePart + "-" + randomPart;

        Order order = new Order();
        order.setOrderTrackingNumber(trackingNumber);
        order.setUser(user);
        order.setCustomerName(form.getCustomerName().trim());
        order.setCustomerEmail(user.getEmail());
        order.setCustomerPhone(form.getCustomerPhone().trim());
        order.setDeliveryAddress(form.getDeliveryAddress().trim());
        order.setDeliveryCity(form.getDeliveryCity().trim());
        order.setDeliveryZip(form.getDeliveryZip().trim());
        order.setOrderNotes(form.getOrderNotes());

        // Price calculations
        order.setSubtotal(cart.getSubtotal());
        order.setTaxAmount(cart.getTaxAmount());
        order.setDeliveryFee(cart.getDeliveryFee());
        order.setDiscountAmount(cart.getDiscountAmount());
        order.setTotalAmount(cart.getGrandTotal());

        if (cart.getAppliedCoupon() != null) {
            order.setCouponCode(cart.getAppliedCoupon().getCode());
        }

        order.setPaymentMethod(form.getPaymentMethod());
        // For COD, payment is pending on delivery; for online methods (UPI, CARD, NET_BANKING), gateway simulation confirms payment
        if ("COD".equalsIgnoreCase(form.getPaymentMethod())) {
            order.setPaymentStatus("PENDING_ON_DELIVERY");
        } else {
            order.setPaymentStatus("PAID");
        }

        order.setOrderStatus("PLACED");
        order.setOrderDate(LocalDateTime.now());
        // Estimated delivery: 35 minutes from now
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(35));

        // Create OrderItems
        for (CartItemDto cartItem : cart.getItemsList()) {
            FoodItem foodItem = foodItemRepository.findById(cartItem.getFoodId())
                    .orElseThrow(() -> new IllegalStateException("Food item not found: " + cartItem.getFoodName()));

            OrderItem orderItem = new OrderItem(
                    order,
                    foodItem,
                    foodItem.getName(),
                    foodItem.getPrice(),
                    cartItem.getQuantity(),
                    cartItem.getSubtotal()
            );
            order.addOrderItem(orderItem);
        }

        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public Optional<Order> getOrderByTrackingNumber(String trackingNumber) {
        return orderRepository.findByOrderTrackingNumber(trackingNumber);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public List<Order> getOrdersByStatus(String status) {
        if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
            return getAllOrders();
        }
        return orderRepository.findByOrderStatusOrderByOrderDateDesc(status.toUpperCase().trim());
    }

    public Order updateOrderStatus(Long orderId, String newStatus, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        order.setOrderStatus(newStatus.toUpperCase().trim());
        if (reason != null && !reason.isBlank()) {
            order.setRejectionReason(reason.trim());
        }

        // If rejected/cancelled, handle simulated refund
        if ("REJECTED_REFUNDED".equalsIgnoreCase(newStatus) || "CANCELLED".equalsIgnoreCase(newStatus)) {
            order.setOrderStatus("REJECTED_REFUNDED");
            if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
                order.setPaymentStatus("REFUNDED");
            }
        } else if ("DELIVERED".equalsIgnoreCase(newStatus)) {
            if ("PENDING_ON_DELIVERY".equalsIgnoreCase(order.getPaymentStatus())) {
                order.setPaymentStatus("PAID");
            }
        }

        return orderRepository.save(order);
    }

    public long getTotalOrdersCount() {
        return orderRepository.count();
    }

    public double getTotalSalesAmount() {
        Double total = orderRepository.calculateTotalSales();
        return (total != null) ? total : 0.0;
    }

    public long getPendingOrdersCount() {
        return orderRepository.countByOrderStatus("PLACED") + orderRepository.countByOrderStatus("ACCEPTED");
    }
}
