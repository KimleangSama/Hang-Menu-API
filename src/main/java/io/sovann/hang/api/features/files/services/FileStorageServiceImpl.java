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

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.channels.Channels;
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
    private static final float IMAGE_COMPRESSION_QUALITY = 0.6f; // Adjust compression quality as needed

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
            String extension = getFileExtension(file);
            if (ALLOWED_EXTENSIONS.contains(extension)) {
                compressImage(inputStream, targetPath, extension);
            } else {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            return filename;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file " + filename, e);
        }
    }

    private void compressImage(InputStream inputStream, Path targetPath, String extension) throws IOException {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                // Handle cases where ImageIO.read returns null (e.g., invalid image format)
                Files.copy(Channels.newInputStream(Channels.newChannel(inputStream)), targetPath, StandardCopyOption.REPLACE_EXISTING);
                return;
            }

            FileOutputStream outputStream = new FileOutputStream(targetPath.toFile());
            ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
            ImageWriter writer = ImageIO.getImageWritersByFormatName(extension).next();
            ImageWriteParam param = writer.getDefaultWriteParam();

            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(IMAGE_COMPRESSION_QUALITY);

            writer.setOutput(imageOutputStream);
            writer.write(null, new IIOImage(image, null, null), param);

            imageOutputStream.close();
            outputStream.close();
            writer.dispose();

        } catch (IOException e) {
            log.error("Error during image compression: {}", e.getMessage());
            // If compression fails, fallback to saving the original file
            inputStream.reset(); // Reset input stream to the beginning
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
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

    public void deleteAllInExclude(List<String> existingImages, List<String> excludeImages) {
        existingImages.forEach(filename -> {
            try {
                if (!excludeImages.contains(filename)) {
                    Files.deleteIfExists(root.resolve(filename));
                }
            } catch (IOException e) {
                log.error("Failed to delete file: {}", e.getMessage());
            }
        });
    }
}