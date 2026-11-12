package com.foodexpress.repository;

import com.foodexpress.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    Optional<Order> findByOrderTrackingNumber(String orderTrackingNumber);
    List<Order> findAllByOrderByOrderDateDesc();
    List<Order> findByOrderStatusOrderByOrderDateDesc(String orderStatus);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o WHERE o.orderStatus <> 'REJECTED_REFUNDED' AND o.paymentStatus = 'PAID'")
    Double calculateTotalSales();

    long countByOrderStatus(String orderStatus);
}
