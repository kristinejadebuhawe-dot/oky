package com.ordering.jan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.ordering.jan.entity.User;
import com.ordering.jan.repository.UserRepository;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    // ADD THIS SECTION HERE
    @GetMapping("/menu")
    public String menu() {
        return "menu"; // This matches your menu.html file name
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        userRepository.save(user);
        return "redirect:/login?success";
    }
}