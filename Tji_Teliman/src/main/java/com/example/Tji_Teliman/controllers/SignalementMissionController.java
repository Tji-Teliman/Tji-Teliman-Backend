package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.config.JwtUtils;
import com.example.Tji_Teliman.dto.SignalementMissionDTO;
import com.example.Tji_Teliman.entites.enums.TypeSignalement;
import com.example.Tji_Teliman.services.SignalementMissionService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/signalements")
public class SignalementMissionController {

    private final SignalementMissionService signalementService;
    private final JwtUtils jwtUtils;

    public SignalementMissionController(SignalementMissionService signalementService, JwtUtils jwtUtils) {
        this.signalementService = signalementService;
        this.jwtUtils = jwtUtils;
    }

    public record ApiResponse(boolean success, String message, Object data) {}

    public record SignalerRequest(TypeSignalement type, String description) {}

    // Signaler une mission (jeune connecté) en précisant un type et une description
    @PostMapping(value = "/missions/{missionId}")
    public ResponseEntity<?> signalerMission(
        @PathVariable Long missionId,
        HttpServletRequest httpRequest,
        @org.springframework.web.bind.annotation.RequestBody SignalerRequest request
    ) {
        try {
            Long jeuneId = jwtUtils.getUserIdFromToken(httpRequest);
            if (jeuneId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            if (request == null || request.type == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Type de signalement requis", null));
            }
            var saved = signalementService.create(missionId, jeuneId, request.type, request.description);
            // Retourner un DTO léger pour éviter la sérialisation profonde de l'entité
            SignalementMissionDTO dto = signalementService.toDTO(saved);
            return ResponseEntity.ok(new ApiResponse(true, "Signalement enregistré", dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    // Endpoint admin: lister tous les signalements
    @GetMapping("/admin")
    public ResponseEntity<List<SignalementMissionDTO>> listAll() {
        var list = signalementService.listAll().stream().map(signalementService::toDTO).toList();
        return ResponseEntity.ok(list);
    }

    // Suppression de l'endpoint de mise à jour de statut (non géré pour l'instant)
}


