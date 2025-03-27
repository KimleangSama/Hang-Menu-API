package io.sovann.hang.api.features.files.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.commons.payloads.BaseResponse;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.files.exceptions.FileStorageException;
import io.sovann.hang.api.features.files.payloads.FileResponse;
import io.sovann.hang.api.features.files.services.FileStorageServiceImpl;
import io.sovann.hang.api.features.menus.entities.Menu;
import io.sovann.hang.api.features.menus.entities.MenuImage;
import io.sovann.hang.api.features.menus.repos.MenuImageRepository;
import io.sovann.hang.api.features.menus.services.CategoryServiceImpl;
import io.sovann.hang.api.features.menus.services.MenuServiceImpl;
import io.sovann.hang.api.features.stores.entities.Store;
import io.sovann.hang.api.features.stores.services.StoreServiceImpl;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import io.sovann.hang.api.utils.ResourceOwner;
import io.sovann.hang.api.utils.SoftEntityDeletable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@RestController
@RequestMapping(APIURLs.FILE)
@RequiredArgsConstructor
public class FileStorageController {
    public static final String FILE_NOT_FOUND = "File not found";
    public static final String LOAD_FILE_ERROR = "Load File Error";
    public static final String FILE_STORAGE_ERROR = "File Storage Error";

    private final FileStorageServiceImpl fileStorageService;
    private final MenuServiceImpl menuService;
    private final StoreServiceImpl storeService;
    private final MenuImageRepository menuImageRepository;
    private final CategoryServiceImpl categoryService;
    private final FileStorageServiceImpl fileStorageServiceImpl;

    @GetMapping("/load/{filename}")
    public BaseResponse<FileResponse> loadFile(
            @PathVariable String filename) {
        try {
            Resource resource = fileStorageService.load(filename);
            if (resource == null || !resource.exists()) {
                return BaseResponse.<FileResponse>notFound()
                        .setError(FILE_NOT_FOUND);
            }
            FileResponse fileResponse = FileResponse.fromEntity(filename);
            return BaseResponse.<FileResponse>ok()
                    .setPayload(fileResponse);
        } catch (ResourceNotFoundException e) {
            return BaseResponse.<FileResponse>notFound()
                    .setError(e.getMessage());
        } catch (Exception e) {
            return BaseResponse.<FileResponse>exception()
                    .setError(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @GetMapping("/view/{filename}")
    public ResponseEntity<Resource> loadView(
            @PathVariable String filename) {
        try {
            Resource resource = fileStorageService.load(filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("image/jpeg"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<FileResponse> uploadFile(
            @CurrentUser CustomUserDetails user,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            SoftEntityDeletable.throwErrorIfSoftDeleted(user);
            String filename = fileStorageService.save(user.getUser(), file);
            FileResponse fileResponse = FileResponse.fromEntity(filename);
            fileResponse.setCreatedBy(user.getUser().getId());
            return BaseResponse.<FileResponse>ok()
                    .setPayload(fileResponse);
        } catch (FileStorageException e) {
            log.error(FILE_STORAGE_ERROR + "{}", e.getMessage());
            return BaseResponse.<FileResponse>duplicateEntity()
                    .setError(e.getMessage());
        } catch (Exception e) {
            log.error(FILE_STORAGE_ERROR + "{}", e.getMessage(), e);
            return BaseResponse.<FileResponse>exception()
                    .setError(e.getMessage());
        }
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<FileResponse> uploadFile(
            @CurrentUser CustomUserDetails currentUser,
            @RequestParam("file") MultipartFile file,
            @RequestParam("id") UUID id,
            @RequestParam("type") String type
    ) {
        if (currentUser == null || currentUser.getUser() == null) {
            return BaseResponse.<FileResponse>accessDenied()
                    .setError("User is not permitted to upload file.");
        }
        User user = currentUser.getUser();
        try {
            String filename = fileStorageService.save(user, file);
            FileResponse fileResponse = FileResponse.fromEntity(filename);
            fileResponse.setCreatedBy(user.getId());
            Map<String, Consumer<String>> typeActions = Map.of(
                    "menu", name -> menuService.updateMenuImage(id, name),
                    "category", name -> categoryService.updateCategoryIcon(id, name)
            );
            if (typeActions.containsKey(type.toLowerCase())) {
                typeActions.get(type.toLowerCase()).accept(fileResponse.getName());
            } else {
                return handleStoreImageUpdate(user, id, type, fileResponse.getName());
            }
            return BaseResponse.<FileResponse>ok().setPayload(fileResponse);
        } catch (FileStorageException e) {
            return BaseResponse.<FileResponse>duplicateEntity().setError(e.getMessage());
        } catch (Exception e) {
            log.error(FILE_STORAGE_ERROR + "{}", e.getMessage(), e);
            return BaseResponse.<FileResponse>exception().setError(e.getMessage());
        }
    }

    private BaseResponse<FileResponse> handleStoreImageUpdate(User user, UUID id, String type, String filename) {
        Store store = storeService.findStoreEntityById(user, id);
        if (!ResourceOwner.hasPermission(user, store)) {
            fileStorageServiceImpl.delete(user, filename);
            return BaseResponse.<FileResponse>accessDenied().setError("User is not permitted to upload file.");
        }
        String imageType = switch (type.toLowerCase()) {
            case "store-banner" -> "banner";
            case "store-promotion" -> "promotion";
            default -> null;
        };
        if (imageType != null) {
            storeService.updateStoreImage(user.getId(), store, imageType, filename);
        }
        return BaseResponse.<FileResponse>ok().setPayload(FileResponse.fromEntity(filename));
    }


    @PostMapping("/uploads")
    @PreAuthorize("authenticated")
    public BaseResponse<List<FileResponse>> uploadFiles(
            @CurrentUser CustomUserDetails user,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("id") UUID id,
            @RequestParam("type") String type
    ) {
        try {
            if (user == null || user.getUser() == null) {
                return BaseResponse.<List<FileResponse>>accessDenied()
                        .setError("User is not permitted to upload files.");
            }
            List<String> filenames = fileStorageService.saveAll(user.getUser(), files);
            if (type.equalsIgnoreCase("menu")) {
                Menu menu = menuService.getMenuEntityById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Menu not found"));
                List<MenuImage> menuImages = filenames.stream()
                        .map(filename -> new MenuImage(menu, filename, APIURLs.BASE + "/view/" + filename))
                        .toList();
                menuImageRepository.saveAll(menuImages);
            }
            return BaseResponse.<List<FileResponse>>ok()
                    .setPayload(FileResponse.fromEntities(filenames));
        } catch (EntityNotFoundException e) {
            return BaseResponse.<List<FileResponse>>notFound().setError(e.getMessage());
        } catch (FileStorageException e) {
            log.error(FILE_STORAGE_ERROR + "{}", e.getMessage(), e);
            return BaseResponse.<List<FileResponse>>duplicateEntity().setError(e.getMessage());
        } catch (Exception e) {
            log.error(FILE_STORAGE_ERROR + "{}", e.getMessage(), e);
            return BaseResponse.<List<FileResponse>>exception().setError(e.getMessage());
        }
    }
}