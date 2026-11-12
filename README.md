# 🍔 FoodExpress – Online Food Ordering and Restaurant Management System
### Team-3: B.Tech Computer Science Software Engineering Capstone Project

**FoodExpress** is a full-stack, enterprise-grade restaurant food ordering and kitchen dispatch web platform developed using **Java 21**, **Spring Boot 3.3.5**, **Thymeleaf**, **Spring Data JPA**, **Hibernate**, and **MySQL**. It satisfies all compulsory topics and workflows from the official Software Engineering project specification sheet.

---

## 🌟 Key Modules and Features

### 1. 👤 Customer & Guest Module
- **Location-Based Discovery:** Customers select their delivery neighborhood (e.g., *Indiranagar, Whitefield, Koramangala, MG Road*) and view nearby kitchens and available menus.
- **Dynamic Food Menu & Search:** Real-time dish searching, category filtering (*Biryani & Rice, Pizzas & Burgers, North Indian, Chinese, Desserts*), and pure vegetarian toggle.
- **Detailed Dish Views:** Food images, price, ingredients, preparation time, and customer ratings.
- **Interactive Shopping Cart:** Live quantity modifiers (+/-), automatic subtotal, 5% GST calculation, dynamic delivery fee (Free on orders &ge; ₹500 or with coupon), and coupon application engine.
- **Checkout & Multi-Channel Payment Simulation:** Delivery address input, order notes, and realistic payment gateway simulation:
  - 📱 UPI / QR (Google Pay, PhonePe, Paytm, BHIM)
  - 💳 Credit / Debit Cards (with simulated card validation)
  - 🏦 Net Banking (SBI, HDFC, ICICI, Axis)
  - 💵 Cash on Delivery (COD)
- **Live Order Progress Timeline:** Step-by-step visual tracker:
  `Placed` &rarr; `Accepted` &rarr; `Preparing` &rarr; `Out for Delivery` &rarr; `Delivered`.
- **Cancellation & Automated Refund:** Transparent notification and simulated refund reconciliation if a kitchen rejects an order due to capacity constraints.
- **My Orders & User Profile:** View full order invoice history and update delivery addresses.

### 2. ⚡ Restaurant Admin Operations Center
- **Executive KPI Dashboard:** Real-time metrics for Total Orders, Gross Revenue (₹), Registered Customers, and Active Menu Items.
- **Food Catalog Management (CRUD):** Add new food items, edit existing recipes/prices, delete items, and upload image links.
- **Live Stock Availability Toggle:** 1-click toggle between *In Stock* and *Out of Stock* with automatic cart synchronization and checkout guards.
- **Category Management:** Create and organize food categories.
- **Live Order Book:** Review incoming orders and update fulfillment statuses with instant rejection/refund triggers.
- **Customer Directory:** Review registered customer details, locations, and join dates.

---

## 🛠️ Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend** | Java 21, Spring Boot 3.3.5 |
| **Security** | Spring Security 6, BCrypt Password Hashing, Session Management |
| **ORM & Persistence** | Spring Data JPA, Hibernate ORM |
| **Database** | MySQL 8.x (`foodexpress_db`) + Embedded H2 (instant fallback profile) |
| **Frontend UI** | HTML5, CSS3 (Modern Responsive Flexbox/Grid), JavaScript (ES6) |
| **Template Engine** | Thymeleaf + Thymeleaf Security 6 Dialect |
| **Build & Tooling** | Apache Maven 3.9.10 with `mvnw.cmd` wrapper, VS Code |
| **CI/CD** | GitHub Actions Pipeline (`.github/workflows/ci.yml`) |

---

## 📁 Package Organization (MVC Architecture)

```
d:\documents(D)\newproject\
├── src\main\java\com\foodexpress\
│   ├── FoodExpressApplication.java
│   ├── config\                 # SecurityConfig, CustomUserDetailsService, DataInitializer
│   ├── controller\             # HomeController, AuthController, MenuController, CartController,
│   │                           # CheckoutController, OrderController, AdminController, ProfileController
│   ├── dto\                    # Cart, CartItemDto, CheckoutForm, UserRegistrationDto
│   ├── entity\                 # User, Role, Category, Restaurant, FoodItem, Order, OrderItem, Coupon
│   ├── repository\             # UserRepository, RoleRepository, FoodItemRepository, OrderRepository...
│   └── service\                # UserService, FoodItemService, CartService, OrderService, CouponService...
├── src\main\resources\
│   ├── application.properties  # MySQL database configuration
│   ├── application-dev.properties # Embedded H2 instant zero-setup profile
│   ├── static\css\             # style.css, admin.css
│   ├── static\js\              # main.js, admin.js
│   └── templates\              # 16 Thymeleaf views (index, menu, cart, checkout, admin/*, etc.)
├── sql\
│   └── foodexpress_database.sql # Complete MySQL DDL schema and sample seed data
└── docs\
    └── SOFTWARE_ENGINEERING_DOCUMENTATION.md # Complete SRS, UML diagrams, Agile, Test Cases
```

---

## 🚀 How to Run in Visual Studio Code on Windows

### Option 1: Standard MySQL Mode (Recommended for Final Demo)

1. **Start MySQL Server:**
   Ensure MySQL is running (e.g. via XAMPP Control Panel, MySQL Workbench, or Windows Service on port `3306`).
2. **Import Database Schema & Data:**
   Open terminal in project root and run:
   ```cmd
   mysql -u root -p < sql/foodexpress_database.sql
   ```
   *(Or import `sql/foodexpress_database.sql` into phpMyAdmin / MySQL Workbench)*.
3. **Verify Database Credentials:**
   Check `src/main/resources/application.properties` (defaults to user `root` with no password).
4. **Run Application:**
   ```cmd
   .\mvnw.cmd spring-boot:run
   ```

### Option 2: Instant Zero-Setup Dev Mode (Offline / In-Memory H2 Fallback)
If MySQL is not installed or currently stopped on the machine, you can run the entire platform instantly with zero configuration using the dev profile:
```cmd
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```
All tables, admin accounts, customer accounts, dishes, and orders will be initialized in-memory automatically on launch!

---

## 🌐 Application URLs

Once started, open your browser:
- **Customer Home Page:** [http://localhost:8080/](http://localhost:8080/)
- **Browse Food Menu:** [http://localhost:8080/menu](http://localhost:8080/menu)
- **Shopping Cart:** [http://localhost:8080/cart](http://localhost:8080/cart)
- **Customer Login:** [http://localhost:8080/login](http://localhost:8080/login)
- **Customer Registration:** [http://localhost:8080/register](http://localhost:8080/register)
- **Admin Dashboard:** [http://localhost:8080/admin/dashboard](http://localhost:8080/admin/dashboard)
- **Admin Direct Login:** [http://localhost:8080/admin/login](http://localhost:8080/admin/login)

---

## 🔑 Pre-Seeded Demonstration Accounts

| Account Type | Email Address | Password | Permissions & Role |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin@foodexpress.com` | `admin123` | Full access to Admin Dashboard, Food CRUD, Stock Toggles, Order Book, Categories |
| **Customer (Rahul Sharma)** | `rahul@example.com` | `password123` | Browse menu, Cart, Multi-Channel Checkout, Live Order Tracking, Previous Orders |
| **Customer (Priya Patel)** | `priya@example.com` | `password123` | Customer ordering & profile management |

*(Note: The login page includes 1-click quick-fill buttons to populate these credentials instantly during presentations!)*

---

## 🧪 Automated Testing & Verification

Run the complete JUnit 5 automated test suite:
```cmd
.\mvnw.cmd test
```

**Results:**
- `testCustomerRegistration`: Verified BCrypt password hashing and user role persistence.
- `testCartCalculationsAndCoupon`: Verified subtotal, 5% GST tax, delivery charge threshold, and 20% coupon discount mathematics.
- `testFoodItemAvailabilityToggle`: Verified admin stock toggle switch.
- `testOrderPlacementAndStatusTransition`: Verified complete 5-stage order progression (`PLACED` &rarr; `ACCEPTED` &rarr; `PREPARING` &rarr; `OUT_FOR_DELIVERY` &rarr; `DELIVERED`).
- `testOrderRejectionAndRefund`: Verified kitchen rejection handling and automated simulated refund trigger.
- **Pass Rate:** **100% (6 / 6 Tests Passed, 0 Failures, 0 Errors)**.

---

## 📚 Complete Software Engineering Documentation
Refer to [docs/SOFTWARE_ENGINEERING_DOCUMENTATION.md](file:///d:/documents(D)/newproject/docs/SOFTWARE_ENGINEERING_DOCUMENTATION.md) for the complete SRS, Agile user stories, sprint breakdown, DevOps CI/CD pipeline, and full test cases report.
