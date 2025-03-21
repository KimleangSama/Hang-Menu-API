package io.sovann.hang.api.features.menus.services;

import io.sovann.hang.api.constants.CacheValue;
import io.sovann.hang.api.exceptions.ResourceForbiddenException;
import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.files.services.FileStorageServiceImpl;
import io.sovann.hang.api.features.menus.entities.Category;
import io.sovann.hang.api.features.menus.entities.Menu;
import io.sovann.hang.api.features.menus.entities.MenuImage;
import io.sovann.hang.api.features.menus.payloads.requests.*;
import io.sovann.hang.api.features.menus.payloads.responses.FavoriteResponse;
import io.sovann.hang.api.features.menus.payloads.responses.MenuResponse;
import io.sovann.hang.api.features.menus.repos.CategoryRepository;
import io.sovann.hang.api.features.menus.repos.MenuImageRepository;
import io.sovann.hang.api.features.menus.repos.MenuRepository;
import io.sovann.hang.api.features.users.entities.Group;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.services.GroupServiceImpl;
import io.sovann.hang.api.utils.ResourceOwner;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(MenuServiceImpl.class);
    private final MenuRepository menuRepository;
    private final MenuImageRepository menuImageRepository;
    private final FavoriteServiceImpl favoriteService;
    private final CategoryRepository categoryRepository;
    private final CategoryServiceImpl categoryServiceImpl;

    private final FileStorageServiceImpl fileStorageServiceImpl;

    @Transactional
    public long count() {
        return menuRepository.count();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.MENUS, key = "#request.storeId", allEntries = true),
    })
    public MenuResponse create(User user, CreateMenuRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId().toString()));
        if (ResourceOwner.hasPermission(user, category)) {
            Group group = category.getGroup();
            Menu menu = CreateMenuRequest.fromRequest(request);
            menu.setGroup(group);
            menu.setCategory(category);
            menu.setCreatedBy(user.getId());
            Menu savedMenu = menuRepository.save(menu);
            saveMenuImages(savedMenu, request.getImages());
            return MenuResponse.fromEntity(savedMenu);
        }
        throw new ResourceForbiddenException(user.getUsername(), Category.class);
    }

    private void saveMenuImages(Menu menu, List<String> imageRequests) {
        List<MenuImage> images = imageRequests.stream()
                .map(CreateMenuImageRequest::fromRequest)
                .peek(image -> image.setMenu(menu))
                .toList();
        menuImageRepository.saveAll(images);
        menu.setImages(images);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.MENU, key = "#id"),
            @CacheEvict(value = CacheValue.MENUS, key = "#request.storeId")
    })
    public MenuResponse update(User user, UUID id, UpdateMenuRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu", id.toString()));
        if (ResourceOwner.hasPermission(user, menu)) {
            if (!menu.getCategory().getId().equals(request.getCategoryId())) {
                Category category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId().toString()));
                menu.setCategory(category);
            }
            List<String> existingImages = menu.getImages().stream().map(MenuImage::getName).toList();
            fileStorageServiceImpl.deleteAllInExclude(existingImages, request.getImages());
            menu.setCode(request.getCode());
            menu.setName(request.getName());
            menu.setDescription(request.getDescription());
            menu.setPrice(request.getPrice());
            menu.setDiscount(request.getDiscount());
            menu.setCurrency(request.getCurrency());
            menu.setImage(request.getImage());
            menu.setBadges(request.getBadges());
            menu.setUpdatedBy(user.getId());
            Menu savedMenu = menuRepository.save(menu);
            updateMenuImages(savedMenu, request.getImages());
            return MenuResponse.fromEntity(savedMenu);
        }
        throw new ResourceForbiddenException(user.getUsername(), Menu.class);
    }

    private void updateMenuImages(Menu menu, List<String> imageRequests) {
        if (imageRequests.isEmpty()) {
            menuImageRepository.deleteAllByMenu(menu);
            return;
        }
        List<MenuImage> images = imageRequests.stream()
                .map(CreateMenuImageRequest::fromRequest)
                .peek(image -> image.setMenu(menu))
                .toList();
        menuImageRepository.deleteAllByMenu(menu);
        menuImageRepository.saveAll(images);
        menu.setImages(images);
    }

    @Transactional
    @Cacheable(value = CacheValue.MENUS, key = "#storeId")
    public List<MenuResponse> list(User user, UUID storeId) {
        List<Category> categories = categoryServiceImpl.findAllByStoreId(storeId);
        List<Menu> menus = menuRepository.findAllByCategoryInOrderByCreatedAtDesc(categories);
        if (user == null) {
            return MenuResponse.fromEntities(menus, Collections.emptyList());
        }
        List<FavoriteResponse> favorites = favoriteService.listMenuFavorites(user);
        return MenuResponse.fromEntities(menus, favorites);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.MENU, key = "#request.menuId"),
            @CacheEvict(value = CacheValue.MENUS, key = "#request.storeId"),
    })
    public MenuResponse toggleMenuVisibility(User user, MenuToggleRequest request) {
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElse(null);
        if (menu == null) {
            return null;
        }
        if (ResourceOwner.hasPermission(user, menu)) {
            menu.setIsHidden(!menu.getIsHidden());
            menuRepository.save(menu);
        }
        return MenuResponse.fromEntity(menu);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.MENU, key = "#request.menuId"),
            @CacheEvict(value = CacheValue.MENUS, key = "#request.storeId"),
    })
    public MenuResponse deleteMenu(User user, MenuToggleRequest request) {
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu", request.getMenuId().toString()));
        if (ResourceOwner.hasPermission(user, menu)) {
            menuImageRepository.deleteAllByMenu(menu);
            menuRepository.delete(menu);
            return MenuResponse.fromEntity(menu);
        } else {
            return null;
        }
    }

    @Transactional
    @Cacheable(value = CacheValue.MENU_ENTITY, key = "#menuId")
    public Optional<Menu> getMenuEntityById(UUID menuId) {
        return menuRepository.findById(menuId);
    }

    @Transactional
    @Cacheable(value = CacheValue.MENU, key = "#id")
    public MenuResponse getMenuResponseById(User user, UUID id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu", id.toString()));
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

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.MENU, key = "#id"),
            @CacheEvict(value = CacheValue.MENUS, allEntries = true),
    })
    public void updateMenuImage(UUID id, String image) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu", id.toString()));
        menu.setImage(image);
        menuRepository.save(menu);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.MENUS, key = "#request.storeId"),
            @CacheEvict(value = CacheValue.CATEGORIES, key = "#request.storeId"),
    })
    public MenuResponse updateMenuCategory(User user, UUID id, UpdateMenuCategoryRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId().toString()));
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu", id.toString()));
        if (ResourceOwner.hasPermission(user, menu)) {
            menu.setCategory(category);
            menuRepository.save(menu);
            return MenuResponse.fromEntity(menu);
        } else {
            return null;
        }
    }
}
