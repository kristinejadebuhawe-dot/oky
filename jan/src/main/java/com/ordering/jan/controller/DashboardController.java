package com.ordering.jan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ordering.jan.repository.ItemRepository;
import com.ordering.jan.repository.LaborRepository;
import com.ordering.jan.repository.OrderRepository;

@Controller
public class DashboardController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private LaborRepository laborRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Calculate KPIs
        double totalSales = orderRepository.findAll().stream().mapToDouble(o -> o.getTotalAmount()).sum();
        long totalOrders = orderRepository.count();
        long lowStockCount = itemRepository.findAll().stream().filter(i -> i.getStock() < 10).count();
        double totalLaborCost = laborRepository.findAll().stream().mapToDouble(l -> l.getDailyWage()).sum();

        model.addAttribute("totalSales", totalSales);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("lowStock", lowStockCount);
        model.addAttribute("laborCost", totalLaborCost);
        
        // Pass recent orders for the table
        model.addAttribute("recentOrders", orderRepository.findAll()); 

        return "dashboard";
    }
}