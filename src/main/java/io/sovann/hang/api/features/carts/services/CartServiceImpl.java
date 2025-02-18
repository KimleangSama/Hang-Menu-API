package io.sovann.hang.api.features.carts.services;

import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.carts.entities.Cart;
import io.sovann.hang.api.features.carts.payloads.requests.CreateCartRequest;
import io.sovann.hang.api.features.carts.payloads.responses.CartResponse;
import io.sovann.hang.api.features.carts.repos.CartRepository;
import io.sovann.hang.api.features.users.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl {
    private final CartRepository cartRepository;

    @Transactional
    @CacheEvict(value = "carts", key = "#user.id")
    public CartResponse createCart(User user, CreateCartRequest request) {
        Cart cart = new Cart();
        if (user != null) {
            cart.setCreatedBy(user.getId());
        }
        cartRepository.save(cart);
        return CartResponse.fromEntity(cart);
    }

    @Transactional
    @Cacheable(value = "carts", key = "#user.id")
    public CartResponse getCart(User user) {
        Cart cart = cartRepository.findByCreatedBy(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", user.getId().toString()));
        return CartResponse.fromEntity(cart);
    }

    @Transactional
    @Cacheable(value = "carts", key = "#cartId")
    public CartResponse getCartById(UUID cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", cartId.toString()));
        return CartResponse.fromEntity(cart);
    }

    @Transactional
    @Cacheable(value = "entities", key = "#cartId")
    public Optional<Cart> getCartEntityById(UUID cartId) {
        return cartRepository.findById(cartId);
    }
}
