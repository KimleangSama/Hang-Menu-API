package io.sovann.hang.api.features.files.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.files.exceptions.FileStorageException;
import io.sovann.hang.api.features.files.payloads.FileResponse;
import io.sovann.hang.api.features.files.services.FileStorageServiceImpl;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
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

@Slf4j
@RestController
@RequestMapping(APIURLs.FILE)
@RequiredArgsConstructor
public class FileStorageController {
    public static final String FILE_NOT_FOUND = "File not found";
    public static final String LOAD_FILE_ERROR = "Load File Error";
    public static final String FILE_STORAGE_ERROR = "File Storage Error";

    private final FileStorageServiceImpl fileStorageService;

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

    @PostMapping("/upload")
//    @PreAuthorize("hasAnyRoles('admin', 'manager')")
    public BaseResponse<FileResponse> uploadFile(
            @CurrentUser CustomUserDetails user,
            @RequestParam("file") MultipartFile file) {
        try {
            if (user == null || user.getUser() == null) {
                return BaseResponse.<FileResponse>accessDenied()
                        .setError("User is not permitted to upload file.");
            }
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

    @PostMapping("/uploads")
    @PreAuthorize("authenticated")
    public BaseResponse<List<FileResponse>> uploadFiles(
            @CurrentUser CustomUserDetails user,
            @RequestParam("files") List<MultipartFile> files) {
        try {
            if (user == null || user.getUser() == null) {
                return BaseResponse.<List<FileResponse>>accessDenied()
                        .setError("User is not permitted to upload file.");
            }
            List<String> filenames = fileStorageService.saveAll(user.getUser(), files);
            return BaseResponse.<List<FileResponse>>ok()
                    .setPayload(FileResponse.fromEntities(filenames));
        } catch (FileStorageException e) {
            log.error(FILE_STORAGE_ERROR + "{}", e.getMessage());
            return BaseResponse.<List<FileResponse>>duplicateEntity()
                    .setError(e.getMessage());
        } catch (Exception e) {
            log.error(FILE_STORAGE_ERROR + "{}", e.getMessage(), e);
            return BaseResponse.<List<FileResponse>>exception()
                    .setError(e.getMessage());
        }
    }
}