package com.demo.ecommerce_backend.cart;

import com.demo.ecommerce_backend.User.User;
import com.demo.ecommerce_backend.User.UserReposirtory;
import com.demo.ecommerce_backend.exception.ResourceNotFoundException;
import com.demo.ecommerce_backend.product.Product;
import com.demo.ecommerce_backend.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserReposirtory userReposirtory;

    // 1. Add or update quantity
    public CartResponse addToCart(CartRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        User user = userReposirtory.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(request.getUserId(), request.getProductId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + request.getQuantity());
                    return existing;
                })
                .orElse(CartItem.builder()
                        .user(user)
                        .product(product)
                        .quantity(request.getQuantity())
                        .build());

        return mapToResponse(cartItemRepository.save(cartItem));
    }

    // 2. Get all cart items
    public List<CartResponse> getCartItems(Integer userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 3. Remove single cart item
    public void removeFromCart(Integer userId, Integer productId) {
        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cartItemRepository.delete(item);
    }

    // 4. Clear all cart items for a user
    @Transactional
    public void clearCart(Integer userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    // 5. Optional: update quantity directly
    public CartResponse updateQuantity(Integer userId, Integer productId, int newQty) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cartItem.setQuantity(newQty);
        return mapToResponse(cartItemRepository.save(cartItem));
    }

    private CartResponse mapToResponse(CartItem item) {
        return CartResponse.builder()
                .id(item.getId())
//                .userId(item.getUser().getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .price(item.getProduct().getPrice())
                .discountedPrice(item.getProduct().getDiscountedPrice())
                .quantity(item.getQuantity())
                .build();
    }
}
