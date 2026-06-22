package com.logicarchive.storage;

import com.logicarchive.exception.FileStorageException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload.code-dir}")
    private String codeUploadDir;

    @Value("${file.upload.resource-dir}")
    private String resourceUploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(codeUploadDir));
            Files.createDirectories(Paths.get(resourceUploadDir));
            log.info("Upload directories initialized: code={}, resource={}", codeUploadDir, resourceUploadDir);
        } catch (IOException e) {
            throw new FileStorageException("Could not create upload directories", e);
        }
    }

    public String uploadCodeFile(MultipartFile file) {
        return storeFile(file, codeUploadDir);
    }

    public String uploadResourceFile(MultipartFile file) {
        return storeFile(file, resourceUploadDir);
    }

    public Resource downloadFile(String filePath) {
        try {
            Path path = Paths.get(filePath).normalize();
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new FileStorageException("File not found or not readable: " + filePath);
        } catch (MalformedURLException e) {
            throw new FileStorageException("File not found: " + filePath, e);
        }
    }

    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath).normalize();
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("Deleted file: {}", filePath);
            } else {
                log.warn("File not found for deletion: {}", filePath);
            }
        } catch (IOException e) {
            throw new FileStorageException("Could not delete file: " + filePath, e);
        }
    }

    private String storeFile(MultipartFile file, String uploadDir) {
        try {
            if (file.isEmpty()) {
                throw new FileStorageException("Cannot store empty file");
            }

            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID() + extension;

            Path targetPath = Paths.get(uploadDir).resolve(uniqueFileName).normalize();
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Stored file: {} as {}", originalFileName, uniqueFileName);
            return targetPath.toString();
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }
}
