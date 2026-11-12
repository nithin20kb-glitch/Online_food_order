-- ==============================================================================
-- FoodExpress - Online Food Ordering and Restaurant Management System
-- Database Setup and Seed Script (MySQL 8.x / MariaDB compatible)
-- Team-3 Software Engineering Project
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS `foodexpress_db` 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `foodexpress_db`;

-- Drop tables in reverse order of foreign key dependency if recreating
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `order_items`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `coupons`;
DROP TABLE IF EXISTS `food_items`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `restaurants`;
DROP TABLE IF EXISTS `user_roles`;
DROP TABLE IF EXISTS `roles`;
DROP TABLE IF EXISTS `users`;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Roles Table
CREATE TABLE `roles` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `roles` (`id`, `name`) VALUES 
(1, 'ROLE_CUSTOMER'),
(2, 'ROLE_ADMIN');

-- 2. Users Table
-- Default Passwords (BCrypt hash for "admin123" and "customer123"):
-- $2a$10$wKkG0k1O9u9w2mJtqFqDte4lPzQe1N1mYm.C78Y5N2qQkU8JzQ1Oa = "password123"
CREATE TABLE `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(120) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `delivery_address` VARCHAR(255) NOT NULL,
    `city` VARCHAR(80) NOT NULL,
    `postal_code` VARCHAR(20) NOT NULL,
    `enabled` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Admin and Customer (passwords encoded with BCrypt: $2a$10$7Z7e1kU... for password123)
-- In the Spring Boot application, DataInitializer will also safely verify/seed these on startup.
INSERT INTO `users` (`id`, `full_name`, `email`, `password`, `phone`, `delivery_address`, `city`, `postal_code`, `enabled`, `created_at`) VALUES
(1, 'System Administrator', 'admin@foodexpress.com', '$2a$10$wKkG0k1O9u9w2mJtqFqDte4lPzQe1N1mYm.C78Y5N2qQkU8JzQ1Oa', '9876543210', 'FoodExpress HQ, Central Plaza', 'Metro City', '560001', TRUE, NOW()),
(2, 'Rahul Sharma', 'rahul@example.com', '$2a$10$wKkG0k1O9u9w2mJtqFqDte4lPzQe1N1mYm.C78Y5N2qQkU8JzQ1Oa', '9812345678', 'Flat 402, Green Valley Apts, Indiranagar', 'Metro City', '560038', TRUE, NOW()),
(3, 'Priya Patel', 'priya@example.com', '$2a$10$wKkG0k1O9u9w2mJtqFqDte4lPzQe1N1mYm.C78Y5N2qQkU8JzQ1Oa', '9823456789', 'Villa 12, Palm Meadows, Whitefield', 'Metro City', '560066', TRUE, NOW());

-- 3. User Roles Mapping Table
CREATE TABLE `user_roles` (
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    CONSTRAINT `fk_user_roles_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_roles_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
(1, 1),
(1, 2),
(2, 1),
(3, 1);

-- 4. Restaurants Table
CREATE TABLE `restaurants` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(120) NOT NULL,
    `cuisine_type` VARCHAR(100) NOT NULL,
    `location_area` VARCHAR(100) NOT NULL,
    `address` VARCHAR(255) NOT NULL,
    `rating` DECIMAL(2,1) NOT NULL DEFAULT 4.5,
    `delivery_time_minutes` INT NOT NULL DEFAULT 30,
    `min_order_amount` DECIMAL(8,2) NOT NULL DEFAULT 100.00,
    `image_url` VARCHAR(500),
    `is_open` BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `restaurants` (`id`, `name`, `cuisine_type`, `location_area`, `address`, `rating`, `delivery_time_minutes`, `min_order_amount`, `image_url`, `is_open`) VALUES
(1, 'FoodExpress Signature Kitchen', 'North Indian, Mughlai & Biryani', 'Indiranagar', '100ft Road, 4th Block, Indiranagar', 4.8, 30, 150.00, 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=800&q=80', TRUE),
(2, 'Spice Royale Dum Biryani', 'Hyderabadi Biryani & Kebabs', 'Whitefield', 'Main ITPL Road, Whitefield', 4.7, 35, 200.00, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=800&q=80', TRUE),
(3, 'Bella Cucina Italian Pizzeria', 'Wood-fired Pizza & Pasta', 'Koramangala', '5th Block, Koramangala', 4.6, 25, 250.00, 'https://images.unsplash.com/photo-1552566626-52f8b828add9?auto=format&fit=crop&w=800&q=80', TRUE),
(4, 'Golden Dragon Pan-Asian', 'Chinese, Dimsums & Noodles', 'MG Road', 'Church Street, Off MG Road', 4.5, 40, 180.00, 'https://images.unsplash.com/photo-1544148103-0773bf10d330?auto=format&fit=crop&w=800&q=80', TRUE);

-- 5. Categories Table
CREATE TABLE `categories` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(80) NOT NULL UNIQUE,
    `slug` VARCHAR(80) NOT NULL UNIQUE,
    `description` VARCHAR(255),
    `image_url` VARCHAR(500),
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `categories` (`id`, `name`, `slug`, `description`, `image_url`, `is_active`) VALUES
(1, 'Biryani & Rice', 'biryani-rice', 'Fragrant dum cooked royal basmati biryanis and pulaos', 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=600&q=80', TRUE),
(2, 'Pizzas & Burgers', 'pizzas-burgers', 'Artisanal stone-baked pizzas and juicy gourmet burgers', 'https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=600&q=80', TRUE),
(3, 'North Indian Curries', 'north-indian', 'Rich gravies, paneer specialties, butter chicken and naan', 'https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?auto=format&fit=crop&w=600&q=80', TRUE),
(4, 'Chinese & Asian', 'chinese-asian', 'Sizzling noodles, spicy fried rice, momos and Manchurian', 'https://images.unsplash.com/photo-1585032226651-759b368d7246?auto=format&fit=crop&w=600&q=80', TRUE),
(5, 'Desserts & Beverages', 'desserts-beverages', 'Decadent sweet treats, brownies, milkshakes and mojitos', 'https://images.unsplash.com/photo-1551024709-8f23befc6f87?auto=format&fit=crop&w=600&q=80', TRUE),
(6, 'Starters & Quick Bites', 'starters-quick-bites', 'Crispy appetizers, kebabs, wraps and French fries', 'https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?auto=format&fit=crop&w=600&q=80', TRUE);

-- 6. Food Items Table
CREATE TABLE `food_items` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(150) NOT NULL,
    `description` TEXT,
    `price` DECIMAL(8,2) NOT NULL,
    `image_url` VARCHAR(500),
    `category_id` BIGINT NOT NULL,
    `restaurant_id` BIGINT NOT NULL,
    `is_available` BOOLEAN NOT NULL DEFAULT TRUE,
    `is_vegetarian` BOOLEAN NOT NULL DEFAULT FALSE,
    `is_featured` BOOLEAN NOT NULL DEFAULT FALSE,
    `rating` DECIMAL(2,1) NOT NULL DEFAULT 4.5,
    `preparation_time_minutes` INT NOT NULL DEFAULT 20,
    CONSTRAINT `fk_food_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_food_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `food_items` (`id`, `name`, `description`, `price`, `image_url`, `category_id`, `restaurant_id`, `is_available`, `is_vegetarian`, `is_featured`, `rating`, `preparation_time_minutes`) VALUES
(1, 'Hyderabadi Chicken Dum Biryani', 'Traditional slow-cooked dum biryani with tender chicken pieces, aromatic basmati rice, mint, saffron, and boiled egg. Served with mirchi ka salan and raita.', 320.00, 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=800&q=80', 1, 1, TRUE, FALSE, TRUE, 4.9, 25),
(2, 'Royal Shahi Paneer Dum Biryani', 'Fragrant basmati rice layered with marinated cottage cheese cubes, caramelized onions, kewra water, and toasted cashews.', 280.00, 'https://images.unsplash.com/photo-1642821373181-696a54913e9a?auto=format&fit=crop&w=800&q=80', 1, 2, TRUE, TRUE, TRUE, 4.7, 25),
(3, 'Butter Chicken Special with Butter Naan', 'Tender tandoori chicken simmered in a velvety tomato, butter, and cashew nut gravy served with 2 freshly baked butter naans.', 340.00, 'https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?auto=format&fit=crop&w=800&q=80', 3, 1, TRUE, FALSE, TRUE, 4.8, 20),
(4, 'Paneer Tikka Masala Deluxe', 'Charcoal-grilled cottage cheese cubes simmered in a spiced onion-tomato gravy with bell peppers and fresh coriander.', 290.00, 'https://images.unsplash.com/photo-1565557623262-b51c2513a641?auto=format&fit=crop&w=800&q=80', 3, 1, TRUE, TRUE, FALSE, 4.6, 20),
(5, 'Margherita Quattro Formaggi Pizza', '12-inch hand-stretched dough topped with San Marzano tomato sauce, fresh mozzarella, cheddar, gorgonzola, and basil leaves.', 380.00, 'https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=800&q=80', 2, 3, TRUE, TRUE, TRUE, 4.8, 20),
(6, 'Smoky BBQ Bacon & Double Cheese Burger', 'Juicy grilled patty stacked with melted aged cheddar, crispy onion rings, dill pickles, and homemade hickory BBQ sauce.', 260.00, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=800&q=80', 2, 3, TRUE, FALSE, TRUE, 4.7, 15),
(7, 'Schezwan Hakka Noodles with Veg Manchurian', 'Wok-tossed noodles with crunchy seasonal vegetables in spicy Schezwan sauce, accompanied by steamed vegetable Manchurian balls.', 240.00, 'https://images.unsplash.com/photo-1585032226651-759b368d7246?auto=format&fit=crop&w=800&q=80', 4, 4, TRUE, TRUE, FALSE, 4.5, 18),
(8, 'Steamed Chicken Dimsums (8 Pcs)', 'Delicate translucent dumplings filled with minced chicken, spring onions, and ginger, served with fiery chili-garlic dip.', 210.00, 'https://images.unsplash.com/photo-1496116218417-1a781b1c416c?auto=format&fit=crop&w=800&q=80', 4, 4, TRUE, FALSE, FALSE, 4.6, 15),
(9, 'Crispy Peri-Peri Paneer Bites', 'Golden crunchy paneer cubes dusted with African bird eye peri-peri seasoning and served with creamy mint mayonnaise.', 180.00, 'https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?auto=format&fit=crop&w=800&q=80', 6, 1, TRUE, TRUE, FALSE, 4.5, 12),
(10, 'Belgian Sizzling Chocolate Brownie', 'Warm walnut chocolate fudge brownie topped with a scoop of vanilla bean ice cream and smothered in hot Belgian chocolate fudge sauce.', 190.00, 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=800&q=80', 5, 1, TRUE, TRUE, TRUE, 4.9, 10),
(11, 'Fresh Mango Alphonso Thick Shake', 'Rich summer delight blended with sweet Ratnagiri Alphonso mango pulp, chilled whole milk, and scoops of mango ice cream.', 150.00, 'https://images.unsplash.com/photo-1551024709-8f23befc6f87?auto=format&fit=crop&w=800&q=80', 5, 2, TRUE, TRUE, FALSE, 4.7, 8),
(12, 'Tandoori Chicken Tikka Platter', 'Boneless chicken marinated in hung curd, Kashmiri red chili, and mustard oil, roasted to smoky perfection in clay tandoor.', 310.00, 'https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?auto=format&fit=crop&w=800&q=80', 6, 1, TRUE, FALSE, TRUE, 4.8, 22);

-- 7. Coupons Table
CREATE TABLE `coupons` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(50) NOT NULL UNIQUE,
    `discount_type` VARCHAR(20) NOT NULL, -- PERCENTAGE, FLAT, FREE_DELIVERY
    `discount_value` DECIMAL(8,2) NOT NULL,
    `min_order_value` DECIMAL(8,2) NOT NULL DEFAULT 0.00,
    `max_discount` DECIMAL(8,2) NOT NULL DEFAULT 100.00,
    `description` VARCHAR(255),
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `coupons` (`id`, `code`, `discount_type`, `discount_value`, `min_order_value`, `max_discount`, `description`, `is_active`) VALUES
(1, 'WELCOME50', 'PERCENTAGE', 50.00, 200.00, 100.00, '50% OFF up to ₹100 on your order (Min order ₹200)', TRUE),
(2, 'TASTY20', 'PERCENTAGE', 20.00, 300.00, 150.00, 'Flat 20% OFF up to ₹150 (Min order ₹300)', TRUE),
(3, 'FREEDEL', 'FREE_DELIVERY', 40.00, 150.00, 40.00, 'Free Delivery on orders above ₹150', TRUE),
(4, 'SUPER100', 'FLAT', 100.00, 500.00, 100.00, 'Flat ₹100 instant discount on orders above ₹500', TRUE);

-- 8. Orders Table
CREATE TABLE `orders` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_tracking_number` VARCHAR(50) NOT NULL UNIQUE,
    `user_id` BIGINT NOT NULL,
    `customer_name` VARCHAR(100) NOT NULL,
    `customer_email` VARCHAR(120) NOT NULL,
    `customer_phone` VARCHAR(20) NOT NULL,
    `delivery_address` VARCHAR(255) NOT NULL,
    `delivery_city` VARCHAR(80) NOT NULL,
    `delivery_zip` VARCHAR(20) NOT NULL,
    `order_notes` TEXT,
    `subtotal` DECIMAL(10,2) NOT NULL,
    `tax_amount` DECIMAL(10,2) NOT NULL,
    `delivery_fee` DECIMAL(10,2) NOT NULL,
    `discount_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `total_amount` DECIMAL(10,2) NOT NULL,
    `coupon_code` VARCHAR(50),
    `payment_method` VARCHAR(30) NOT NULL,
    `payment_status` VARCHAR(30) NOT NULL,
    `order_status` VARCHAR(40) NOT NULL,
    `rejection_reason` VARCHAR(255),
    `order_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `estimated_delivery_time` DATETIME NOT NULL,
    CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Order Items Table
CREATE TABLE `order_items` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL,
    `food_item_id` BIGINT NOT NULL,
    `food_name` VARCHAR(150) NOT NULL,
    `price` DECIMAL(8,2) NOT NULL,
    `quantity` INT NOT NULL,
    `subtotal` DECIMAL(10,2) NOT NULL,
    CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_order_item_food` FOREIGN KEY (`food_item_id`) REFERENCES `food_items` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Sample Completed / In-Progress Orders for Rahul Sharma
INSERT INTO `orders` (`id`, `order_tracking_number`, `user_id`, `customer_name`, `customer_email`, `customer_phone`, `delivery_address`, `delivery_city`, `delivery_zip`, `subtotal`, `tax_amount`, `delivery_fee`, `discount_amount`, `total_amount`, `coupon_code`, `payment_method`, `payment_status`, `order_status`, `order_date`, `estimated_delivery_time`) VALUES
(1, 'FX-ORD-20261109-1001', 2, 'Rahul Sharma', 'rahul@example.com', '9812345678', 'Flat 402, Green Valley Apts, Indiranagar', 'Metro City', '560038', 600.00, 30.00, 0.00, 100.00, 530.00, 'WELCOME50', 'UPI', 'PAID', 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(2, 'FX-ORD-20261109-1002', 2, 'Rahul Sharma', 'rahul@example.com', '9812345678', 'Flat 402, Green Valley Apts, Indiranagar', 'Metro City', '560038', 380.00, 19.00, 40.00, 0.00, 439.00, NULL, 'CARD', 'PAID', 'OUT_FOR_DELIVERY', DATE_SUB(NOW(), INTERVAL 25 MINUTE), DATE_ADD(NOW(), INTERVAL 15 MINUTE));

INSERT INTO `order_items` (`id`, `order_id`, `food_item_id`, `food_name`, `price`, `quantity`, `subtotal`) VALUES
(1, 1, 1, 'Hyderabadi Chicken Dum Biryani', 320.00, 1, 320.00),
(2, 1, 2, 'Royal Shahi Paneer Dum Biryani', 280.00, 1, 280.00),
(3, 2, 5, 'Margherita Quattro Formaggi Pizza', 380.00, 1, 380.00);
