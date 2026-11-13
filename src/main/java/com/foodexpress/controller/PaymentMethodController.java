package com.foodexpress.controller;

import com.foodexpress.entity.PaymentMethod;
import com.foodexpress.entity.User;
import com.foodexpress.service.PaymentMethodService;
import com.foodexpress.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;
    private final UserService userService;

    @Autowired
    public PaymentMethodController(PaymentMethodService paymentMethodService, UserService userService) {
        this.paymentMethodService = paymentMethodService;
        this.userService = userService;
    }

    @GetMapping
    public String showPaymentMethods(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        List<PaymentMethod> paymentMethods = paymentMethodService.getPaymentMethodsForUser(user.getId());
        model.addAttribute("paymentMethods", paymentMethods);
        model.addAttribute("user", user);

        return "payment-methods";
    }

    @PostMapping("/card/add")
    public String addCard(@RequestParam("cardHolderName") String cardHolderName,
                          @RequestParam("cardNumber") String cardNumber,
                          @RequestParam("cardExpiry") String cardExpiry,
                          @RequestParam(value = "nickname", required = false) String nickname,
                          @RequestParam(value = "isDefault", defaultValue = "false") boolean isDefault,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        try {
            paymentMethodService.addCard(user.getId(), cardHolderName, cardNumber, cardExpiry, nickname, isDefault);
            redirectAttributes.addFlashAttribute("successMessage", "New card added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add card: " + e.getMessage());
        }

        return "redirect:/payment-methods";
    }

    @PostMapping("/upi/add")
    public String addUpi(@RequestParam("upiId") String upiId,
                         @RequestParam(value = "nickname", required = false) String nickname,
                         @RequestParam(value = "isDefault", defaultValue = "false") boolean isDefault,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        try {
            paymentMethodService.addUpi(user.getId(), upiId, nickname, isDefault);
            redirectAttributes.addFlashAttribute("successMessage", "UPI ID saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add UPI: " + e.getMessage());
        }

        return "redirect:/payment-methods";
    }

    @PostMapping("/{id}/delete")
    public String deletePaymentMethod(@PathVariable("id") Long id,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        try {
            paymentMethodService.deletePaymentMethod(id, user.getId());
            redirectAttributes.addFlashAttribute("infoMessage", "Payment method removed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not remove payment method: " + e.getMessage());
        }

        return "redirect:/payment-methods";
    }

    @PostMapping("/{id}/default")
    public String setDefaultPaymentMethod(@PathVariable("id") Long id,
                                         Authentication authentication,
                                         RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        try {
            paymentMethodService.setDefaultPaymentMethod(id, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Default payment method updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not set default: " + e.getMessage());
        }

        return "redirect:/payment-methods";
    }
}
