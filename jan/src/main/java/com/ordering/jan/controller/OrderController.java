package com.ordering.jan.controller;

import com.ordering.jan.entity.Item;
import com.ordering.jan.entity.OrderItem;
import com.ordering.jan.entity.Orders;
import com.ordering.jan.repository.ItemRepository;
import com.ordering.jan.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Displays the Order Management / POS page.
     * This fixes the White Label Error when navigating to /orders.
     */
    /**
     * Displays the Order Management / POS page.
     * Updated to filter out cancelled/inactive products.
     */
    @GetMapping("/orders")
    public String showOrderPage(Model model) {
        // FIX: Only fetch items that are ACTIVE
        List<Item> items = itemRepository.findByActiveTrue();
        List<Orders> orders = orderRepository.findAll();

        // DEBUG: Check your console/terminal for this message!
        System.out.println("DEBUG: Found " + items.size() + " active items for the POS.");

        model.addAttribute("items", items);
        model.addAttribute("orders", orders);
        return "order-management";
    }
    
    @PostMapping("/orders/add")
    public String addOrder(@RequestParam String customerName,
                           @RequestParam(value = "itemIds", required = false) List<Long> itemIds,
                           @RequestParam(value = "quantities", required = false) List<Integer> quantities,
                           RedirectAttributes redirectAttributes) {
        
        // Validation: Ensure the cart isn't empty
        if (itemIds == null || itemIds.isEmpty() || quantities == null) {
            redirectAttributes.addFlashAttribute("error", "Your cart is empty!");
            return "redirect:/orders";
        }

        // 1. Create the Parent Order
        Orders order = new Orders();
        order.setCustomerName(customerName);
        order.setStatus("PENDING");
        
        double grandTotal = 0;

        // 2. Process each item in the order
        for (int i = 0; i < itemIds.size(); i++) {
            Long itemId = itemIds.get(i);
            Integer qty = quantities.get(i);

            // Skip items with 0 or negative quantity
            if (qty == null || qty <= 0) continue;

            Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

            // Check Stock Availability
            if (item.getStock() < qty) {
                redirectAttributes.addFlashAttribute("error", "Not enough stock for: " + item.getName());
                return "redirect:/orders";
            }

            // 3. Create the OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setQuantity(qty);
            
            // Link to parent (Uses the helper method in your Orders entity)
            order.addOrderItem(orderItem);

            // Update running total
            grandTotal += item.getPrice() * qty;

            // Deduct from inventory
            item.setStock(item.getStock() - qty);
            itemRepository.save(item);
        }

        order.setTotalAmount(grandTotal);
        
        // 4. Save the Parent (Cascades to OrderItems)
        orderRepository.save(order);

        redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
        return "redirect:/orders";
    }
    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Find the order first
            Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

            // IMPORTANT: If you want to return items to stock when canceling:
            for (OrderItem detail : order.getOrderItems()) {
                Item item = detail.getItem();
                item.setStock(item.getStock() + detail.getQuantity());
                itemRepository.save(item);
            }

            // Delete the order (CascadeType.ALL will handle the OrderItems)
            orderRepository.delete(order);
            
            redirectAttributes.addFlashAttribute("success", "Order cancelled and stock returned!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error cancelling order: " + e.getMessage());
        }
        
        return "redirect:/orders";
    }
}