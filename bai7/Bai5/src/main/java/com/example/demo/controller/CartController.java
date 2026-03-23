package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm để thêm vào giỏ hàng.");
            return "redirect:/products";
        }

        int validQuantity = Math.max(quantity, 1);
        cartService.addToCart(product, validQuantity, session);
        redirectAttributes.addFlashAttribute("message", "Đã thêm sản phẩm vào giỏ hàng.");
        return "redirect:/products";
    }

    @GetMapping
    public String showCart(Model model, HttpSession session) {
        List<CartItem> cartItems = cartService.getCart(session);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", cartService.getTotalAmount(session));
        model.addAttribute("cartCount", cartService.getTotalItems(session));
        return "cart/index";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam Long productId,
            @RequestParam int quantity,
            HttpSession session) {
        cartService.updateQuantity(productId, quantity, session);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{productId}")
    public String removeItem(@PathVariable Long productId, HttpSession session) {
        cartService.removeItem(productId, session);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, RedirectAttributes redirectAttributes) {
        List<CartItem> cartItems = cartService.getCart(session);
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng đang trống.");
            return "redirect:/cart";
        }

        var order = orderService.checkout(cartItems);
        cartService.clearCart(session);
        redirectAttributes.addFlashAttribute("message", "Đặt hàng thành công. Mã đơn hàng: #" + order.getId());
        return "redirect:/orders/success/" + order.getId();
    }
}
