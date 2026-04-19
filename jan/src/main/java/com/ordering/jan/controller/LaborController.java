package com.ordering.jan.controller;

import com.ordering.jan.entity.Labor;
import com.ordering.jan.entity.User;
import com.ordering.jan.repository.LaborRepository;
import com.ordering.jan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/labor")
@RequiredArgsConstructor
public class LaborController {

    private final LaborRepository laborRepository;
    private final UserService userService; // Ensure your UserService has getAllUsers() and findById()

    @GetMapping
    public String listLabor(Model model) {
        List<Labor> allLabor = laborRepository.findAll();
        model.addAttribute("labors", allLabor);
        
        // Find users who don't have a labor profile yet
        List<Long> linkedUserIds = allLabor.stream()
                .filter(l -> l.getSystemAccount() != null)
                .map(l -> l.getSystemAccount().getId())
                .collect(Collectors.toList());

        List<User> availableUsers = userService.getAllUsers().stream()
                .filter(u -> !linkedUserIds.contains(u.getId()))
                .collect(Collectors.toList());

        model.addAttribute("availableUsers", availableUsers);
        return "labor-management";
    }

    @PostMapping("/activate")
    public String activateWorker(@RequestParam Long userId, 
                                 @RequestParam String position, 
                                 @RequestParam Double dailyWage, 
                                 RedirectAttributes ra) {
        User user = userService.findById(userId);
        if (user != null) {
            Labor labor = new Labor();
            labor.setSystemAccount(user);
            labor.setName(user.getUsername()); // Default labor name to username
            labor.setPosition(position);
            labor.setDailyWage(dailyWage);
            laborRepository.save(labor);
            ra.addFlashAttribute("message", "Worker profile activated for " + user.getUsername());
        }
        return "redirect:/labor";
    }

    @PostMapping("/update")
    public String updateLabor(@ModelAttribute Labor labor, RedirectAttributes ra) {
        // Find existing to preserve the User link if not sent in the form
        Labor existing = laborRepository.findById(labor.getId()).orElse(null);
        if (existing != null) {
            existing.setPosition(labor.getPosition());
            existing.setDailyWage(labor.getDailyWage());
            existing.setActive(labor.isActive());
            laborRepository.save(existing);
        }
        ra.addFlashAttribute("message", "Metrics updated successfully.");
        return "redirect:/labor";
    }
}