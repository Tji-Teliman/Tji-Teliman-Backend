package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.config.JwtUtils;
import com.example.Tji_Teliman.entites.Categorie;
import com.example.Tji_Teliman.dto.CategorieDTO;
import com.example.Tji_Teliman.services.CategorieService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/categories")
public class CategorieController {

    private final CategorieService categorieService;
    private final JwtUtils jwtUtils;

    public CategorieController(CategorieService categorieService, JwtUtils jwtUtils) {
        this.categorieService = categorieService;
        this.jwtUtils = jwtUtils;
    }

    public record CategorieCreateRequest(String nom) {}
    public record ApiResponse(boolean success, String message, Object data) {}

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> create(@RequestParam String nom, @RequestParam MultipartFile photo, HttpServletRequest httpRequest) {
        try {
            Long adminId = jwtUtils.getUserIdFromToken(httpRequest);
            if (adminId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            Categorie c = categorieService.create(adminId, nom, photo);
            CategorieDTO dto = categorieService.toDTO(c);
            return ResponseEntity.ok(new ApiResponse(true, "Catégorie créée", dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<List<CategorieDTO>> list() {
        var list = categorieService.list().stream().map(categorieService::toDTO).toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> update(@PathVariable Long id, @RequestParam(required=false) String nom, @RequestParam(required=false) MultipartFile photo, HttpServletRequest httpRequest) {
        try {
            Long adminId = jwtUtils.getUserIdFromToken(httpRequest);
            if (adminId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            Categorie c = categorieService.update(id, nom, photo);
            CategorieDTO dto = categorieService.toDTO(c);
            return ResponseEntity.ok(new ApiResponse(true, "Catégorie mise à jour", dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            Long adminId = jwtUtils.getUserIdFromToken(httpRequest);
            if (adminId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            categorieService.delete(id);
            return ResponseEntity.ok(new ApiResponse(true, "Catégorie supprimée", null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }
}
