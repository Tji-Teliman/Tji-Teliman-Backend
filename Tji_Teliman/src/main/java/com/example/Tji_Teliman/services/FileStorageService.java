package com.example.Tji_Teliman.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path baseDir;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String baseDir) throws IOException {
        this.baseDir = Paths.get(baseDir).toAbsolutePath().normalize();
        Files.createDirectories(this.baseDir);
    }

    public String store(MultipartFile file, String subDirectory) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        Path targetDir = baseDir.resolve(subDirectory == null ? "" : subDirectory).normalize();
        Files.createDirectories(targetDir);
        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains(".")) ? original.substring(original.lastIndexOf('.')) : "";
        String filename = UUID.randomUUID() + ext;
        Path target = targetDir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString();
    }
}


