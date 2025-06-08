package com.demo.ecommerce_backend.cart;

import com.demo.ecommerce_backend.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(@Valid @RequestBody CartRequest request) {
        CartResponse response = cartService.addToCart(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product added to cart", response));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<CartResponse>>> getCart(@PathVariable Integer userId) {
        List<CartResponse> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart fetched successfully", cartItems));
    }

    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(@PathVariable Integer userId, @PathVariable Integer productId) {
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product removed from cart", null));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Integer userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart cleared", null));
    }

    // Optional: Update quantity API
    @PutMapping("/{userId}/{productId}/update")
    public ResponseEntity<ApiResponse<CartResponse>> updateQuantity(
            @PathVariable Integer userId,
            @PathVariable Integer productId,
            @RequestParam int quantity
    ) {
        CartResponse response = cartService.updateQuantity(userId, productId, quantity);
        return ResponseEntity.ok(new ApiResponse<>(true, "Quantity updated", response));
    }
}
