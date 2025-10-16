package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.config.JwtUtils;
import com.example.Tji_Teliman.dto.CandidatureDTO;
import com.example.Tji_Teliman.dto.MotivationDTO;
import com.example.Tji_Teliman.dto.ProfilCandidatureDTO;
import com.example.Tji_Teliman.entites.Candidature;
import com.example.Tji_Teliman.services.CandidatureService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidatures")
public class CandidatureController {

    private final CandidatureService candidatureService;
    private final JwtUtils jwtUtils;

    public CandidatureController(CandidatureService candidatureService, JwtUtils jwtUtils) {
        this.candidatureService = candidatureService;
        this.jwtUtils = jwtUtils;
    }

    public record PostulerRequest(
        String motivationContenu
    ) {}

    public record ApiResponse(boolean success, String message, Object data) {}

    @PostMapping("/mission/{missionId}")
    public ResponseEntity<?> postuler(
            @PathVariable Long missionId,
            @RequestBody PostulerRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long jeuneId = jwtUtils.getUserIdFromToken(httpRequest);
            if (jeuneId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            Candidature candidature = candidatureService.postuler(jeuneId, missionId, request.motivationContenu());
            return ResponseEntity.ok(new ApiResponse(true, "Candidature soumise avec succès", candidatureService.toCandidatureDTO(candidature)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @GetMapping("/mes-candidatures")
    public ResponseEntity<?> getCandidaturesByJeune(HttpServletRequest httpRequest) {
        try {
            Long jeuneId = jwtUtils.getUserIdFromToken(httpRequest);
            if (jeuneId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            List<CandidatureDTO> candidatures = candidatureService.getCandidaturesByJeune(jeuneId);
            return ResponseEntity.ok(new ApiResponse(true, "Candidatures récupérées", candidatures));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<CandidatureDTO>> getCandidaturesByMission(@PathVariable Long missionId) {
        return ResponseEntity.ok(candidatureService.getCandidaturesByMission(missionId));
    }

    @GetMapping("/mission/{missionId}/motivations")
    public ResponseEntity<List<MotivationDTO>> getMotivationsByMission(@PathVariable Long missionId) {
        return ResponseEntity.ok(candidatureService.getMotivationsByMission(missionId));
    }

    @GetMapping("/acceptees")
    public ResponseEntity<?> getCandidaturesAcceptees() {
        List<CandidatureDTO> liste = candidatureService.getCandidaturesAcceptees();
        var data = new java.util.HashMap<String, Object>();
        data.put("nombre", liste.size());
        data.put("candidatures", liste);
        return ResponseEntity.ok(new ApiResponse(true, "Candidatures acceptées", data));
    }

    @GetMapping("/refusees")
    public ResponseEntity<?> getCandidaturesRefusees() {
        List<CandidatureDTO> liste = candidatureService.getCandidaturesRefusees();
        var data = new java.util.HashMap<String, Object>();
        data.put("nombre", liste.size());
        data.put("candidatures", liste);
        return ResponseEntity.ok(new ApiResponse(true, "Candidatures refusées", data));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStatsCandidatures() {
        var counts = candidatureService.getCountsAccepteesEtRefusees();
        return ResponseEntity.ok(new ApiResponse(true, "Statistiques candidatures", counts));
    }

    @PutMapping("/{candidatureId}/valider")
    public ResponseEntity<?> validerCandidature(
            @PathVariable Long candidatureId,
            HttpServletRequest httpRequest) {
        try {
            Long recruteurId = jwtUtils.getUserIdFromToken(httpRequest);
            if (recruteurId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            Candidature candidature = candidatureService.validerCandidature(candidatureId, recruteurId);
            return ResponseEntity.ok(new ApiResponse(true, "Candidature validée avec succès", candidatureService.toCandidatureDTO(candidature)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @PutMapping("/{candidatureId}/rejeter")
    public ResponseEntity<?> rejeterCandidature(
            @PathVariable Long candidatureId,
            HttpServletRequest httpRequest) {
        try {
            Long recruteurId = jwtUtils.getUserIdFromToken(httpRequest);
            if (recruteurId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            Candidature candidature = candidatureService.rejeterCandidature(candidatureId, recruteurId);
            return ResponseEntity.ok(new ApiResponse(true, "Candidature rejetée avec succès", candidatureService.toCandidatureDTO(candidature)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @GetMapping("/{candidatureId}/profil")
    public ResponseEntity<?> getProfilCandidature(@PathVariable Long candidatureId) {
        try {
            ProfilCandidatureDTO profil = candidatureService.getProfilCandidature(candidatureId);
            return ResponseEntity.ok(new ApiResponse(true, "Profil de candidature récupéré", profil));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }
}
