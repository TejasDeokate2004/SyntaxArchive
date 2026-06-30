package com.logicarchive.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20 MB

    private static final List<String> ALLOWED_CODE_EXT =
            List.of(".java", ".py", ".cpp", ".c", ".js", ".ts", ".go", ".kt", ".txt");

    private static final List<String> ALLOWED_RESOURCE_EXT =
            List.of(".pdf", ".doc", ".docx", ".ppt", ".pptx", ".txt", ".png", ".jpg", ".jpeg", ".zip");

    private final Path uploadRoot;
    private final String publicBasePath;

    public FileStorageService(
            @Value("${file.upload-dir}") String uploadDir,
            @Value("${file.public-base-path:/uploads}") String publicBasePath
    ) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.publicBasePath = publicBasePath;

        try {
            Files.createDirectories(this.uploadRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload directory", e);
        }
    }

    // Stores a code file (java, py, cpp...) and returns the stored filename
    public String storeCodeFile(MultipartFile file) {
        validate(file, ALLOWED_CODE_EXT);
        return store(file);
    }

    // Stores a resource file (pdf, image, docs...) and returns the stored filename
    public String storeResourceFile(MultipartFile file) {
        validate(file, ALLOWED_RESOURCE_EXT);
        return store(file);
    }

    public void delete(String filename) {
        if (filename == null || filename.isBlank()) return;

        try {
            Files.deleteIfExists(uploadRoot.resolve(filename).normalize());
        } catch (IOException e) {
            // Do NOT crash the app for a cleanup failure
            System.err.println("Failed to delete file: " + filename);
        }
    }

    public String buildPublicUrl(String filename) {
        if (filename == null || filename.isBlank()) return null;
        return publicBasePath + "/" + filename;
    }

    private String store(MultipartFile file) {
        String cleanFilename = generateSafeFilename(file.getOriginalFilename());
        Path destination = uploadRoot.resolve(cleanFilename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + cleanFilename, e);
        }

        return cleanFilename;
    }

    private void validate(MultipartFile file, List<String> allowedExtensions) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum limit of 20 MB");
        }
        String name = file.getOriginalFilename();
        if (name == null || allowedExtensions.stream().noneMatch(ext -> name.toLowerCase().endsWith(ext))) {
            throw new IllegalArgumentException("Invalid file type. Allowed: " + allowedExtensions);
        }
    }

    private String generateSafeFilename(String originalFilename) {
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + ext;
    }
}
