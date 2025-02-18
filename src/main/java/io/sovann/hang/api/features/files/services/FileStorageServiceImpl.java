package io.sovann.hang.api.features.files.services;

import io.sovann.hang.api.features.files.exceptions.FileStorageException;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.utils.RandomString;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class FileStorageServiceImpl {
    private final Path root;
    private static final long MAX_FILE_SIZE = 10_000_000; // 10MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");

    public FileStorageServiceImpl(@Value("${app.upload.base-dir:uploads}") String baseDir) {
        this.root = Path.of(baseDir).toAbsolutePath().normalize();
        init();
    }

    @PostConstruct
    private void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new FileStorageException("Failed to initialize storage", e);
        }
    }

    public String save(User user, MultipartFile file) throws FileStorageException {
        validateFile(file);
        String filename = generateSecureFilename(user, file);
        Path targetPath = root.resolve(filename);
        validatePath(targetPath);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file " + filename, e);
        }
    }

    public Resource load(String filename) throws FileStorageException {
        try {
            Path filePath = root.resolve(filename).normalize();
            validatePath(filePath);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new FileStorageException("File not found or not readable: " + filename);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new FileStorageException("Invalid file path: " + filename, e);
        }
    }

    public void delete(User user, String filename) throws FileStorageException {
        try {
            String userId = user.getId().toString();
            if (!filename.startsWith(userId)) {
                throw new FileStorageException("Unauthorized to delete file: " + filename);
            }
            Path filePath = root.resolve(filename).normalize();
            validatePath(filePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete file: " + filename, e);
        }
    }

    private void validateFile(MultipartFile file) throws FileStorageException {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("File is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileStorageException("File exceeds maximum size of " + MAX_FILE_SIZE + " bytes");
        }
        String extension = getFileExtension(file);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new FileStorageException("File type not allowed: " + extension);
        }
    }

    private void validatePath(Path path) throws FileStorageException {
        if (!path.normalize().startsWith(root)) {
            throw new FileStorageException("Path traversal attempt detected");
        }
    }

    private String generateSecureFilename(User user, MultipartFile file) throws FileStorageException {
        String extension = getFileExtension(file);
        return user.getId().toString() + "_" + RandomString.make(6) + "." + extension;
    }

    private String getFileExtension(MultipartFile file) throws FileStorageException {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new FileStorageException("Invalid filename");
        }
        return FilenameUtils.getExtension(filename).toLowerCase();
    }

    public List<String> saveAll(User user, List<MultipartFile> files) {
        List<String> filenames = new java.util.ArrayList<>(files.stream()
                .map(file -> {
                    try {
                        return save(user, file);
                    } catch (FileStorageException e) {
                        log.error("Failed to save file: {}", e.getMessage());
                        return null;
                    }
                })
                .toList());
        filenames.removeIf(Objects::isNull);
        return filenames;
    }
}
