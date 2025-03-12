package io.sovann.hang.api.features.files.controllers;

import io.sovann.hang.api.annotations.*;
import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.exceptions.*;
import io.sovann.hang.api.features.commons.payloads.*;
import io.sovann.hang.api.features.files.exceptions.*;
import io.sovann.hang.api.features.files.payloads.*;
import io.sovann.hang.api.features.files.services.*;
import io.sovann.hang.api.features.menus.entities.*;
import io.sovann.hang.api.features.menus.repos.*;
import io.sovann.hang.api.features.menus.services.*;
import io.sovann.hang.api.features.stores.entities.*;
import io.sovann.hang.api.features.stores.services.*;
import io.sovann.hang.api.features.users.securities.*;
import io.sovann.hang.api.utils.*;
import jakarta.persistence.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

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
            log.error(FILE_NOT_FOUND + ": {}", e.getMessage());
            return BaseResponse.<FileResponse>notFound()
                    .setError(e.getMessage());
        } catch (Exception e) {
            log.error(LOAD_FILE_ERROR + ": {}", e.getMessage());
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
            log.error(LOAD_FILE_ERROR + ": {}", e.getMessage());
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
            @CurrentUser CustomUserDetails user,
            @RequestParam("file") MultipartFile file,
            @RequestParam("id") UUID id,
            @RequestParam("type") String type
    ) {
        try {
            if (user == null || user.getUser() == null) {
                return BaseResponse.<FileResponse>accessDenied()
                        .setError("User is not permitted to upload file.");
            }
            String filename = fileStorageService.save(user.getUser(), file);
            FileResponse fileResponse = FileResponse.fromEntity(filename);
            fileResponse.setCreatedBy(user.getUser().getId());
            if (type.equalsIgnoreCase("menu")) {
                menuService.updateMenuImage(id, fileResponse.getName());
            } else if (type.equalsIgnoreCase("category")) {
                categoryService.updateCategoryIcon(id, fileResponse.getName());
            } else if (type.equalsIgnoreCase("store-promotion")) {
                Store store = storeService.getStoreEntityById(user.getUser(), id);
                storeService.updateStorePromotionImage(
                        user.getUser().getId(),
                        store,
                        fileResponse.getName());
            }
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