package io.sovann.hang.api.features.menus.services;

import io.sovann.hang.api.exceptions.ResourceForbiddenException;
import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.menus.entities.Category;
import io.sovann.hang.api.features.menus.payloads.requests.CategoryToggleRequest;
import io.sovann.hang.api.features.menus.payloads.requests.CreateCategoryRequest;
import io.sovann.hang.api.features.menus.payloads.responses.CategoryResponse;
import io.sovann.hang.api.features.menus.repos.CategoryRepository;
import io.sovann.hang.api.features.users.entities.Role;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.enums.AuthRole;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl {
    private final CategoryRepository categoryRepository;

    public long count() {
        return categoryRepository.count();
    }

    @Transactional
    @CacheEvict(value = "Categories", allEntries = true)
    public CategoryResponse createCategory(User user, CreateCategoryRequest request) {
        Category category = CreateCategoryRequest.fromRequest(request);
        category.setCreatedBy(user.getId());
        categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "Categories", key = "#user.id")
    public List<CategoryResponse> listCategories(User user, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        List<Category> Categories = categoryRepository.findAll(pageable).getContent();
        return CategoryResponse.fromEntities(Categories);
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
    @CacheEvict(value = "Categories", key = "#user.id")
    public CategoryResponse toggleCategoryVisibility(User user, CategoryToggleRequest request) {
        return toggleCategory(user, request, true);
    }

    @Transactional
    @CacheEvict(value = "Categories", key = "#user.id")
    public CategoryResponse toggleCategoryAvailability(User user, CategoryToggleRequest request) {
        return toggleCategory(user, request, false);
    }

    @Transactional
    @CacheEvict(value = "Categories", key = "#user.id")
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

}
