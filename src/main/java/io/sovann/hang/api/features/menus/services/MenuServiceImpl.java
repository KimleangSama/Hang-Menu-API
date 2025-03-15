package io.sovann.hang.api.features.menus.services;

import io.sovann.hang.api.configs.RabbitMQConfig;
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
import io.sovann.hang.api.features.users.entities.User;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(MenuServiceImpl.class);
    private final MenuRepository menuRepository;
    private final MenuImageRepository menuImageRepository;
    private final FavoriteServiceImpl favoriteService;
    private final CategoryRepository categoryRepository;
    private final CategoryServiceImpl categoryServiceImpl;

    private final RabbitTemplate rabbitTemplate;
    private final FileStorageServiceImpl fileStorageServiceImpl;

    @Transactional
    public long count() {
        return menuRepository.count();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "menus", key = "#request.storeId"),
            @CacheEvict(value = "menus-categories", key = "#request.storeId"),
            @CacheEvict(value = "categories", key = "#request.storeId"),
            @CacheEvict(value = "category-entities", key = "#request.storeId")
    })
    public MenuResponse createMenu(User user, CreateMenuRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId().toString()));
        Menu menu = CreateMenuRequest.fromRequest(request);
        menu.setCategory(category);
        menu.setCreatedBy(user.getId());
        Menu savedMenu = menuRepository.save(menu);
        saveMenuImages(savedMenu, request.getImages());
        return MenuResponse.fromEntity(savedMenu);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "menus", key = "#request.storeId"),
            @CacheEvict(value = "menus-categories", allEntries = true)
    })
    public MenuResponse updateMenu(User user, UUID id, UpdateMenuRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu", id.toString()));
        if (!menu.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId().toString()));
            menu.setCategory(category);
        }
        List<String> existingImages = menu.getImages().stream().map(MenuImage::getName).toList();
        fileStorageServiceImpl.deleteAllInExclude(existingImages, request.getImages());
        updateMenuFields(menu, user, request);
        Menu savedMenu = menuRepository.save(menu);
        updateMenuImages(savedMenu, request.getImages());
        return MenuResponse.fromEntity(savedMenu);
    }

    private void saveMenuImages(Menu menu, List<String> imageRequests) {
        List<MenuImage> images = imageRequests.stream()
                .map(CreateMenuImageRequest::fromRequest)
                .peek(image -> image.setMenu(menu))
                .toList();
        menuImageRepository.saveAll(images);
        menu.setImages(images);
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

    private void updateMenuFields(Menu menu, User user, UpdateMenuRequest request) {
        menu.setCode(request.getCode());
        menu.setName(request.getName());
        menu.setDescription(request.getDescription());
        menu.setPrice(request.getPrice());
        menu.setDiscount(request.getDiscount());
        menu.setCurrency(request.getCurrency());
        menu.setImage(request.getImage());
        menu.setBadges(request.getBadges());
        menu.setUpdatedBy(user.getId());
    }

    @Transactional
    @Cacheable(value = "menus-categories", key = "#categoryId")
    public List<MenuResponse> listMenusOfCategoryId(
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
        List<Menu> menus = menuRepository.findAllByCategoryInOrderByCreatedAtDesc(categories);
        if (user == null) {
            return MenuResponse.fromEntities(menus, Collections.emptyList());
        }
        List<FavoriteResponse> favorites = favoriteService.listMenuFavorites(user);
        return MenuResponse.fromEntities(menus, favorites);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "menus", key = "#request.storeId"),
            @CacheEvict(value = "menus", key = "#request.menuId"),
            @CacheEvict(value = "menus-categories", allEntries = true)
    })
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
    @Caching(evict = {
            @CacheEvict(value = "menus", key = "#request.storeId"),
            @CacheEvict(value = "menus", key = "#request.menuId"),
            @CacheEvict(value = "menus-categories", allEntries = true)
    })
    public MenuResponse deleteMenu(User user, MenuToggleRequest request) {
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElse(null);
        if (menu == null) {
            return null;
        }
        menu.setDeleted(true);
        menuRepository.save(menu);
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
            @CacheEvict(value = "menus", key = "#id"),
            @CacheEvict(value = "menus-categories", allEntries = true)
    })
    public void updateMenuImage(UUID id, String image) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu", id.toString()));
        menu.setImage(image);
        menuRepository.save(menu);
    }

    @Transactional
    public String batchMenuCreate(
            User user,
            UUID storeId,
            MultipartFile file
    ) throws IOException {
        if (file.isEmpty()) return "File is empty!";
        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVParser csvParser = CSVParser.parse(reader, CSVFormat.Builder.create()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .get())) {
            List<CreateMenuRequest> batch = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                try {
                    CreateMenuRequest menu = new CreateMenuRequest();
                    menu.setCreatedBy(user.getId());
                    menu.setStoreId(storeId);
                    menu.setCode(record.get("code"));
                    menu.setName(record.get("name"));
                    menu.setDescription(record.get("description"));
                    menu.setPrice(Double.parseDouble(record.get("price")));
                    menu.setDiscount(Double.parseDouble(record.get("discount")));
                    menu.setCurrency(record.get("currency"));
                    menu.setImage(record.get("image"));
                    menu.setHidden(Boolean.parseBoolean(record.get("isHidden")));
                    String badgesStr = record.get("badges");
                    if (badgesStr != null && !badgesStr.trim().isEmpty()) {
                        menu.setBadges(Arrays.stream(badgesStr.split(",")).map(String::trim).toList());
                    } else {
                        menu.setBadges(Collections.emptyList());
                    }
                    menu.setCategoryId(UUID.fromString(record.get("categoryId")));
                    batch.add(menu);
                } catch (NumberFormatException e) {
                    return "Invalid number format in row " + record.getRecordNumber();
                } catch (IllegalArgumentException e) {
                    return "Invalid data in row " + record.getRecordNumber() + ": " + e.getMessage();
                }
            }
            if (!batch.isEmpty()) {
                rabbitTemplate.convertAndSend(RabbitMQConfig.BATCH_MENU_QUEUE, batch);
                return "Batch menu creation is in progress!";
            } else {
                return "No valid data found in the file!";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @RabbitListener(queues = RabbitMQConfig.BATCH_MENU_QUEUE)
    public void processBatch(List<CreateMenuRequest> menuList) {
        List<Menu> menus = new ArrayList<>();
        for (CreateMenuRequest m : menuList) {
            try {
                Category category = categoryRepository.findById(m.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category", m.getCategoryId().toString()));
                Menu menu = CreateMenuRequest.fromRequest(m);
                menu.setCreatedBy(m.getCreatedBy());
                menu.setCategory(category);
                menus.add(menu);
            } catch (ResourceNotFoundException e) {
                log.error("Error processing menu: {}", e.getMessage());
            }
        }
        menuRepository.saveAll(menus);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "menus", key = "#request.storeId"),
            @CacheEvict(value = "menus-categories", key = "#request.storeId"),
            @CacheEvict(value = "categories", key = "#request.storeId"),
    })
    public MenuResponse updateMenuCategory(User user, UUID id, UpdateMenuCategoryRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu", id.toString()));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId().toString()));
        menu.setCategory(category);
        menuRepository.save(menu);
        return MenuResponse.fromEntity(menu);
    }
}
