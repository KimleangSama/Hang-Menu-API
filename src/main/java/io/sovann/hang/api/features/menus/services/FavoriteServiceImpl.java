package io.sovann.hang.api.features.menus.services;

import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.menus.entities.Favorite;
import io.sovann.hang.api.features.menus.entities.Menu;
import io.sovann.hang.api.features.menus.payloads.requests.CreateFavoriteRequest;
import io.sovann.hang.api.features.menus.payloads.responses.FavoriteResponse;
import io.sovann.hang.api.features.menus.repos.MenuFavoriteRepository;
import io.sovann.hang.api.features.menus.repos.MenuRepository;
import io.sovann.hang.api.features.users.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
