package com.foodexpress.repository;

import com.foodexpress.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    List<PaymentMethod> findByUserIdOrderByIsDefaultDescCreatedAtDesc(Long userId);

    Optional<PaymentMethod> findByIdAndUserId(Long id, Long userId);

    List<PaymentMethod> findByUserIdAndType(Long userId, String type);

    Optional<PaymentMethod> findFirstByUserIdAndIsDefaultTrue(Long userId);

    void deleteByIdAndUserId(Long id, Long userId);
}
