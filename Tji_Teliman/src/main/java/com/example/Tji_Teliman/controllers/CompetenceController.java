package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.config.JwtUtils;
import com.example.Tji_Teliman.entites.Competence;
import com.example.Tji_Teliman.services.CompetenceService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/competences")
public class CompetenceController {

    private final CompetenceService competenceService;
    private final JwtUtils jwtUtils;

    public CompetenceController(CompetenceService competenceService, JwtUtils jwtUtils) {
        this.competenceService = competenceService;
        this.jwtUtils = jwtUtils;
    }

    public record CreateCompetenceRequest(String nom) {}
    public record ApiResponse(boolean success, String message, Object data) {}

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateCompetenceRequest req, HttpServletRequest httpRequest) {
        try {
            Long adminId = jwtUtils.getUserIdFromToken(httpRequest);
            if (adminId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            Competence c = competenceService.create(req.nom(), adminId);
            return ResponseEntity.ok(new ApiResponse(true, "Compétence créée", c));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<List<Competence>> list() {
        return ResponseEntity.ok(competenceService.listAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CreateCompetenceRequest req, HttpServletRequest httpRequest) {
        try {
            Long adminId = jwtUtils.getUserIdFromToken(httpRequest);
            if (adminId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            Competence c = competenceService.update(id, req.nom());
            return ResponseEntity.ok(new ApiResponse(true, "Compétence mise à jour", c));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            Long adminId = jwtUtils.getUserIdFromToken(httpRequest);
            if (adminId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            competenceService.delete(id);
            return ResponseEntity.ok(new ApiResponse(true, "Compétence supprimée", null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }
}
