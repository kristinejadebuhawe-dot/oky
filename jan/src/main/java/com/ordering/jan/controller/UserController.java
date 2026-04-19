package com.ordering.jan.controller;

import com.ordering.jan.entity.User;
import com.ordering.jan.entity.Labor;
import com.ordering.jan.repository.UserRepository;
import com.ordering.jan.repository.LaborRepository; // ADDED
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // ADDED
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final LaborRepository laborRepository; // ADDED local variable
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("newUser", new User());
        return "user-management";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute User user, RedirectAttributes ra) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        ra.addFlashAttribute("message", "Identity Provisioned Successfully.");
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    @Transactional // CRITICAL: This ensures both deletions happen or none at all
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        try {
            // 1. Check for and destroy the tethered Labor profile first
            // This prevents the "Foreign Key Constraint" 500 Error
            Labor linkedLabor = laborRepository.findBySystemAccountId(id);
            if (linkedLabor != null) {
                laborRepository.delete(linkedLabor);
            }

            // 2. Safe to revoke the System Identity now
            userRepository.deleteById(id);
            
            ra.addFlashAttribute("message", "Identity and associated metrics revoked.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "System Error: Could not break identity tether.");
        }
        return "redirect:/users";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user, @RequestParam(required = false) String password, RedirectAttributes ra) {
        User existing = userRepository.findById(user.getId()).orElse(null);
        if (existing != null) {
            existing.setEmail(user.getEmail());
            existing.setRole(user.getRole());
            if (password != null && !password.isEmpty()) {
                existing.setPassword(passwordEncoder.encode(password));
            }
            userRepository.save(existing);
            ra.addFlashAttribute("message", "Credentials updated.");
        }
        return "redirect:/users";
    }
}