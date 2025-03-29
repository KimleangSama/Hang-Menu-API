package com.keakimleang.digital_menu.features.menus.services;

import com.keakimleang.digital_menu.exceptions.*;
import com.keakimleang.digital_menu.features.menus.entities.*;
import com.keakimleang.digital_menu.features.menus.payloads.requests.*;
import com.keakimleang.digital_menu.features.menus.payloads.responses.*;
import com.keakimleang.digital_menu.features.menus.repos.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import java.util.*;
import lombok.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl {
    private final MenuRepository menuRepository;
    private final MenuFavoriteRepository favoriteRepository;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "favorites", key = "#user.id"),
            @CacheEvict(value = "menus", key = "#request.categoryId")
    })
    public FavoriteResponse createFavorite(User user, CreateFavoriteRequest request) {
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu", request.getMenuId().toString()));
        Favorite favorite = CreateFavoriteRequest.fromRequest(menu);
        favorite.setUser(user);
        favorite = favoriteRepository.save(favorite);
        return FavoriteResponse.fromEntity(favorite);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "favorites", key = "#user.id"),
            @CacheEvict(value = "menus", key = "#request.categoryId")
    })
    public FavoriteResponse deleteFavorite(User user, CreateFavoriteRequest request) {
        Favorite favorite = favoriteRepository.findByUserIdAndMenuId(user.getId(), request.getMenuId())
                .orElseThrow(() -> new ResourceNotFoundException("Favorite", request.getMenuId().toString()));
        favoriteRepository.delete(favorite);
        return FavoriteResponse.fromEntity(favorite);
    }

    @Transactional
    @Cacheable(value = "favorites", key = "#user.id")
    public List<FavoriteResponse> listMenuFavorites(User user) {
        List<Favorite> favorites = favoriteRepository.findAllByUserId(user.getId());
        return FavoriteResponse.fromEntities(favorites);
    }

    @Transactional
    @Cacheable(value = "favorite", key = "#user.id + '-' + #id")
    public FavoriteResponse getFavoritesByMenuId(User user, UUID id) {
        Favorite favorite = favoriteRepository.findByUserIdAndMenuId(user.getId(), id)
                .orElse(null);
        return favorite != null ? FavoriteResponse.fromEntity(favorite) : null;
    }
}
