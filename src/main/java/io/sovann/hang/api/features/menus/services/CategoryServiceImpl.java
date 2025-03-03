package io.sovann.hang.api.features.menus.services;

import io.sovann.hang.api.exceptions.*;
import io.sovann.hang.api.features.menus.entities.*;
import io.sovann.hang.api.features.menus.payloads.requests.*;
import io.sovann.hang.api.features.menus.payloads.responses.*;
import io.sovann.hang.api.features.menus.repos.*;
import io.sovann.hang.api.features.stores.entities.*;
import io.sovann.hang.api.features.stores.services.*;
import io.sovann.hang.api.features.users.entities.*;
import io.sovann.hang.api.features.users.enums.*;
import java.util.*;
import lombok.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl {
    private final CategoryRepository categoryRepository;
    private final StoreServiceImpl storeServiceImpl;

    public long count() {
        return categoryRepository.count();
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(User user, CreateCategoryRequest request) {
        Store store = storeServiceImpl.getStoreEntityById(user, request.getStoreId());
        Category category = CreateCategoryRequest.fromRequest(request);
        category.setCreatedBy(user.getId());
        category.setStore(store);
        categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#storeId")
    public List<CategoryResponse> listCategories(User user, UUID storeId) {
        List<Category> categories = categoryRepository.findAllByStoreId(storeId);
        return CategoryResponse.fromEntities(categories);
    }

    public CategoryResponse toggleCategory(User user, CategoryToggleRequest request, boolean toggleVisibility) {
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
    @CacheEvict(value = "categories", key = "#user.id")
    public CategoryResponse toggleCategoryVisibility(User user, CategoryToggleRequest request) {
        return toggleCategory(user, request, true);
    }

    @Transactional
    @CacheEvict(value = "categories", key = "#user.id")
    public CategoryResponse toggleCategoryAvailability(User user, CategoryToggleRequest request) {
        return toggleCategory(user, request, false);
    }

    @Transactional
    @CacheEvict(value = "categories", key = "#user.id")
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
    @Cacheable(value = "category-entities", key = "#storeId")
    public List<Category> findAllByStoreId(UUID storeId) {
        return categoryRepository.findAllByStoreId(storeId);
    }

    @Transactional
    @CacheEvict(value = "categories", key = "#id")
    public void updateCategoryIcon(UUID id, String icon) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id.toString()));
        category.setIcon(icon);
        categoryRepository.save(category);
    }
}
