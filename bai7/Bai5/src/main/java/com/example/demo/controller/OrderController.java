package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.repository.OrderRepository;
import com.example.demo.service.CartService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @GetMapping("/success/{id}")
    public String success(@PathVariable Long id, Model model, HttpSession session) {
        model.addAttribute("order", orderRepository.findById(id).orElse(null));
        model.addAttribute("cartCount", cartService.getTotalItems(session));
        return "order/success";
    }
}
