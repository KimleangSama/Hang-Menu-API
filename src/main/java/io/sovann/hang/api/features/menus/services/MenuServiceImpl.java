package io.sovann.hang.api.features.menus.services;

import io.sovann.hang.api.exceptions.*;
import io.sovann.hang.api.features.menus.entities.*;
import io.sovann.hang.api.features.menus.payloads.requests.*;
import io.sovann.hang.api.features.menus.payloads.responses.*;
import io.sovann.hang.api.features.menus.repos.*;
import io.sovann.hang.api.features.users.entities.*;
import java.util.*;
import lombok.*;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl {
    private final MenuRepository menuRepository;
    private final MenuImageRepository menuImageRepository;
    private final FavoriteServiceImpl favoriteService;
    private final CategoryRepository categoryRepository;
    private final CategoryServiceImpl categoryServiceImpl;

    @Transactional
    public long count() {
        return menuRepository.count();
    }

    @Transactional
    @CacheEvict(value = "menus", key = "#request.storeId")
    public MenuResponse createMenu(User user, CreateMenuRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId().toString()));
        List<MenuImage> images = menuImageRepository.findAllById(request.getImages());
        Menu menu = CreateMenuRequest.fromRequest(request);
        menu.setCategory(category);
        menu.setImages(images);
        menu.setCreatedBy(user.getId());
        menu = menuRepository.save(menu);
        return MenuResponse.fromEntity(menu);
    }

    @Transactional
    @Cacheable(value = "menus", key = "#categoryId")
    public List<MenuResponse> listMenuByCategoryId(
            User user,
            UUID categoryId
    ) {
        Category category = categoryRepository.findById(categoryId)
                .orElse(null);
        if (category == null) {
            return Collections.emptyList();
        }
        List<Menu> menus = menuRepository.findAllByCategory(category);
        if (user == null) {
            return MenuResponse.fromEntities(menus, Collections.emptyList());
        }
        List<FavoriteResponse> favorites = favoriteService.listMenuFavorites(user);
        return MenuResponse.fromEntities(menus, favorites);
    }

    @Transactional
    @Cacheable(value = "menus", key = "#storeId")
    public List<MenuResponse> listMenusWithCategory(User user, UUID storeId) {
        List<Category> categories = categoryServiceImpl.findAllByStoreId(storeId);
        List<Menu> menus = menuRepository.findAllByCategoryIn(categories);
        if (user == null) {
            return MenuResponse.fromEntities(menus, Collections.emptyList());
        }
        List<FavoriteResponse> favorites = favoriteService.listMenuFavorites(user);
        return MenuResponse.fromEntities(menus, favorites);
    }

    @Transactional
    @CacheEvict(value = "menus", key = "#request.categoryId")
    public MenuResponse toggleMenuVisibility(User user, MenuToggleRequest request) {
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElse(null);
        if (menu == null) {
            return null;
        }
        menu.setIsHidden(!menu.getIsHidden());
        menuRepository.save(menu);
        return MenuResponse.fromEntity(menu);
    }

    @Transactional
    @CacheEvict(value = "menus", key = "#request.categoryId")
    public MenuResponse toggleMenuAvailability(User user, MenuToggleRequest request) {
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElse(null);
        if (menu == null) {
            return null;
        }
        menu.setIsAvailable(!menu.getIsAvailable());
        menuRepository.save(menu);
        return MenuResponse.fromEntity(menu);
    }

    @Transactional
    @CacheEvict(value = "menus", key = "#request.categoryId")
    public MenuResponse deleteMenu(User user, MenuToggleRequest request) {
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElse(null);
        if (menu == null) {
            return null;
        }
        menuRepository.delete(menu);
        return MenuResponse.fromEntity(menu);
    }

    @Transactional
    @CacheEvict(value = "menu-entity", key = "#menuId")
    public Optional<Menu> getMenuEntityById(UUID menuId) {
        return menuRepository.findById(menuId);
    }

    @Transactional
    @Cacheable(value = "menus", key = "#id")
    public MenuResponse getMenuResponseById(User user, UUID id) {
        Menu menu = menuRepository.findById(id)
                .orElse(null);
        if (menu == null) {
            return null;
        }
        if (user == null) {
            return MenuResponse.fromEntity(menu);
        }
        MenuResponse response = MenuResponse.fromEntity(menu);
        FavoriteResponse favorite = favoriteService.getFavoritesByMenuId(user, id);
        if (favorite != null) {
            response.setFavorite(true);
        }
        return response;
    }
}
