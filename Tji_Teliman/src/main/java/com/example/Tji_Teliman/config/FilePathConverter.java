package com.example.Tji_Teliman.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utilitaire pour convertir les chemins absolus de fichiers en chemins relatifs
 * pour l'accès via HTTP depuis le frontend
 */
@Component
public class FilePathConverter {

    @Value("${app.file.base-url:}")
    private String baseUrl;

    /**
     * Convertit un chemin absolu en chemin relatif HTTP

     * 
     * @param absolutePath Le chemin absolu du fichier
     * @return Le chemin relatif pour l'accès HTTP, ou null si le chemin est null
     */
    public String toRelativePath(String absolutePath) {
        if (absolutePath == null || absolutePath.isEmpty()) {
            return null;
        }
        
        // Si c'est déjà un chemin relatif commençant par /uploads/ ou /api/files/, le retourner tel quel
        if (absolutePath.startsWith("/uploads/") || absolutePath.startsWith("/api/files/")) {
            return absolutePath;
        }
        
        // Si c'est une URL complète (http://...), la retourner tel quel
        if (absolutePath.startsWith("http://") || absolutePath.startsWith("https://")) {
            return absolutePath;
        }
        
        // Extraire le chemin relatif depuis le chemin absolu
        String relativePath = null;
        
        // Remplacer les chemins absolus Windows par des chemins relatifs
        if (absolutePath.contains("uploads\\")) {
            relativePath = absolutePath.replaceAll(".*uploads\\\\", "/uploads/").replace("\\", "/");
        }
        // Remplacer les chemins absolus Unix/Linux par des chemins relatifs
        else if (absolutePath.contains("uploads/")) {
            relativePath = absolutePath.replaceAll(".*uploads/", "/uploads/");
        }
        
        // Si aucun pattern n'a été trouvé, retourner le chemin tel quel (peut être une URL externe)
        if (relativePath == null) {
            return absolutePath;
        }
        
        // Si une baseUrl est configurée, l'utiliser (pour les environnements spécifiques)
        if (baseUrl != null && !baseUrl.isEmpty()) {
            return baseUrl + relativePath;
        }
        
        return relativePath;
    }
}

