package com.keakimleang.digital_menu.features.menus.services;

import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.exceptions.*;
import com.keakimleang.digital_menu.features.files.services.*;
import com.keakimleang.digital_menu.features.menus.entities.*;
import com.keakimleang.digital_menu.features.menus.payloads.requests.*;
import com.keakimleang.digital_menu.features.menus.payloads.responses.*;
import com.keakimleang.digital_menu.features.menus.repos.*;
import com.keakimleang.digital_menu.features.sysparams.entities.*;
import com.keakimleang.digital_menu.features.sysparams.services.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.utils.*;
import java.util.*;
import java.util.concurrent.*;
import lombok.*;
import org.slf4j.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

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
    private final SysParamServiceImpl sysParamServiceImpl;

    @Transactional
    public long count() {
        return menuRepository.count();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.MENUS, key = "#request.storeId"),
            @CacheEvict(value = CacheValue.MENU_ENTITIES, key = "#request.storeId")
    })
    public MenuResponse create(User user, CreateMenuRequest request) {
        CompletableFuture<Optional<Category>> categoryFuture =
                categoryRepository.asyncFindById(request.getStoreId(), request.getCategoryId());
        CompletableFuture<Integer> countFuture = categoryFuture.thenCompose(optionalCategory -> {
            Category category = optionalCategory.orElseThrow(() ->
                    new ResourceNotFoundException("Category", request.getCategoryId().toString()));
            if (!ResourceOwner.hasPermission(user, category)) {
                throw new ResourceForbiddenException(user.getUsername(), Category.class);
            }
            return menuRepository.asyncCountByCategory(category);
        });
        return categoryFuture.thenCombine(countFuture, (optionalCategory, count) -> {
            Category category = optionalCategory.orElseThrow(() ->
                    new ResourceNotFoundException("Category", request.getCategoryId().toString()));
            SysParam sysParam = sysParamServiceImpl.findSysParamByStoreId(request.getStoreId());
            Integer maxMenus = (sysParam != null) ? sysParam.getMaxMenuNumber() : SysParamValue.MAX_MENU;
            if (maxMenus != null && count >= maxMenus) {
                throw new ResourceExceedLimitException("Menu", "category", maxMenus);
            }
            // Process menu creation
            Group group = category.getGroup();
            Menu menu = CreateMenuRequest.fromRequest(request);
            menu.setGroup(group);
            menu.setCategory(category);
            menu.setCreatedBy(user.getId());
            Menu savedMenu = menuRepository.save(menu);
            saveMenuImages(savedMenu, request.getImages());
            return MenuResponse.fromEntity(savedMenu);
        }).join();
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
    public MenuResponse updateMenuById(User user, UUID id, UpdateMenuRequest request) {
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
    public List<MenuResponse> findAllMenusByStoreId(User user, UUID storeId) {
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
    public MenuResponse toggleMenuAvailability(User user, MenuToggleRequest request) {
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElse(null);
        if (menu == null) {
            return null;
        }
        if (ResourceOwner.hasPermission(user, menu)) {
            menu.setIsAvailable(!menu.getIsAvailable());
            menuRepository.save(menu);
        }
        return MenuResponse.fromEntity(menu);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.MENU, key = "#request.menuId"),
            @CacheEvict(value = CacheValue.MENUS, key = "#request.storeId"),
    })
    public MenuResponse deleteMenuById(User user, MenuToggleRequest request) {
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
    public Optional<Menu> findMenuEntityById(UUID menuId) {
        return menuRepository.findById(menuId);
    }

    @Transactional
    @Cacheable(value = CacheValue.MENU, key = "#id")
    public MenuResponse findMenuById(User user, UUID id) {
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
    public MenuResponse updateCategoryOfMenuById(User user, UUID id, UpdateMenuCategoryRequest request) {
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
