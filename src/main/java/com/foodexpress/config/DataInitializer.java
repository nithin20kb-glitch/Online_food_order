package com.foodexpress.config;

import com.foodexpress.entity.*;
import com.foodexpress.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodItemRepository foodItemRepository;
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           RestaurantRepository restaurantRepository,
                           FoodItemRepository foodItemRepository,
                           CouponRepository couponRepository,
                           OrderRepository orderRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.restaurantRepository = restaurantRepository;
        this.foodItemRepository = foodItemRepository;
        this.couponRepository = couponRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Initialize Roles
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_CUSTOMER")));
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        // 2. Initialize Admin User
        if (!userRepository.existsByEmail("admin@foodexpress.com")) {
            User admin = new User(
                    "System Administrator",
                    "admin@foodexpress.com",
                    passwordEncoder.encode("admin123"),
                    "9876543210",
                    "FoodExpress Central HQ, Suite 400",
                    "Metro City",
                    "560001"
            );
            Set<Role> roles = new HashSet<>();
            roles.add(customerRole);
            roles.add(adminRole);
            admin.setRoles(roles);
            userRepository.save(admin);
        }

        // 3. Initialize Demo Customer
        User demoCustomer;
        if (!userRepository.existsByEmail("rahul@example.com")) {
            demoCustomer = new User(
                    "Rahul Sharma",
                    "rahul@example.com",
                    passwordEncoder.encode("password123"),
                    "9812345678",
                    "Flat 402, Green Valley Apartments, Indiranagar",
                    "Metro City",
                    "560038"
            );
            Set<Role> roles = new HashSet<>();
            roles.add(customerRole);
            demoCustomer.setRoles(roles);
            demoCustomer = userRepository.save(demoCustomer);
        } else {
            demoCustomer = userRepository.findByEmail("rahul@example.com").orElse(null);
        }

        if (!userRepository.existsByEmail("priya@example.com")) {
            User priya = new User(
                    "Priya Patel",
                    "priya@example.com",
                    passwordEncoder.encode("password123"),
                    "9823456789",
                    "Villa 12, Palm Meadows, Whitefield",
                    "Metro City",
                    "560066"
            );
            Set<Role> roles = new HashSet<>();
            roles.add(customerRole);
            priya.setRoles(roles);
            userRepository.save(priya);
        }

        // 4. Initialize Restaurants
        if (restaurantRepository.count() == 0) {
            restaurantRepository.saveAll(List.of(
                new Restaurant(
                    "FoodExpress Signature Kitchen",
                    "North Indian, Mughlai & Biryani",
                    "Indiranagar",
                    "100ft Road, 4th Block, Indiranagar",
                    4.8, 30, 150.0,
                    "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=800&q=80"
                ),
                new Restaurant(
                    "Spice Royale Dum Biryani",
                    "Hyderabadi Biryani & Kebabs",
                    "Whitefield",
                    "Main ITPL Road, Whitefield",
                    4.7, 35, 200.0,
                    "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=800&q=80"
                ),
                new Restaurant(
                    "Bella Cucina Italian Pizzeria",
                    "Wood-fired Pizza & Pasta",
                    "Koramangala",
                    "5th Block, Koramangala",
                    4.6, 25, 250.0,
                    "https://images.unsplash.com/photo-1552566626-52f8b828add9?auto=format&fit=crop&w=800&q=80"
                ),
                new Restaurant(
                    "Golden Dragon Pan-Asian",
                    "Chinese, Dimsums & Noodles",
                    "MG Road",
                    "Church Street, Off MG Road",
                    4.5, 40, 180.0,
                    "https://images.unsplash.com/photo-1544148103-0773bf10d330?auto=format&fit=crop&w=800&q=80"
                )
            ));
        }

        // 5. Initialize Categories
        if (categoryRepository.count() == 0) {
            categoryRepository.saveAll(List.of(
                new Category("Biryani & Rice", "biryani-rice", "Fragrant dum cooked royal basmati biryanis and pulaos",
                        "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=600&q=80"),
                new Category("Pizzas & Burgers", "pizzas-burgers", "Artisanal stone-baked pizzas and juicy gourmet burgers",
                        "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=600&q=80"),
                new Category("North Indian Curries", "north-indian", "Rich gravies, paneer specialties, butter chicken and naan",
                        "https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?auto=format&fit=crop&w=600&q=80"),
                new Category("Chinese & Asian", "chinese-asian", "Sizzling noodles, spicy fried rice, dimsums and Manchurian",
                        "https://images.unsplash.com/photo-1585032226651-759b368d7246?auto=format&fit=crop&w=600&q=80"),
                new Category("Desserts & Beverages", "desserts-beverages", "Decadent sweet treats, brownies, milkshakes and mojitos",
                        "https://images.unsplash.com/photo-1551024709-8f23befc6f87?auto=format&fit=crop&w=600&q=80"),
                new Category("Starters & Quick Bites", "starters-quick-bites", "Crispy appetizers, kebabs, wraps and French fries",
                        "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?auto=format&fit=crop&w=600&q=80")
            ));
        }

        // 6. Initialize Food Items
        if (foodItemRepository.count() == 0) {
            Category catBiryani = categoryRepository.findBySlug("biryani-rice").orElse(null);
            Category catPizza = categoryRepository.findBySlug("pizzas-burgers").orElse(null);
            Category catCurry = categoryRepository.findBySlug("north-indian").orElse(null);
            Category catAsian = categoryRepository.findBySlug("chinese-asian").orElse(null);
            Category catDessert = categoryRepository.findBySlug("desserts-beverages").orElse(null);
            Category catStarters = categoryRepository.findBySlug("starters-quick-bites").orElse(null);

            List<Restaurant> restList = restaurantRepository.findAll();
            Restaurant r1 = restList.get(0);
            Restaurant r2 = restList.size() > 1 ? restList.get(1) : r1;
            Restaurant r3 = restList.size() > 2 ? restList.get(2) : r1;
            Restaurant r4 = restList.size() > 3 ? restList.get(3) : r1;

            foodItemRepository.saveAll(List.of(
                new FoodItem("Hyderabadi Chicken Dum Biryani",
                        "Traditional slow-cooked dum biryani with tender chicken pieces, aromatic basmati rice, mint, saffron, and boiled egg. Served with mirchi ka salan and raita.",
                        320.0, "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=800&q=80",
                        catBiryani, r1, true, false, true, 4.9, 25),

                new FoodItem("Royal Shahi Paneer Dum Biryani",
                        "Fragrant basmati rice layered with marinated cottage cheese cubes, caramelized onions, kewra water, and toasted cashews.",
                        280.0, "https://images.unsplash.com/photo-1642821373181-696a54913e9a?auto=format&fit=crop&w=800&q=80",
                        catBiryani, r2, true, true, true, 4.7, 25),

                new FoodItem("Butter Chicken Special with Butter Naan",
                        "Tender tandoori chicken simmered in a velvety tomato, butter, and cashew nut gravy served with 2 freshly baked butter naans.",
                        340.0, "https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?auto=format&fit=crop&w=800&q=80",
                        catCurry, r1, true, false, true, 4.8, 20),

                new FoodItem("Paneer Tikka Masala Deluxe",
                        "Charcoal-grilled cottage cheese cubes simmered in a spiced onion-tomato gravy with bell peppers and fresh coriander.",
                        290.0, "https://images.unsplash.com/photo-1565557623262-b51c2513a641?auto=format&fit=crop&w=800&q=80",
                        catCurry, r1, true, true, false, 4.6, 20),

                new FoodItem("Margherita Quattro Formaggi Pizza",
                        "12-inch hand-stretched dough topped with San Marzano tomato sauce, fresh mozzarella, cheddar, gorgonzola, and basil leaves.",
                        380.0, "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=800&q=80",
                        catPizza, r3, true, true, true, 4.8, 20),

                new FoodItem("Smoky BBQ Bacon & Double Cheese Burger",
                        "Juicy grilled patty stacked with melted aged cheddar, crispy onion rings, dill pickles, and homemade hickory BBQ sauce.",
                        260.0, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=800&q=80",
                        catPizza, r3, true, false, true, 4.7, 15),

                new FoodItem("Schezwan Hakka Noodles with Veg Manchurian",
                        "Wok-tossed noodles with crunchy seasonal vegetables in spicy Schezwan sauce, accompanied by steamed vegetable Manchurian balls.",
                        240.0, "https://images.unsplash.com/photo-1585032226651-759b368d7246?auto=format&fit=crop&w=800&q=80",
                        catAsian, r4, true, true, false, 4.5, 18),

                new FoodItem("Steamed Chicken Dimsums (8 Pcs)",
                        "Delicate translucent dumplings filled with minced chicken, spring onions, and ginger, served with fiery chili-garlic dip.",
                        210.0, "https://images.unsplash.com/photo-1496116218417-1a781b1c416c?auto=format&fit=crop&w=800&q=80",
                        catAsian, r4, true, false, false, 4.6, 15),

                new FoodItem("Crispy Peri-Peri Paneer Bites",
                        "Golden crunchy paneer cubes dusted with African bird eye peri-peri seasoning and served with creamy mint mayonnaise.",
                        180.0, "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?auto=format&fit=crop&w=800&q=80",
                        catStarters, r1, true, true, false, 4.5, 12),

                new FoodItem("Belgian Sizzling Chocolate Brownie",
                        "Warm walnut chocolate fudge brownie topped with a scoop of vanilla bean ice cream and smothered in hot Belgian chocolate fudge sauce.",
                        190.0, "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=800&q=80",
                        catDessert, r1, true, true, true, 4.9, 10),

                new FoodItem("Fresh Mango Alphonso Thick Shake",
                        "Rich summer delight blended with sweet Ratnagiri Alphonso mango pulp, chilled whole milk, and scoops of mango ice cream.",
                        150.0, "https://images.unsplash.com/photo-1551024709-8f23befc6f87?auto=format&fit=crop&w=800&q=80",
                        catDessert, r2, true, true, false, 4.7, 8),

                new FoodItem("Tandoori Chicken Tikka Platter",
                        "Boneless chicken marinated in hung curd, Kashmiri red chili, and mustard oil, roasted to smoky perfection in clay tandoor.",
                        310.0, "https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?auto=format&fit=crop&w=800&q=80",
                        catStarters, r1, true, false, true, 4.8, 22)
            ));
        }

        // 7. Initialize Coupons
        if (couponRepository.count() == 0) {
            couponRepository.saveAll(List.of(
                new Coupon("WELCOME50", "PERCENTAGE", 50.0, 200.0, 100.0, "50% OFF up to ₹100 on your order (Min order ₹200)", true),
                new Coupon("TASTY20", "PERCENTAGE", 20.0, 300.0, 150.0, "Flat 20% OFF up to ₹150 (Min order ₹300)", true),
                new Coupon("FREEDEL", "FREE_DELIVERY", 40.0, 150.0, 40.0, "Free Delivery on orders above ₹150", true),
                new Coupon("SUPER100", "FLAT", 100.0, 500.0, 100.0, "Flat ₹100 instant discount on orders above ₹500", true)
            ));
        }

        // 8. Seed Sample Completed Orders if demoCustomer exists and no orders
        if (demoCustomer != null && orderRepository.count() == 0) {
            List<FoodItem> items = foodItemRepository.findAll();
            if (!items.isEmpty()) {
                Order order1 = new Order();
                order1.setOrderTrackingNumber("FX-ORD-20261109-1001");
                order1.setUser(demoCustomer);
                order1.setCustomerName(demoCustomer.getFullName());
                order1.setCustomerEmail(demoCustomer.getEmail());
                order1.setCustomerPhone(demoCustomer.getPhone());
                order1.setDeliveryAddress(demoCustomer.getDeliveryAddress());
                order1.setDeliveryCity(demoCustomer.getCity());
                order1.setDeliveryZip(demoCustomer.getPostalCode());
                order1.setSubtotal(600.0);
                order1.setTaxAmount(30.0);
                order1.setDeliveryFee(0.0);
                order1.setDiscountAmount(100.0);
                order1.setTotalAmount(530.0);
                order1.setCouponCode("WELCOME50");
                order1.setPaymentMethod("UPI");
                order1.setPaymentStatus("PAID");
                order1.setOrderStatus("DELIVERED");
                order1.setOrderDate(LocalDateTime.now().minusHours(2));
                order1.setEstimatedDeliveryTime(LocalDateTime.now().minusHours(1));

                order1.addOrderItem(new OrderItem(order1, items.get(0), items.get(0).getName(), items.get(0).getPrice(), 1, items.get(0).getPrice()));
                if (items.size() > 1) {
                    order1.addOrderItem(new OrderItem(order1, items.get(1), items.get(1).getName(), items.get(1).getPrice(), 1, items.get(1).getPrice()));
                }
                orderRepository.save(order1);

                Order order2 = new Order();
                order2.setOrderTrackingNumber("FX-ORD-20261109-1002");
                order2.setUser(demoCustomer);
                order2.setCustomerName(demoCustomer.getFullName());
                order2.setCustomerEmail(demoCustomer.getEmail());
                order2.setCustomerPhone(demoCustomer.getPhone());
                order2.setDeliveryAddress(demoCustomer.getDeliveryAddress());
                order2.setDeliveryCity(demoCustomer.getCity());
                order2.setDeliveryZip(demoCustomer.getPostalCode());
                order2.setSubtotal(380.0);
                order2.setTaxAmount(19.0);
                order2.setDeliveryFee(40.0);
                order2.setDiscountAmount(0.0);
                order2.setTotalAmount(439.0);
                order2.setPaymentMethod("CARD");
                order2.setPaymentStatus("PAID");
                order2.setOrderStatus("OUT_FOR_DELIVERY");
                order2.setOrderDate(LocalDateTime.now().minusMinutes(25));
                order2.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(15));

                if (items.size() > 4) {
                    order2.addOrderItem(new OrderItem(order2, items.get(4), items.get(4).getName(), items.get(4).getPrice(), 1, items.get(4).getPrice()));
                }
                orderRepository.save(order2);
            }
        }
    }
}
