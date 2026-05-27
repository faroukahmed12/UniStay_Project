package com.unistay.housing_management_system.services;

import com.unistay.housing_management_system.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file, String universityId) {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String fileName = universityId + "_" + System.currentTimeMillis() + "." + extension;

            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return uploadDir + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }

    public Resource loadFile(String filePath) {
        try {
            Path path = Paths.get(filePath).toAbsolutePath().normalize();
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists()) return resource;
            throw new ResourceNotFoundException("File not found: " + filePath);
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("File not found: " + filePath);
        }
    }
}