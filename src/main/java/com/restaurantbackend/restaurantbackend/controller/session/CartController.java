package com.restaurantbackend.restaurantbackend.controller.session;

import com.restaurantbackend.restaurantbackend.service.session.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class CartController {

    private final CartService cartService;

    @GetMapping("/{sessionId}")
    public ResponseEntity<List<Map<String, Object>>> getCart(@PathVariable Long sessionId) {
        try {
            List<Map<String, Object>> cart = cartService.getCart(sessionId);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{sessionId}/add")
    public ResponseEntity<?> addToCart(
            @PathVariable Long sessionId,
            @RequestBody Map<String, Object> cartItem) {
        try {
            List<Map<String, Object>> cart = cartService.addToCart(sessionId, cartItem);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            System.err.println("CartController.addToCart hatası: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{sessionId}/remove")
    public ResponseEntity<List<Map<String, Object>>> removeFromCart(
            @PathVariable Long sessionId,
            @RequestBody Map<String, Object> cartItem) {
        try {
            List<Map<String, Object>> cart = cartService.removeFromCart(sessionId, cartItem);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{sessionId}/update")
    public ResponseEntity<List<Map<String, Object>>> updateCart(
            @PathVariable Long sessionId,
            @RequestBody Map<String, Object> cartItem) {
        try {
            List<Map<String, Object>> cart = cartService.updateCart(sessionId, cartItem);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long sessionId) {
        try {
            cartService.clearCart(sessionId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // CORS preflight için
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }
}
