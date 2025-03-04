package io.sovann.hang.api.features.menus.services;

import io.sovann.hang.api.exceptions.ResourceForbiddenException;
import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.menus.entities.Category;
import io.sovann.hang.api.features.menus.payloads.requests.CategoryReorderRequest;
import io.sovann.hang.api.features.menus.payloads.requests.CategoryToggleRequest;
import io.sovann.hang.api.features.menus.payloads.requests.CreateCategoryRequest;
import io.sovann.hang.api.features.menus.payloads.responses.CategoryResponse;
import io.sovann.hang.api.features.menus.repos.CategoryRepository;
import io.sovann.hang.api.features.stores.entities.Store;
import io.sovann.hang.api.features.stores.services.StoreServiceImpl;
import io.sovann.hang.api.features.users.entities.Role;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.enums.AuthRole;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
            @CacheEvict(value = "categories", key = "#request.storeId"),
            @CacheEvict(value = "category-entities", key = "#request.storeId")
    })
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
        List<Category> categories = categoryRepository.findAllByStoreIdOrderByPosition(storeId);
        return CategoryResponse.fromEntities(categories);
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
            @CacheEvict(value = "categories", key = "#request.storeId"),
            @CacheEvict(value = "category-entities", key = "#request.storeId")
    })
    public CategoryResponse toggleCategoryVisibility(User user, CategoryToggleRequest request) {
        return toggleCategory(user, request, true);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "categories", key = "#request.storeId"),
            @CacheEvict(value = "category-entities", key = "#request.storeId")
    })
    public CategoryResponse toggleCategoryAvailability(User user, CategoryToggleRequest request) {
        return toggleCategory(user, request, false);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "categories", key = "#request.storeId"),
            @CacheEvict(value = "category-entities", key = "#request.storeId")
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
    @Cacheable(value = "category-entities", key = "#storeId")
    public List<Category> findAllByStoreId(UUID storeId) {
        return categoryRepository.findAllByStoreIdOrderByPosition(storeId);
    }

    @Transactional
    @CacheEvict(value = "categories", key = "#id")
    public void updateCategoryIcon(UUID id, String icon) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id.toString()));
        category.setIcon(icon);
        categoryRepository.save(category);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "categories", key = "#storeId"),
            @CacheEvict(value = "category-entities", key = "#storeId")
    })
    public List<CategoryResponse> reorderCategories(User user, UUID storeId, List<CategoryReorderRequest.CategoryPositionUpdate> updates) {
        // Extract IDs from the request
        List<UUID> categoryIds = updates.stream()
                .map(CategoryReorderRequest.CategoryPositionUpdate::getId)
                .toList();
        // Fetch categories from DB
        List<Category> categories = categoryRepository.findByIdIn(categoryIds);
        // Convert updates into a map for quick lookup
        Map<UUID, Integer> updatedPositions = updates.stream()
                .collect(Collectors.toMap(CategoryReorderRequest.CategoryPositionUpdate::getId,
                        CategoryReorderRequest.CategoryPositionUpdate::getPosition));
        // Update positions
        categories.forEach(category -> category.setPosition(updatedPositions.get(category.getId())));
        // Save all updates in batch
        categoryRepository.saveAll(categories);
        // Return updated categories
        return CategoryResponse.fromEntities(categories);
    }
}
