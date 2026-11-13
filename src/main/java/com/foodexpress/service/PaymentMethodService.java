package com.foodexpress.service;

import com.foodexpress.entity.PaymentMethod;
import com.foodexpress.entity.User;
import com.foodexpress.repository.PaymentMethodRepository;
import com.foodexpress.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    @Autowired
    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository, UserRepository userRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<PaymentMethod> getPaymentMethodsForUser(Long userId) {
        return paymentMethodRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
    }

    public PaymentMethod addCard(Long userId, String cardHolderName, String cardNumber, String cardExpiry, String nickname, boolean isDefault) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        String cleanedNumber = cardNumber != null ? cardNumber.replaceAll("[^0-9]", "") : "";
        if (cleanedNumber.length() < 13 || cleanedNumber.length() > 19) {
            throw new IllegalArgumentException("Invalid card number. Must be between 13 and 19 digits.");
        }

        String last4 = cleanedNumber.substring(cleanedNumber.length() - 4);
        String maskedNumber = "•••• •••• •••• " + last4;
        String brand = detectCardBrand(cleanedNumber);

        if (isDefault) {
            clearDefaultForUser(userId);
        } else {
            // If this is the user's first payment method, make it default automatically
            List<PaymentMethod> existing = paymentMethodRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
            if (existing.isEmpty()) {
                isDefault = true;
            }
        }

        PaymentMethod pm = new PaymentMethod();
        pm.setUser(user);
        pm.setType("CARD");
        pm.setCardHolderName(cardHolderName != null ? cardHolderName.trim().toUpperCase() : "CARDHOLDER");
        pm.setCardNumberMasked(maskedNumber);
        pm.setCardBrand(brand);
        pm.setCardExpiry(cardExpiry != null ? cardExpiry.trim() : "");
        pm.setNickname(nickname != null && !nickname.isBlank() ? nickname.trim() : brand + " ending in " + last4);
        pm.setDefault(isDefault);
        pm.setCreatedAt(LocalDateTime.now());

        return paymentMethodRepository.save(pm);
    }

    public PaymentMethod addUpi(Long userId, String upiId, String nickname, boolean isDefault) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (upiId == null || !upiId.contains("@") || upiId.trim().length() < 5) {
            throw new IllegalArgumentException("Please enter a valid UPI ID (e.g. yourname@okhdfcbank or 9876543210@upi).");
        }

        String cleanedUpi = upiId.trim().toLowerCase();

        if (isDefault) {
            clearDefaultForUser(userId);
        } else {
            List<PaymentMethod> existing = paymentMethodRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
            if (existing.isEmpty()) {
                isDefault = true;
            }
        }

        PaymentMethod pm = new PaymentMethod();
        pm.setUser(user);
        pm.setType("UPI");
        pm.setUpiId(cleanedUpi);
        pm.setNickname(nickname != null && !nickname.isBlank() ? nickname.trim() : "UPI - " + cleanedUpi);
        pm.setDefault(isDefault);
        pm.setCreatedAt(LocalDateTime.now());

        return paymentMethodRepository.save(pm);
    }

    public void deletePaymentMethod(Long id, Long userId) {
        Optional<PaymentMethod> opt = paymentMethodRepository.findByIdAndUserId(id, userId);
        if (opt.isPresent()) {
            boolean wasDefault = opt.get().isDefault();
            paymentMethodRepository.delete(opt.get());

            // If the deleted one was default, set another one as default if available
            if (wasDefault) {
                List<PaymentMethod> remaining = paymentMethodRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
                if (!remaining.isEmpty()) {
                    PaymentMethod nextDefault = remaining.get(0);
                    nextDefault.setDefault(true);
                    paymentMethodRepository.save(nextDefault);
                }
            }
        }
    }

    public void setDefaultPaymentMethod(Long id, Long userId) {
        PaymentMethod target = paymentMethodRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found"));

        clearDefaultForUser(userId);
        target.setDefault(true);
        paymentMethodRepository.save(target);
    }

    private void clearDefaultForUser(Long userId) {
        List<PaymentMethod> list = paymentMethodRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
        for (PaymentMethod pm : list) {
            if (pm.isDefault()) {
                pm.setDefault(false);
                paymentMethodRepository.save(pm);
            }
        }
    }

    private String detectCardBrand(String number) {
        if (number.startsWith("4")) {
            return "Visa";
        } else if (number.matches("^(5[1-5]|222[1-9]|22[3-9]|2[3-6]|27[01]|2720).*")) {
            return "Mastercard";
        } else if (number.matches("^(60|65|35).*") || number.startsWith("508")) {
            return "RuPay";
        } else if (number.startsWith("34") || number.startsWith("37")) {
            return "American Express";
        }
        return "RuPay";
    }
}
