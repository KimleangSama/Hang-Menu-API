package io.sovann.hang.api.features.carts.services;

import io.sovann.hang.api.configs.CartRabbitMQConfig;
import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.carts.entities.Cart;
import io.sovann.hang.api.features.carts.entities.CartMenu;
import io.sovann.hang.api.features.carts.payloads.requests.CartMenuMutateRequest;
import io.sovann.hang.api.features.carts.payloads.requests.CreateCartMenuRequest;
import io.sovann.hang.api.features.carts.payloads.responses.CartMenuResponse;
import io.sovann.hang.api.features.carts.repos.CartMenuRepository;
import io.sovann.hang.api.features.carts.repos.CartRepository;
import io.sovann.hang.api.features.menus.entities.Menu;
import io.sovann.hang.api.features.menus.services.MenuServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartMenuServiceImpl {
    private final MenuServiceImpl menuService;
    private final CartServiceImpl cartService;
    private final CartMenuRepository CartMenuRepository;
    private final RabbitTemplate rabbitTemplate;
    private final CartRepository cartRepository;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "cart-menus", key = "#request.cartId"),
            @CacheEvict(value = "cart-menu", key = "#request.cartId")
    })
    public CartMenuResponse createCart(CreateCartMenuRequest request) {
        try {
            rabbitTemplate.convertAndSend(
                    CartRabbitMQConfig.QUEUE_NAME, request);
            CartMenuResponse response = new CartMenuResponse();
            response.setCartId(request.getCartId());
            response.setMenuId(request.getMenuId());
            response.setQuantity(request.getQuantity());
            response.setSpecialRequests(request.getSpecialRequests());
            return response;
        } catch (Exception e) {
            log.error("Error creating cart menu", e);
            return null;
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void createCartMenu(CreateCartMenuRequest request) {
        Cart cart = cartService.getCartEntityById(request.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", request.getCartId().toString()));
        Menu menu = menuService.getMenuById(request.getMenuId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu", request.getMenuId().toString()));
        CartMenu cartMenu = new CartMenu();
        cartMenu.setCart(cart);
        cartMenu.setMenu(menu);
        cartMenu.setQuantity(request.getQuantity());
        cartMenu.setSpecialRequests(request.getSpecialRequests());
        CartMenuRepository.save(cartMenu);
    }

    @Cacheable(value = "cart-menus", key = "#cartId")
    public List<CartMenuResponse> getCartMenuOfCart(UUID cartId) {
        List<CartMenu> cartMenus = CartMenuRepository.findAllByCartId(cartId);
        return CartMenuResponse.fromEntities(cartMenus);
    }

    @Caching(evict = {
            @CacheEvict(value = "cart-menus", key = "#request.cartId"),
            @CacheEvict(value = "cart-menu", key = "#request.cartId")
    })
    public CartMenuResponse deleteCartMenu(CartMenuMutateRequest request) {
        CartMenu cartMenu = CartMenuRepository.findByIdAndCartId(request.getCartMenuId(), request.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("CartMenu and Cart", request.getCartMenuId().toString()
                        + " and " + request.getCartId().toString()));
        CartMenuRepository.delete(cartMenu);
        return CartMenuResponse.fromEntity(cartMenu);
    }

    @Cacheable(value = "cart-menu", key = "#request.cartId")
    public CartMenuResponse getCartMenuById(CartMenuMutateRequest request) {
        CartMenu cartMenu = CartMenuRepository.findByIdAndCartId(request.getCartMenuId(), request.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("CartMenu and Cart", request.getCartMenuId().toString()
                        + " and " + request.getCartId().toString()));
        return CartMenuResponse.fromEntity(cartMenu);
    }

    @Caching(evict = {
            @CacheEvict(value = "cart-menus", key = "#request.cartId"),
            @CacheEvict(value = "cart-menu", key = "#request.cartId")
    })
    public CartMenuResponse updateCartMenu(CartMenuMutateRequest request) {
        CartMenu cartMenu = CartMenuRepository.findByIdAndCartId(request.getCartMenuId(), request.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("CartMenu and Cart", request.getCartMenuId().toString()
                        + " and " + request.getCartId().toString()));
        cartMenu.setQuantity(request.getQuantity());
        cartMenu.setSpecialRequests(request.getSpecialRequests());
        CartMenuRepository.save(cartMenu);
        return CartMenuResponse.fromEntity(cartMenu);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "cart-menus", key = "#cartId"),
            @CacheEvict(value = "cart-menu", key = "#cartId")
    })
    public void deleteCartMenuOfCart(UUID cartId) {
        CartMenuRepository.deleteAllByCartId(cartId);
    }
}
