package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.config.JwtService;
import com.example.Tji_Teliman.dto.SignalementMissionDTO;
import com.example.Tji_Teliman.entites.enums.StatutSignalement;
import com.example.Tji_Teliman.services.SignalementMissionService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/signalements")
public class SignalementMissionController {

    private final SignalementMissionService signalementService;
    private final JwtService jwtService;

    public SignalementMissionController(SignalementMissionService signalementService, JwtService jwtService) {
        this.signalementService = signalementService;
        this.jwtService = jwtService;
    }

    public record ApiResponse(boolean success, String message, Object data) {}

    // Endpoint côté jeune pour signaler une mission
    @PostMapping("/missions/{missionId}")
    public ResponseEntity<?> signalerMission(
        @PathVariable Long missionId,
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @RequestParam String motif,
        @RequestParam(required = false) String description,
        @RequestParam(required = false) String pieceJointe
    ) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant", null));
            }
            String token = authorization.substring(7);
            Long jeuneId = jwtService.parseUserId(token);
            if (jeuneId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token invalide", null));
            }
            var saved = signalementService.create(missionId, jeuneId, motif, description, pieceJointe);
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

    // Endpoint admin: changer le statut d'un signalement
    @PostMapping("/admin/{id}/statut")
    public ResponseEntity<?> updateStatut(@PathVariable Long id, @RequestParam StatutSignalement statut) {
        try {
            var updated = signalementService.updateStatut(id, statut);
            return ResponseEntity.ok(new ApiResponse(true, "Statut mis à jour", signalementService.toDTO(updated)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }
}


