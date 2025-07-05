package com.demo.ecommerce_backend.util;

import com.demo.ecommerce_backend.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileUploadUtil {

    private final AppProperties appProperties;

    public String saveImage(MultipartFile file, String subFolder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is missing or empty.");
        }

        // Base directory (e.g. uploads/)
        String uploadRoot = "uploads";
        String folderPath = uploadRoot + File.separator + subFolder;

        // Ensure the directory exists
        File dir = new File(folderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Create unique file name
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String filename = UUID.randomUUID() + extension;

        // Save file
        Path destinationPath = Paths.get(folderPath, filename);
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        // Return full URL (e.g., http://localhost:8080/uploads/categories/abc.jpg)
        String imageUrl = appProperties.getBaseUrl()
                + "/" + folderPath.replace("\\", "/")
                + "/" + filename;

        return imageUrl;
    }
}
