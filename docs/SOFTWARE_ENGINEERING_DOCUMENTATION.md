# Team-3: Online Food Ordering and Restaurant Management System
## Software Engineering Capstone Project Documentation & SRS Report
**Platform Brand Name:** FoodExpress – Online Food Ordering  
**Academic Program:** B.Tech Computer Science and Engineering  
**Technology Stack:** Java 21, Spring Boot 3.3.5, Thymeleaf, Spring Data JPA, Hibernate, MySQL, HTML5/CSS3/JavaScript, Maven  

---

## 1. Project Overview and Problem Statement

### 1.1 Problem Statement
In traditional restaurant dining and manual phone-ordering setups, customers experience notable friction: difficulty browsing real-time menus, lack of transparency regarding item availability, inaccurate delivery fee calculations, inability to apply promotional discounts dynamically, opaque payment channels, and virtually zero visibility into food preparation and order transit stages. On the restaurateur side, manual order taking leads to order discrepancies, inventory mismanagement, delayed food dispatches, and an inability to track revenue analytics or handle order cancellations/refunds cleanly.

### 1.2 Proposed Solution: FoodExpress
FoodExpress is an enterprise-grade Model-View-Controller (MVC) online food ordering and restaurant management platform developed using Java 21, Spring Boot, and MySQL. It empowers customers to:
1. Provide their delivery location and immediately discover active local kitchens delivering to that zone.
2. Explore categorised culinary menus, inspect dish ingredients, and check stock availability.
3. Dynamically manage an interactive shopping cart with automated subtotal, 5% GST calculation, distance/order-tier delivery fees, and promo coupon discounts.
4. Complete orders through a simulated multi-channel payment gateway (UPI, Credit/Debit Card, Net Banking, Cash on Delivery).
5. Track real-time progress through an interactive visual timeline (`PLACED` &rarr; `ACCEPTED` &rarr; `PREPARING` &rarr; `OUT_FOR_DELIVERY` &rarr; `DELIVERED`).
6. Receive transparent cancellation notices and automated refund reconciliation if an order is rejected by the kitchen.
7. Provide restaurant managers with an administrative operations center to manage menu catalogs, toggle ingredient availability, process orders, and inspect revenue KPIs.

---

## 2. Objectives and Scope

### 2.1 Project Objectives
- **End-to-End Automation:** Automate the entire food ordering workflow from customer discovery to kitchen dispatch.
- **Location-Aware Discovery:** Filter restaurant kitchens and menus dynamically based on the customer's specified delivery neighborhood.
- **Accurate Mathematical Billing:** Ensure precision billing accounting for items, quantities, taxes, threshold-based delivery fees, and percentage or flat coupon deductions.
- **Transparent Status Tracking:** Provide a state-machine order tracking timeline so customers have full transparency throughout food preparation and delivery.
- **Administrative Empowerment:** Deliver a secure, responsive dashboard for restaurant operators to manage food items, prices, availability toggles, and live customer orders.
- **Enterprise Security:** Enforce BCrypt password hashing, session-based role-based access control (RBAC), and SQL injection prevention via JPA parameterization.

### 2.2 Project Scope
- **Included:** Customer registration/login, user profile editing, location-based kitchen filtering, menu search and categorization, shopping cart state management, coupon validation engine, simulated multi-channel payment gateway, live order tracking timeline, kitchen order acceptance/rejection with refund handling, admin KPI metrics dashboard, food item CRUD, stock availability toggles, and category management.
- **Excluded (Future Enhancements):** Real-time GPS driver tracking via Google Maps SDK, integration with live commercial banking payment gateways (e.g. Razorpay / Stripe production webhooks), and native Android/iOS mobile clients.

---

## 3. Requirement Elicitation & Analysis

### 3.1 Stakeholder Identification
1. **End-Customer (Guest & Authenticated User):** Desires rapid food discovery, fast checkout, discount savings, and real-time delivery transparency.
2. **Restaurant Manager / Administrator:** Desires real-time order visibility, control over inventory/menu stock, price update flexibility, and sales analytics.
3. **Delivery Partner (Simulated Agent):** Relies on delivery instructions, delivery address, and customer contact details.
4. **System Maintainer:** Demands clean modular code, relational database integrity, zero unhandled runtime exceptions, and comprehensive test coverage.

### 3.2 Elicitation Techniques Used
- **Domain Benchmarking:** Analyzed commercial food delivery platforms (Swiggy, Zomato, UberEats) to identify essential UI patterns and checkout mechanics.
- **Scenario Walkthroughs:** Mapped end-to-end customer journeys matching the official requirement sheet (location selection, menu browsing, cart adjustments, coupon applications, payment authorization, kitchen acceptance, and delivery handover).

---

## 4. Software Requirements Specification (SRS)

### 4.1 Functional Requirements (FR)

| Req ID | Module | Functional Description | Priority |
| :--- | :--- | :--- | :--- |
| **FR-01** | User / Auth | System shall allow customer registration with validation for name, unique email, 10-digit phone, address, and password &ge; 6 characters. | High |
| **FR-02** | User / Auth | System shall authenticate customers and administrators using BCrypt password hashing and Spring Security session cookies. | High |
| **FR-03** | User Profile | System shall allow authenticated users to view and update their profile, contact phone, and delivery street address. | Medium |
| **FR-04** | Discovery | System shall filter restaurants and menus according to user-selected delivery locations (e.g. Indiranagar, Whitefield, Koramangala, MG Road). | High |
| **FR-05** | Menu &amp; Search | System shall support full-text search by food name/description and category filtering (Biryani, Pizzas, Curries, Asian, Desserts). | High |
| **FR-06** | Cart Management | System shall allow adding items, increasing/decreasing quantities, removing items, and clearing the cart with live item count updates. | High |
| **FR-07** | Availability Check | System shall prevent ordering of out-of-stock items and warn customers if an item in their cart becomes unavailable. | High |
| **FR-08** | Pricing &amp; Taxes | System shall calculate subtotal, 5% GST tax, delivery charge (₹40, free if &ge; ₹500), and apply valid promotional coupons (`WELCOME50`, `TASTY20`, `FREEDEL`). | High |
| **FR-09** | Checkout &amp; Pay | System shall support realistic simulated payment channels: UPI / QR, Credit/Debit Card, Net Banking, and Cash on Delivery. | High |
| **FR-10** | Order Tracking | System shall render a 5-step visual timeline (`PLACED`, `ACCEPTED`, `PREPARING`, `OUT_FOR_DELIVERY`, `DELIVERED`). | High |
| **FR-11** | Cancellation/Refund | If restaurant rejects an order (`REJECTED_REFUNDED`), system shall record the reason, notify customer, and mark payment as `REFUNDED`. | High |
| **FR-12** | Admin Dashboard | System shall display KPI metrics: Total Orders, Gross Sales (₹), Registered Customers, and Active Food Items. | High |
| **FR-13** | Food Management | System shall allow administrators to create, edit, delete, and toggle in-stock availability of food items. | High |
| **FR-14** | Category Admin | System shall allow administrators to view, create, edit, and delete food categories. | Medium |
| **FR-15** | Order Management | System shall allow administrators to view all orders filtered by status and update order progression states. | High |

### 4.2 Non-Functional Requirements (NFR)
- **Performance:** Page render time under 500ms under local execution; database queries optimized via indexed primary and foreign keys.
- **Security:** Passwords encrypted using standard `BCryptPasswordEncoder` (strength 10); CSRF protection enabled on web forms; admin URLs restricted by role authorization (`ROLE_ADMIN`).
- **Reliability &amp; Availability:** Application designed with zero external runtime dependencies when running in development mode (H2 fallback); auto-reconnect and schema updates on MySQL.
- **Maintainability:** Standard layered package architecture (`config`, `controller`, `entity`, `repository`, `service`, `dto`).
- **Usability &amp; Responsiveness:** Mobile-responsive fluid CSS grid layout, visual dietary indicators (green veg / red non-veg), intuitive hover states, and clear notification toasts.

---

## 5. Software Development Life Cycle (SDLC) Model

### 5.1 Selected Model: Agile Scrum
For the FoodExpress project, the **Agile Scrum Methodology** was chosen over the traditional Waterfall model.

### 5.2 Justification for Agile Scrum
1. **Iterative Feature Delivery:** Online food ordering consists of distinct, testable user journeys (authentication &rarr; catalog browsing &rarr; cart/pricing &rarr; payment/order lifecycle &rarr; admin backoffice). Agile allowed each increment to be developed, integrated, and verified iteratively.
2. **Rapid Verification of Complex Workflows:** Order state machines (handling normal fulfillment vs. kitchen rejection with refund) required iterative unit and integration testing.
3. **Continuous UI/UX Refinement:** Developing the Thymeleaf templates alongside backend controllers allowed rapid adjustments to form validation messages, responsive breakpoints, and live order tracking timelines.
4. **Contrast with Waterfall:** In Waterfall, database schema changes discovered during frontend development cause costly backward revisions. Agile accommodated progressive enhancements (e.g. adding coupons, dietary badges, and delivery areas) without disrupting earlier deliverables.

---

## 6. System Architecture & Component Design

### 6.1 Architectural Pattern: Multi-Tier MVC
FoodExpress adheres strictly to the classic **Model-View-Controller (MVC)** architectural pattern:
- **Presentation Layer (View):** HTML5, CSS3, JavaScript, and Thymeleaf template engine rendering dynamic server-side pages with Spring Security dialect tags.
- **Controller Layer (Controller):** Spring MVC `@Controller` classes handling HTTP GET/POST endpoints, binding form DTOs, validating constraints, and invoking business services.
- **Business Logic Layer (Service):** Spring `@Service` classes orchestrating domain logic, pricing calculations, transactional order placement, and security checks.
- **Data Access Layer (Repository):** Spring Data JPA repository interfaces extending `JpaRepository` for relational database operations using Hibernate ORM.
- **Persistence Layer (Database):** MySQL 8.x relational database (with embedded H2 compatibility profile for isolated testing).

---

## 7. Software Engineering Diagrams

### 7.1 System Architecture Diagram
```
+-----------------------------------------------------------------------------------+
|                              PRESENTATION TIER (VIEW)                             |
|  HTML5 + CSS3 + JavaScript + Thymeleaf Engine (Responsive Desktop, Tablet, Mobile)|
|  Templates: index, menu, food-detail, cart, checkout, tracking, admin/dashboard   |
+-----------------------------------------------------------------------------------+
                                         |
                                  HTTPS / HTTP GET & POST
                                         v
+-----------------------------------------------------------------------------------+
|                            APPLICATION / CONTROLLER TIER                          |
|  Spring Security FilterChain (BCrypt, Role-Based Access Control: CUSTOMER, ADMIN)  |
|  Controllers: HomeController, MenuController, CartController, OrderController,    |
|               CheckoutController, AuthController, ProfileController, AdminController|
+-----------------------------------------------------------------------------------+
                                         |
                                Method Invocations
                                         v
+-----------------------------------------------------------------------------------+
|                               BUSINESS SERVICE TIER                               |
|  Services: UserService, FoodItemService, CategoryService, CartService,            |
|            OrderService (State Machine), CouponService, RestaurantService         |
+-----------------------------------------------------------------------------------+
                                         |
                              Spring Data JPA / Hibernate
                                         v
+-----------------------------------------------------------------------------------+
|                                 DATA ACCESS TIER                                  |
|  Repositories: UserRepository, FoodItemRepository, OrderRepository,              |
|                CategoryRepository, RestaurantRepository, CouponRepository         |
+-----------------------------------------------------------------------------------+
                                         |
                                     JDBC / SQL
                                         v
+-----------------------------------------------------------------------------------+
|                                 PERSISTENCE TIER                                  |
|                  MySQL Database 8.x (`foodexpress_db`)                            |
|    Tables: users, roles, categories, restaurants, food_items, orders, order_items |
+-----------------------------------------------------------------------------------+
```

### 7.2 Use-Case Diagram
```
+----------------------------------------------------------------------------------+
|                               FOODEXPRESS SYSTEM                                 |
|                                                                                  |
|    +-------------------------+                                                   |
|    | Browse Restaurant Menus | <-----------------------+                         |
|    +-------------------------+                         |                         |
|    +-------------------------+                         |                         |
|    | Search & Filter by Area | <-----------------------+                         |
|    +-------------------------+                         |                         |
|    +-------------------------+                         |                         |
|    | Customer Register/Login | <-----------------------+                         |
|    +-------------------------+                         |                         |
|    +-------------------------+                         |     <<Actor>>           |
|    | Manage Shopping Cart    | <-----------------------+--- Customer / Guest     |
|    +-------------------------+                         |                         |
|    +-------------------------+                         |                         |
|    | Apply Discount Coupon   | <-----------------------+                         |
|    +-------------------------+                         |                         |
|    +-------------------------+                         |                         |
|    | Place Order & Pay       | <-----------------------+                         |
|    +-------------------------+                         |                         |
|    +-------------------------+                         |                         |
|    | Track Live Order Status | <-----------------------+                         |
|    +-------------------------+                                                   |
|                                                                                  |
|    +-------------------------+                                                   |
|    | Admin Login             | <-----------------------+                         |
|    +-------------------------+                         |                         |
|    +-------------------------+                         |                         |
|    | Manage Foods (CRUD)     | <-----------------------+                         |
|    +-------------------------+                         |                         |
|    +-------------------------+                         |     <<Actor>>           |
|    | Toggle Item Stock       | <-----------------------+--- Restaurant Admin     |
|    +-------------------------+                         |                         |
|    | Manage Categories       | <-----------------------+                         |
|    +-------------------------+                         |                         |
|    | Update Order Status     | <-----------------------+                         |
|    +-------------------------+                         |                         |
|    | Reject & Refund Order   | <-----------------------+                         |
|    +-------------------------+                                                   |
+----------------------------------------------------------------------------------+
```

### 7.3 Swimlane Diagram (Order Placement & Fulfillment Lifecycle)
```
+--------------------+---------------------+----------------------+---------------------+
|      CUSTOMER      |    SYSTEM / CART    |   RESTAURANT ADMIN   |   DELIVERY AGENT    |
+--------------------+---------------------+----------------------+---------------------+
| 1. Select location |                     |                      |                     |
|    & browse menu   |                     |                      |                     |
|                    |                     |                      |                     |
| 2. Add dish to cart| 3. Check stock &    |                      |                     |
|                    |    calc subtotal/tax|                      |                     |
|                    |                     |                      |                     |
| 4. Enter coupon    | 5. Validate coupon  |                      |                     |
|    code & checkout |    & deduct discount|                      |                     |
|                    |                     |                      |                     |
| 6. Select payment  | 7. Authorize payment| 8. Order notification|                     |
|    & click "Pay"   |    & save Order     |    received in panel |                     |
|                    |                     |                      |                     |
|                    |                     | [Decision: Capacity?]|                     |
|                    |                     |  /                \  |                     |
|                    |                     | (No)              (Yes)                    |
|                    |                     |  |                  |                      |
|                    | 9. Process refund   | Reject & record     Accept & begin         |
| 10. Refund alert   |    & set REFUNDED   | reason              preparation            |
|     displayed      |                     |                     |                      |
|                    |                     |                     | 11. Food ready: mark |
|                    |                     |                     |     READY_FOR_PICKUP |
|                    |                     |                     |       |              |
|                    |                     |                     | 12. Collect package  |
|                    |                     |                     |     & OUT_FOR_DELIV  |
|                    | 13. Update tracking |                     |       |              |
| 14. Track progress |     timeline        |                     | 15. Handover food to |
|     on live screen |                     |                     |     customer         |
|                    |                     |                     |       |              |
|                    |                     |                     | 16. Mark DELIVERED   |
| 17. Enjoy meal!    |                     |                     |                      |
+--------------------+---------------------+----------------------+---------------------+
```

### 7.4 Database Entity-Relationship (ER) Design
```
+-------------------+        1:N        +-----------------------+
|    categories     |-------------------|      food_items       |
+-------------------+                   +-----------------------+
| id (PK)           |                   | id (PK)               |
| name              |                   | name                  |
| slug (UNIQUE)     |                   | description           |
| description       |                   | price                 |
| image_url         |                   | image_url             |
| is_active         |                   | category_id (FK)      |
+-------------------+                   | restaurant_id (FK)    |
                                        | is_available          |
+-------------------+        1:N        | is_vegetarian         |
|    restaurants    |-------------------| is_featured           |
+-------------------+                   | rating                |
| id (PK)           |                   | preparation_time_mins |
| name              |                   +-----------------------+
| cuisine_type      |                               |
| location_area     |                               | 1:N
| address           |                               v
| rating            |                   +-----------------------+
| delivery_time_mins|                   |      order_items      |
| min_order_amount  |                   +-----------------------+
| image_url         |                   | id (PK)               |
| is_open           |                   | order_id (FK)         |
+-------------------+                   | food_item_id (FK)     |
                                        | food_name             |
+-------------------+        M:N        | price                 |
|       roles       |======+            | quantity              |
+-------------------+      |            | subtotal              |
| id (PK)           |      |            +-----------------------+
| name (UNIQUE)     |      |                        ^
+-------------------+      |                        | 1:N
                           v                        |
+-------------------+-------------+     1:N     +-----------------------+
|       users       | user_roles  |-------------|        orders         |
+-------------------+-------------+             +-----------------------+
| id (PK)           | user_id (FK)|             | id (PK)               |
| full_name         | role_id (FK)|             | order_tracking_number |
| email (UNIQUE)    +-------------+             | user_id (FK)          |
| password (BCrypt)                             | customer_name         |
| phone                                         | customer_email        |
| delivery_address                              | customer_phone        |
| city                                          | delivery_address      |
| postal_code                                   | delivery_city         |
| enabled                                       | delivery_zip          |
| created_at                                    | order_notes           |
+-------------------+                           | subtotal              |
                                                | tax_amount (5% GST)   |
+-------------------+                           | delivery_fee          |
|      coupons      |                           | discount_amount       |
+-------------------+                           | total_amount          |
| id (PK)           |                           | coupon_code           |
| code (UNIQUE)     |                           | payment_method        |
| discount_type     |                           | payment_status        |
| discount_value    |                           | order_status          |
| min_order_value   |                           | rejection_reason      |
| max_discount      |                           | order_date            |
| description       |                           | est_delivery_time     |
| is_active         |                           +-----------------------+
+-------------------+
```

---

## 8. Agile/Scrum User Stories & Sprint Planning

### 8.1 User Stories

| Story ID | As a... | I want to... | So that... | Story Points |
| :--- | :--- | :--- | :--- | :--- |
| **US-01** | Hungry Customer | Specify my delivery neighborhood on the home page | I only see kitchens that deliver to my address. | 3 |
| **US-02** | Customer | Browse food items by category and filter for vegetarian items | I can quickly discover dishes matching my dietary choices. | 3 |
| **US-03** | Customer | Add food items to cart and adjust quantities with live price calculation | I know the exact subtotal, 5% GST, and delivery charges before paying. | 5 |
| **US-04** | Customer | Apply coupon code `WELCOME50` or `FREEDEL` at checkout | I receive instant promotional savings on my order. | 3 |
| **US-05** | Customer | Pay through simulated UPI, Card, NetBanking, or COD | I can complete my order safely with instant confirmation. | 5 |
| **US-06** | Customer | Track my order progression through a visual timeline | I know exactly when the food is accepted, cooking, and out for delivery. | 5 |
| **US-07** | Customer | Be notified and receive a full refund if the kitchen rejects my order | I don't lose money when a restaurant is overwhelmed. | 5 |
| **US-08** | Restaurant Admin | Log in securely to an administrative portal | Only authorized managers can edit menus and inspect financials. | 3 |
| **US-09** | Restaurant Admin | Add new dishes, edit prices, and toggle in-stock availability | The customer catalog reflects real-time kitchen inventory. | 5 |
| **US-10** | Restaurant Admin | Update order progression states and view total revenue metrics | I can manage kitchen throughput and monitor business growth. | 5 |

### 8.2 Sprint Planning Breakdown
- **Sprint 1 (Foundations & Architecture):** Setup Java 21, Spring Boot 3.3.5, Maven POM dependencies, Hibernate database schema (`foodexpress_db`), entity models, seed data initialization (`DataInitializer`), Spring Security configuration, and customer registration/login controllers.
- **Sprint 2 (Menu Catalog & Location Discovery):** Implement responsive Home page, Hero section with location selector, Categories browsing, Food details view, search bar filtering, and vegetarian toggle switches.
- **Sprint 3 (Cart, Calculations & Checkout Gateway):** Implement session-based Shopping Cart, item quantity controllers, coupon discount validation engine, subtotal/GST/delivery fee arithmetic, and multi-channel simulated checkout flow.
- **Sprint 4 (Order Lifecycle Tracking, Admin Operations & Quality Assurance):** Implement live 5-step visual tracking timeline, kitchen rejection & refund handling, admin KPI dashboard cards, Food CRUD management, stock availability switches, order book management, and JUnit 5 automated test suite execution.

---

## 9. DevOps Workflow & CI/CD Pipeline

### 9.1 DevOps Tools Used
- **Source Code Management:** Git (Feature branching: `main`, `develop`, `feature/*`).
- **Build & Dependency Management:** Apache Maven 3.9.10 with Maven Wrapper (`mvnw.cmd`).
- **Testing Framework:** JUnit 5, Spring Boot Test, Spring Security Test, AssertJ.
- **Continuous Integration (CI):** Automated GitHub Actions workflow triggered on every push and pull request to compile code on Java 21, execute unit tests, and build executable JARs.

### 9.2 GitHub Actions CI Pipeline Configuration (`.github/workflows/ci.yml`)
```yaml
name: FoodExpress Java 21 CI Pipeline

on:
  push:
    branches: [ "main", "develop" ]
  pull_request:
    branches: [ "main" ]

jobs:
  build-and-test:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v4

    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'
        cache: maven

    - name: Compile and Run Automated Tests
      run: ./mvnw clean test

    - name: Package Executable JAR
      run: ./mvnw package -DskipTests

    - name: Archive Build Artifact
      uses: actions/upload-artifact@v4
      with:
        name: foodexpress-app-jar
        path: target/*.jar
```

---

## 10. Software Testing Plan & Executed Test Cases

### 10.1 Testing Strategy
1. **Unit Testing:** Individual validation of entity logic, BCrypt hash verification, cart arithmetic, tax rates, delivery fee thresholds, and coupon deductions.
2. **Integration Testing:** Spring Boot context initialization, Spring Data JPA repository queries, and custom query methods.
3. **End-to-End Workflow Testing:** Complete simulated order placement, payment confirmation, multi-stage status advancement, and kitchen rejection with automatic refund reconciliation.
4. **Security Testing:** Access prevention on `/admin/**` endpoints for unauthenticated users and regular customers.

### 10.2 Test Execution Results Summary
All automated test cases were executed using JUnit 5 and the Maven Test Runner against the Spring Boot test context.

**Execution Output Summary:**
```
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time: 01:00 min
```

### 10.3 Detailed Test Cases Table

| Test Case ID | Test Module | Scenario Description | Inputs / Preconditions | Expected Result | Actual Result | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-01** | System Context | Spring Boot Application Context loads without bean conflicts. | Java 21 environment, `@SpringBootTest` | All repositories, services, and security configurations initialize. | HikariPool initialized; 8 JPA repos scanned; context loaded. | **PASS** |
| **TC-02** | User &amp; Security | Customer registration with secure BCrypt password hashing. | `fullName="Test User"`, `email="test@..."`, `password="SecretPass123"` | Password is never stored in plain text; BCrypt matcher validates `true`. | Plain-text mismatch confirmed; BCrypt match evaluated `true`. | **PASS** |
| **TC-03** | Cart Math | Shopping cart subtotal, 5% GST, free delivery threshold (&ge;₹500), and coupon deduction. | 1 Biryani (₹300) + 2 Brownies (₹200) = ₹500 subtotal; coupon `TASTY20` (20% off). | Subtotal: ₹500.00, Tax: ₹25.00, Delivery: ₹0.00, Discount: ₹100.00, Grand Total: ₹425.00. | Exact calculations verified to 2 decimal places. | **PASS** |
| **TC-04** | Inventory | Admin toggles food availability between In Stock and Out of Stock. | `foodItemService.toggleAvailability(id)` on existing food item. | `available` boolean flips cleanly and persists in database. | Initial state inverted from `true` to `false` and restored to `true`. | **PASS** |
| **TC-05** | Order Lifecycle | End-to-end customer order placement through complete 5-step status lifecycle. | Seeded customer `rahul@example.com`, cart with items, paymentMethod `UPI`. | Order created with `FX-ORD-*` ID, status progresses `PLACED` &rarr; `ACCEPTED` &rarr; `PREPARING` &rarr; `OUT_FOR_DELIVERY` &rarr; `DELIVERED`. | Order status transitioned cleanly across all 5 states; payment verified `PAID`. | **PASS** |
| **TC-06** | Rejection &amp; Refund | Kitchen rejects customer order due to overload; system triggers simulated refund. | Existing order with status `PAID`, admin sets status `REJECTED_REFUNDED`. | `orderStatus` becomes `REJECTED_REFUNDED`, `paymentStatus` switches to `REFUNDED`, reason saved. | Status updated to `REJECTED_REFUNDED`, payment marked `REFUNDED`, reason recorded. | **PASS** |
| **TC-07** | Out of Stock Guard | Customer attempts to checkout when item is out of stock. | Item in cart has `available=false`. | System prevents checkout with clear error message. | Error message rendered: "Some items in your cart are out of stock". | **PASS** (Manual / Web) |
| **TC-08** | RBAC Security | Anonymous user attempts to access `/admin/dashboard`. | Request `GET /admin/dashboard` without session. | Spring Security intercepts and redirects to `/login` or returns 403. | Redirection to `/login` enforced by SecurityFilterChain. | **PASS** (Security) |

---

## 11. Database Setup & MySQL Deployment Instructions

1. **Launch MySQL Server:** Ensure MySQL server is running on `localhost:3306` (e.g., via MySQL Workbench, XAMPP Control Panel, or Windows Service).
2. **Execute Database Script:**
   ```bash
   mysql -u root -p < sql/foodexpress_database.sql
   ```
   This will create the `foodexpress_db` database, all 9 relational tables, foreign key constraints, and pre-seed initial admin, customers, categories, restaurants, dishes, coupons, and orders.
3. **Verify Configuration:** Open `src/main/resources/application.properties` and verify your MySQL username and password (default is `root` with empty password).

---

## 12. Build and Run Instructions (Windows / VS Code)

### Prerequisites:
- Java JDK 21 installed.
- Terminal: Command Prompt or PowerShell in Visual Studio Code.

### Step-by-Step Commands:
1. **Open Workspace in VS Code:**
   Open folder `d:\documents(D)\newproject` in VS Code.
2. **Compile and Run Tests:**
   ```cmd
   .\mvnw.cmd test
   ```
3. **Run Application in Standard MySQL Mode:**
   ```cmd
   .\mvnw.cmd spring-boot:run
   ```
4. **Run Application in Instant Zero-Setup Dev Mode (H2 In-Memory Fallback):**
   ```cmd
   .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
   ```
5. **Open Website in Browser:**
   - **Customer Portal:** `http://localhost:8080/`
   - **Menu &amp; Restaurants:** `http://localhost:8080/menu`
   - **Admin Dashboard:** `http://localhost:8080/admin/dashboard`
   - **Admin Login:** `http://localhost:8080/admin/login`

### Default Pre-Seeded Accounts:
| Role | Email | Password | Permissions |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@foodexpress.com` | `admin123` | Full access to Admin Dashboard, Food CRUD, Stock Toggles, Orders, Categories |
| **Customer** | `rahul@example.com` | `password123` | Browse menu, Cart, Checkout, Order Tracking, Profile |
| **Customer 2** | `priya@example.com` | `password123` | Browse menu, Cart, Checkout, Order Tracking, Profile |
