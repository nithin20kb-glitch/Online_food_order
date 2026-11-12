package com.foodexpress;

import com.foodexpress.dto.Cart;
import com.foodexpress.dto.CheckoutForm;
import com.foodexpress.dto.UserRegistrationDto;
import com.foodexpress.entity.*;
import com.foodexpress.repository.*;
import com.foodexpress.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FoodExpressApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("Application Context Loads Successfully")
    void contextLoads() {
        assertNotNull(userService);
        assertNotNull(foodItemService);
        assertNotNull(orderService);
    }

    @Test
    @DisplayName("Customer Registration & Secure Password Hashing")
    void testCustomerRegistration() {
        String testEmail = "testuser" + System.currentTimeMillis() + "@foodexpress.com";
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setFullName("Test User");
        dto.setEmail(testEmail);
        dto.setPassword("SecretPass123");
        dto.setConfirmPassword("SecretPass123");
        dto.setPhone("9988776655");
        dto.setDeliveryAddress("123 Tech Park Road");
        dto.setCity("Metro City");
        dto.setPostalCode("560001");

        User savedUser = userService.registerCustomer(dto);

        assertNotNull(savedUser.getId());
        assertEquals("Test User", savedUser.getFullName());
        assertTrue(passwordEncoder.matches("SecretPass123", savedUser.getPassword()),
                "Stored password must be securely hashed and match original plain text");
        assertNotEquals("SecretPass123", savedUser.getPassword(),
                "Password must NEVER be stored as plain text");
        assertTrue(savedUser.hasRole("ROLE_CUSTOMER"));
    }

    @Test
    @DisplayName("Shopping Cart Mathematical Calculations & Coupon Discount")
    void testCartCalculationsAndCoupon() {
        Cart cart = new Cart();
        assertTrue(cart.isEmpty());

        // Create mock food items
        Category cat = new Category("Test Cat", "test-cat", "Desc", "img.jpg");
        Restaurant rest = new Restaurant("Test Rest", "Indian", "Downtown", "123 St", 4.5, 30, 100.0, "img.jpg");
        FoodItem item1 = new FoodItem("Biryani", "Desc", 300.0, "img.jpg", cat, rest, true, false, true, 4.8, 20);
        item1.setId(101L);

        FoodItem item2 = new FoodItem("Brownie", "Desc", 100.0, "img.jpg", cat, rest, true, true, false, 4.5, 10);
        item2.setId(102L);

        // Add 1 Biryani and 2 Brownies
        cart.addItem(item1, 1);
        cart.addItem(item2, 2);

        assertEquals(3, cart.getTotalItemCount());
        // Subtotal = 300 + (100 * 2) = 500
        assertEquals(500.0, cart.getSubtotal(), 0.01);
        // Tax 5% = 25.0
        assertEquals(25.0, cart.getTaxAmount(), 0.01);
        // Delivery fee for subtotal >= 500 is 0.0 (Free delivery)
        assertEquals(0.0, cart.getDeliveryFee(), 0.01);

        // Test Coupon Application: 20% discount (max 150)
        Coupon coupon = new Coupon("TASTY20", "PERCENTAGE", 20.0, 300.0, 150.0, "20% off", true);
        cart.setAppliedCoupon(coupon);

        // 20% of 500 = 100.0 discount
        assertEquals(100.0, cart.getDiscountAmount(), 0.01);
        // Grand Total = 500 + 25 + 0 - 100 = 425.0
        assertEquals(425.0, cart.getGrandTotal(), 0.01);
    }

    @Test
    @DisplayName("Food Item Availability Toggle")
    void testFoodItemAvailabilityToggle() {
        var items = foodItemService.getAvailableFoodItems();
        if (!items.isEmpty()) {
            FoodItem item = items.get(0);
            Long id = item.getId();
            boolean initial = item.isAvailable();

            FoodItem toggled = foodItemService.toggleAvailability(id);
            assertEquals(!initial, toggled.isAvailable());

            // Toggle back
            FoodItem restored = foodItemService.toggleAvailability(id);
            assertEquals(initial, restored.isAvailable());
        }
    }

    @Test
    @DisplayName("End-to-End Order Placement and Status Lifecycle")
    void testOrderPlacementAndStatusTransition() {
        Optional<User> optUser = userRepository.findByEmail("rahul@example.com");
        assertTrue(optUser.isPresent(), "Demo user rahul@example.com should be seeded");
        User user = optUser.get();

        var foodList = foodItemService.getAvailableFoodItems();
        assertFalse(foodList.isEmpty(), "Available food items should exist");
        FoodItem food = foodList.get(0);

        Cart cart = new Cart();
        cart.addItem(food, 2);

        CheckoutForm form = new CheckoutForm();
        form.setCustomerName("Rahul Sharma");
        form.setCustomerPhone("9812345678");
        form.setDeliveryAddress("Flat 402, Green Valley Apartments");
        form.setDeliveryCity("Metro City");
        form.setDeliveryZip("560038");
        form.setPaymentMethod("UPI");
        form.setUpiId("rahul@oksbi");

        Order order = orderService.placeOrder(user, cart, form);
        assertNotNull(order.getId());
        assertNotNull(order.getOrderTrackingNumber());
        assertEquals("PLACED", order.getOrderStatus());
        assertEquals("PAID", order.getPaymentStatus());
        assertEquals(1, order.getOrderItems().size());

        // Test status progression: PLACED -> ACCEPTED -> PREPARING -> OUT_FOR_DELIVERY -> DELIVERED
        Order accepted = orderService.updateOrderStatus(order.getId(), "ACCEPTED", null);
        assertEquals("ACCEPTED", accepted.getOrderStatus());

        Order preparing = orderService.updateOrderStatus(order.getId(), "PREPARING", null);
        assertEquals("PREPARING", preparing.getOrderStatus());

        Order outForDelivery = orderService.updateOrderStatus(order.getId(), "OUT_FOR_DELIVERY", null);
        assertEquals("OUT_FOR_DELIVERY", outForDelivery.getOrderStatus());

        Order delivered = orderService.updateOrderStatus(order.getId(), "DELIVERED", null);
        assertEquals("DELIVERED", delivered.getOrderStatus());
    }

    @Test
    @DisplayName("Order Rejection Triggers Simulated Refund")
    void testOrderRejectionAndRefund() {
        Optional<User> optUser = userRepository.findByEmail("rahul@example.com");
        User user = optUser.orElseThrow();

        FoodItem food = foodItemService.getAvailableFoodItems().get(0);
        Cart cart = new Cart();
        cart.addItem(food, 1);

        CheckoutForm form = new CheckoutForm();
        form.setCustomerName("Rahul Sharma");
        form.setCustomerPhone("9812345678");
        form.setDeliveryAddress("Indiranagar");
        form.setDeliveryCity("Metro City");
        form.setDeliveryZip("560038");
        form.setPaymentMethod("CARD");

        Order order = orderService.placeOrder(user, cart, form);
        assertEquals("PAID", order.getPaymentStatus());

        // Restaurant rejects order due to kitchen overload
        Order rejected = orderService.updateOrderStatus(order.getId(), "REJECTED_REFUNDED", "Restaurant kitchen at maximum capacity");
        assertEquals("REJECTED_REFUNDED", rejected.getOrderStatus());
        assertEquals("REFUNDED", rejected.getPaymentStatus(), "Customer payment must be refunded on rejection");
        assertEquals("Restaurant kitchen at maximum capacity", rejected.getRejectionReason());
    }
}
