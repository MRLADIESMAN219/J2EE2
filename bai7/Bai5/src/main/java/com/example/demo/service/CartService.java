package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.CartItem;
import com.example.demo.model.Product;

import jakarta.servlet.http.HttpSession;

@Service
public class CartService {
    public static final String CART_SESSION_KEY = "cart";

    @SuppressWarnings("unchecked")
    public List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    public void addToCart(Product product, int quantity, HttpSession session) {
        List<CartItem> cart = getCart(session);
        for (CartItem item : cart) {
            if (item.getProductId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        cart.add(new CartItem(product.getId(), product.getName(), product.getImage(), product.getPrice(), quantity));
    }

    public void updateQuantity(Long productId, int quantity, HttpSession session) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getProductId().equals(productId) && quantity <= 0);
        for (CartItem item : cart) {
            if (item.getProductId().equals(productId) && quantity > 0) {
                item.setQuantity(quantity);
                break;
            }
        }
    }

    public void removeItem(Long productId, HttpSession session) {
        getCart(session).removeIf(item -> item.getProductId().equals(productId));
    }

    public long getTotalAmount(HttpSession session) {
        return getCart(session).stream().mapToLong(CartItem::getSubtotal).sum();
    }

    public int getTotalItems(HttpSession session) {
        return getCart(session).stream().mapToInt(CartItem::getQuantity).sum();
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }
}
