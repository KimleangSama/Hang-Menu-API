package com.keakimleang.digital_menu.features.menus.services;

import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.exceptions.*;
import com.keakimleang.digital_menu.features.menus.entities.*;
import com.keakimleang.digital_menu.features.menus.payloads.requests.*;
import com.keakimleang.digital_menu.features.menus.payloads.responses.*;
import com.keakimleang.digital_menu.features.menus.repos.*;
import com.keakimleang.digital_menu.features.stores.entities.*;
import com.keakimleang.digital_menu.features.stores.services.*;
import com.keakimleang.digital_menu.features.sysparams.entities.*;
import com.keakimleang.digital_menu.features.sysparams.services.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.features.users.enums.*;
import com.keakimleang.digital_menu.utils.*;
import java.util.*;
import java.util.stream.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl {
    private final CategoryRepository categoryRepository;
    private final StoreServiceImpl storeServiceImpl;
    private final SysParamServiceImpl sysParamServiceImpl;

    public long count() {
        return categoryRepository.count();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.CATEGORIES, key = "#request.storeId"),
            @CacheEvict(value = CacheValue.CATEGORY_ENTITIES, key = "#request.storeId")
    })
    public CategoryResponse create(User user, CreateCategoryRequest request) {
        Store store = storeServiceImpl.findStoreEntityById(user, request.getStoreId());
        if (!ResourceOwner.hasPermission(user, store) || store.isExpired()) {
            throw new ResourceForbiddenException(user.getUsername(), Store.class);
        }
        SysParam sysParam = sysParamServiceImpl.findSysParamByStoreId(request.getStoreId());
        Integer maxCategories = (sysParam != null) ? sysParam.getMaxCategoryNumber() : SysParamValue.MAX_CATEGORY;
        Integer count = categoryRepository.countByStore(store);
        if (maxCategories != null && count >= maxCategories) {
            throw new ResourceExceedLimitException("Category", "store", maxCategories);
        }
        Group group = store.getGroup();
        Category category = CreateCategoryRequest.fromRequest(request);
        category.setGroup(group);
        category.setCreatedBy(user.getId());
        category.setStore(store);
        categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }


    @Transactional(readOnly = true)
    @Cacheable(value = CacheValue.CATEGORIES, key = "#storeId")
    public List<CategoryResponse> findAllCategoriesByStoreId(User user, UUID storeId) {
        Store store = storeServiceImpl.findStoreEntityById(user, storeId);
        if (ResourceOwner.hasPermission(user, store)) {
            return categoryRepository.findAllWithMenuCountByStoreId(storeId);
        } else {
            throw new ResourceForbiddenException(user.getUsername(), Store.class);
        }
    }

    private CategoryResponse toggleCategory(User user, CategoryToggleRequest request, boolean toggleVisibility) {
        Category category = getCategoryById(user, request);
        if (toggleVisibility) {
            category.setHidden(!category.isHidden());
        } else {
            category.setAvailable(!category.isAvailable());
        }
        category.setUpdatedBy(user.getId());
        categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.CATEGORIES, key = "#request.storeId"),
            @CacheEvict(value = CacheValue.CATEGORY_ENTITIES, key = "#request.storeId")
    })
    public CategoryResponse toggleCategoryVisibility(User user, CategoryToggleRequest request) {
        return toggleCategory(user, request, true);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.CATEGORIES, key = "#request.storeId"),
            @CacheEvict(value = CacheValue.CATEGORY_ENTITIES, key = "#request.storeId")
    })
    public CategoryResponse toggleCategoryAvailability(User user, CategoryToggleRequest request) {
        return toggleCategory(user, request, false);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.CATEGORIES, key = "#request.storeId"),
            @CacheEvict(value = CacheValue.CATEGORY_ENTITIES, key = "#request.storeId")
    })
    public CategoryResponse deleteCategoryById(User user, CategoryToggleRequest request) {
        Category category = getCategoryById(user, request);
        categoryRepository.delete(category);
        return CategoryResponse.fromEntity(category);
    }

    private Category getCategoryById(User user, CategoryToggleRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId().toString()));
        boolean isAdminOrManager = user.getRoles().stream()
                .map(Role::getName)
                .anyMatch(role -> role.equals(AuthRole.admin) || role.equals(AuthRole.manager));
        if (!isAdminOrManager && !category.getCreatedBy().equals(user.getId())) {
            throw new ResourceForbiddenException(user.getUsername(), Category.class);
        }
        return category;
    }

    @Transactional
    @Cacheable(value = CacheValue.CATEGORY_ENTITIES, key = "#storeId")
    public List<Category> findAllByStoreId(UUID storeId) {
        return categoryRepository.findAllByStoreIdOrderByPosition(storeId);
    }

    @Transactional
    @CacheEvict(value = CacheValue.CATEGORIES, allEntries = true)
    public void updateCategoryIcon(UUID id, String icon) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id.toString()));
        category.setIcon(icon);
        categoryRepository.save(category);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.CATEGORIES, key = "#storeId"),
            @CacheEvict(value = CacheValue.CATEGORY_ENTITIES, key = "#storeId"),
            @CacheEvict(value = CacheValue.MENUS, key = "#storeId"),
    })
    public List<CategoryResponse> orderCategoriesPositions(User user, UUID storeId, List<CategoryReorderRequest.CategoryPositionUpdate> updates) {
        Store store = storeServiceImpl.findStoreEntityById(user, storeId);
        if (ResourceOwner.hasPermission(user, store)) {
            List<UUID> categoryIds = updates.stream()
                    .map(CategoryReorderRequest.CategoryPositionUpdate::getId)
                    .toList();
            List<Category> categories = categoryRepository.findByIdIn(categoryIds);
            Map<UUID, Integer> updatedPositions = updates.stream()
                    .collect(Collectors.toMap(CategoryReorderRequest.CategoryPositionUpdate::getId,
                            CategoryReorderRequest.CategoryPositionUpdate::getPosition));
            categories.forEach(category -> category.setPosition(updatedPositions.get(category.getId())));
            categoryRepository.saveAll(categories);
            return CategoryResponse.fromEntities(categories);
        }
        throw new ResourceForbiddenException(user.getUsername(), Store.class);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.CATEGORY, key = "#request.storeId"),
            @CacheEvict(value = CacheValue.CATEGORIES, key = "#request.storeId"),
            @CacheEvict(value = CacheValue.CATEGORY_ENTITIES, key = "#request.storeId")
    })
    public CategoryResponse updateCategoryById(User user, UUID id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id.toString()));
        if (ResourceOwner.hasPermission(user, category)) {
            category.setName(request.getName());
            category.setDescription(request.getDescription());
            category.setUpdatedBy(user.getId());
            categoryRepository.save(category);
            return CategoryResponse.fromEntity(category);
        } else {
            throw new ResourceForbiddenException(user.getUsername(), Category.class);
        }
    }
}
