package io.sovann.hang.api.features.menus.services;

import io.sovann.hang.api.constants.CacheValue;
import io.sovann.hang.api.exceptions.ResourceForbiddenException;
import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.menus.entities.Category;
import io.sovann.hang.api.features.menus.payloads.requests.CategoryReorderRequest;
import io.sovann.hang.api.features.menus.payloads.requests.CategoryToggleRequest;
import io.sovann.hang.api.features.menus.payloads.requests.CreateCategoryRequest;
import io.sovann.hang.api.features.menus.payloads.requests.UpdateCategoryRequest;
import io.sovann.hang.api.features.menus.payloads.responses.CategoryResponse;
import io.sovann.hang.api.features.menus.repos.CategoryRepository;
import io.sovann.hang.api.features.stores.entities.Store;
import io.sovann.hang.api.features.stores.services.StoreServiceImpl;
import io.sovann.hang.api.features.users.entities.Group;
import io.sovann.hang.api.features.users.entities.Role;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.enums.AuthRole;
import io.sovann.hang.api.utils.ResourceOwner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl {
    private final CategoryRepository categoryRepository;
    private final StoreServiceImpl storeServiceImpl;

    public long count() {
        return categoryRepository.count();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheValue.CATEGORIES, key = "#request.storeId"),
            @CacheEvict(value = CacheValue.CATEGORY_ENTITIES, key = "#request.storeId")
    })
    public CategoryResponse create(User user, CreateCategoryRequest request) {
        Store store = storeServiceImpl.getStoreEntityById(user, request.getStoreId());
        if (ResourceOwner.hasPermission(user, store)) {
            Group group = store.getGroup();
            Category category = CreateCategoryRequest.fromRequest(request);
            category.setGroup(group);
            category.setCreatedBy(user.getId());
            category.setStore(store);
            categoryRepository.save(category);
            return CategoryResponse.fromEntity(category);
        }
        throw new ResourceForbiddenException(user.getUsername(), Store.class);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheValue.CATEGORIES, key = "#storeId")
    public List<CategoryResponse> list(User user, UUID storeId) {
        Store store = storeServiceImpl.getStoreEntityById(user, storeId);
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
    public CategoryResponse deleteCategory(User user, CategoryToggleRequest request) {
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
    public List<CategoryResponse> reorderCategories(User user, UUID storeId, List<CategoryReorderRequest.CategoryPositionUpdate> updates) {
        Store store = storeServiceImpl.getStoreEntityById(user, storeId);
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
    public CategoryResponse updateCategory(User user, UUID id, UpdateCategoryRequest request) {
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
