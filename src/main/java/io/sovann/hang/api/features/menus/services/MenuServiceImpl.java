package io.sovann.hang.api.features.menus.services;

import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.menus.entities.Category;
import io.sovann.hang.api.features.menus.entities.Menu;
import io.sovann.hang.api.features.menus.entities.MenuImage;
import io.sovann.hang.api.features.menus.payloads.requests.CreateMenuRequest;
import io.sovann.hang.api.features.menus.payloads.requests.MenuToggleRequest;
import io.sovann.hang.api.features.menus.payloads.responses.FavoriteResponse;
import io.sovann.hang.api.features.menus.payloads.responses.MenuResponse;
import io.sovann.hang.api.features.menus.repos.CategoryRepository;
import io.sovann.hang.api.features.menus.repos.MenuImageRepository;
import io.sovann.hang.api.features.menus.repos.MenuRepository;
import io.sovann.hang.api.features.users.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl {
    private final MenuRepository menuRepository;
    private final MenuImageRepository menuImageRepository;
    private final FavoriteServiceImpl favoriteService;
    private final CategoryRepository categoryRepository;
    private final CategoryServiceImpl categoryServiceImpl;

    @Transactional
    @CacheEvict(value = "menus", key = "#request.categoryId", allEntries = true)
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

    // For admin only
    @Transactional
    @Cacheable(value = "menus")
    public List<MenuResponse> listMenus(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Menu> menus = menuRepository.findAll(pageable);
        return MenuResponse.fromEntities(menus.getContent(), Collections.emptyList());
    }

    @Transactional
    public long count() {
        return menuRepository.count();
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
    @Cacheable(value = "menus")
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
