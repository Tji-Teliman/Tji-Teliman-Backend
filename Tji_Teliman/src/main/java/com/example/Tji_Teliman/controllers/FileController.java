package com.example.Tji_Teliman.controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrôleur pour servir les fichiers uploadés (images, fichiers vocaux, etc.)
 * Endpoint alternatif si le ResourceHandler ne fonctionne pas correctement
 */
@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileController {

    private final String uploadsBaseDir;

    public FileController() {
        // Déterminer le chemin du dossier uploads (relatif au répertoire de travail)
        Path uploadsPath = Paths.get("uploads").toAbsolutePath().normalize();
        this.uploadsBaseDir = uploadsPath.toString();
        
        // Log pour debug
        System.out.println("FileController - Chemin uploads: " + uploadsBaseDir);
    }

    /**
     * Servir un fichier uploadé
     * @param subDirectory Le sous-dossier (ex: "categories", "recruteurs", "jeunes", "vocaux")
     * @param filename Le nom du fichier
     * @return Le fichier avec les headers appropriés
     */
    @GetMapping("/{subDirectory}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(
            @PathVariable String subDirectory,
            @PathVariable String filename) {
        try {
            // Construire le chemin complet du fichier
            Path filePath = Paths.get(uploadsBaseDir, subDirectory, filename).normalize();
            
            // Vérifier que le fichier existe et est dans le dossier uploads (sécurité)
            File file = filePath.toFile();
            if (!file.exists() || !file.isFile()) {
                return ResponseEntity.notFound().build();
            }
            
            // Vérifier que le chemin normalisé est bien dans le dossier uploads (prévention des path traversal attacks)
            Path basePath = Paths.get(uploadsBaseDir).normalize();
            if (!filePath.startsWith(basePath)) {
                return ResponseEntity.badRequest().build();
            }
            
            Resource resource = new FileSystemResource(file);
            
            // Déterminer le type MIME
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                    .body(resource);
                    
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du fichier: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Servir un fichier directement depuis uploads/ (sans sous-dossier)
     * @param filename Le nom du fichier
     * @return Le fichier avec les headers appropriés
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFileDirect(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadsBaseDir, filename).normalize();
            
            File file = filePath.toFile();
            if (!file.exists() || !file.isFile()) {
                return ResponseEntity.notFound().build();
            }
            
            Path basePath = Paths.get(uploadsBaseDir).normalize();
            if (!filePath.startsWith(basePath)) {
                return ResponseEntity.badRequest().build();
            }
            
            Resource resource = new FileSystemResource(file);
            
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                    .body(resource);
                    
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du fichier: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

