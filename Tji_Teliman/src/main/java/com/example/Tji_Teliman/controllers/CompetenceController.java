package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.entites.Competence;
import com.example.Tji_Teliman.services.CompetenceService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/{adminId}/competences")
public class CompetenceController {

    private final CompetenceService competenceService;

    public CompetenceController(CompetenceService competenceService) {
        this.competenceService = competenceService;
    }

    public record CreateCompetenceRequest(String nom) {}
    public record ApiResponse(boolean success, String message, Object data) {}

    @PostMapping
    public ResponseEntity<?> create(@PathVariable("adminId") Long adminId, @RequestBody CreateCompetenceRequest req) {
        try {
            Competence c = competenceService.create(req.nom(), adminId);
            return ResponseEntity.ok(new ApiResponse(true, "Compétence créée", c));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<List<Competence>> list(@PathVariable("adminId") Long adminId) {
        return ResponseEntity.ok(competenceService.listAll());
    }
}
