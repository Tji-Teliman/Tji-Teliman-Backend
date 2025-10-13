package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.dto.CandidatureDTO;
import com.example.Tji_Teliman.dto.MotivationDTO;
import com.example.Tji_Teliman.entites.Candidature;
import com.example.Tji_Teliman.services.CandidatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidatures")
public class CandidatureController {

    private final CandidatureService candidatureService;

    public CandidatureController(CandidatureService candidatureService) {
        this.candidatureService = candidatureService;
    }

    public record PostulerRequest(
        String motivationContenu
    ) {}

    public record ApiResponse(boolean success, String message, Object data) {}

    @PostMapping("/jeune/{jeuneId}/mission/{missionId}")
    public ResponseEntity<?> postuler(
            @PathVariable Long jeuneId,
            @PathVariable Long missionId,
            @RequestBody PostulerRequest request) {
        try {
            Candidature candidature = candidatureService.postuler(jeuneId, missionId, request.motivationContenu());
            return ResponseEntity.ok(new ApiResponse(true, "Candidature soumise avec succès", candidatureService.toCandidatureDTO(candidature)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @GetMapping("/jeune/{jeuneId}")
    public ResponseEntity<List<CandidatureDTO>> getCandidaturesByJeune(@PathVariable Long jeuneId) {
        return ResponseEntity.ok(candidatureService.getCandidaturesByJeune(jeuneId));
    }

    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<CandidatureDTO>> getCandidaturesByMission(@PathVariable Long missionId) {
        return ResponseEntity.ok(candidatureService.getCandidaturesByMission(missionId));
    }

    @GetMapping("/{candidatureId}/motivations")
    public ResponseEntity<List<MotivationDTO>> getMotivationsByCandidature(@PathVariable Long candidatureId) {
        return ResponseEntity.ok(candidatureService.getMotivationsByCandidature(candidatureId));
    }

    @PutMapping("/{candidatureId}/valider")
    public ResponseEntity<?> validerCandidature(
            @PathVariable Long candidatureId,
            @RequestParam Long recruteurId) {
        try {
            Candidature candidature = candidatureService.validerCandidature(candidatureId, recruteurId);
            return ResponseEntity.ok(new ApiResponse(true, "Candidature validée avec succès", candidatureService.toCandidatureDTO(candidature)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @PutMapping("/{candidatureId}/rejeter")
    public ResponseEntity<?> rejeterCandidature(
            @PathVariable Long candidatureId,
            @RequestParam Long recruteurId) {
        try {
            Candidature candidature = candidatureService.rejeterCandidature(candidatureId, recruteurId);
            return ResponseEntity.ok(new ApiResponse(true, "Candidature rejetée avec succès", candidatureService.toCandidatureDTO(candidature)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }
}
