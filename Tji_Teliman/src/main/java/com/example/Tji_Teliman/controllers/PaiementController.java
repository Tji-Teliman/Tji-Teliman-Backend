package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.config.JwtUtils;
import com.example.Tji_Teliman.entites.Paiement;
import com.example.Tji_Teliman.services.PaiementService;
import com.example.Tji_Teliman.dto.PaiementDTO;
import com.example.Tji_Teliman.entites.enums.StatutPaiement;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService paiementService;
    private final JwtUtils jwtUtils;

    public PaiementController(PaiementService paiementService, JwtUtils jwtUtils) {
        this.paiementService = paiementService;
        this.jwtUtils = jwtUtils;
    }

    public record EffectuerPaiementRequest(Double montant, String telephone) {}
    public record ApiResponse(boolean success, String message, Object data) {}

    // Effectuer un paiement pour une candidature (recruteur connecté)
    @PostMapping("/candidature/{candidatureId}")
    public ResponseEntity<?> effectuerPaiement(@PathVariable Long candidatureId, @RequestBody EffectuerPaiementRequest req, HttpServletRequest httpRequest) {
        try {
            Long recruteurId = jwtUtils.getUserIdFromToken(httpRequest);
            if (recruteurId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            Paiement p = paiementService.effectuerPaiement(candidatureId, req.montant(), req.telephone());
            PaiementDTO dto = paiementService.toDTO(p);
            return ResponseEntity.ok(new ApiResponse(true, "Paiement effectué", dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    // Lister tous les paiements
    @GetMapping("/all")
    public ResponseEntity<List<PaiementDTO>> getAllPaiements() {
        List<PaiementDTO> paiements = paiementService.getAllPaiements().stream()
                .map(paiementService::toDTO)
                .toList();
        return ResponseEntity.ok(paiements);
    }

    // Obtenir un paiement par son ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaiementById(@PathVariable Long id) {
        Optional<Paiement> paiement = paiementService.getPaiementById(id);
        if (paiement.isPresent()) {
            PaiementDTO dto = paiementService.toDTO(paiement.get());
            return ResponseEntity.ok(new ApiResponse(true, "Paiement trouvé", dto));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Paiement introuvable", null));
        }
    }

    // Lister les paiements du recruteur connecté
    @GetMapping("/mes-paiements-recruteur")
    public ResponseEntity<?> getPaiementsByRecruteur(HttpServletRequest httpRequest) {
        try {
            Long recruteurId = jwtUtils.getUserIdFromToken(httpRequest);
            if (recruteurId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            List<PaiementDTO> paiements = paiementService.getPaiementsByRecruteur(recruteurId).stream()
                    .map(paiementService::toDTO)
                    .toList();
            return ResponseEntity.ok(new ApiResponse(true, "Mes paiements", paiements));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    // Lister les paiements du jeune prestataire connecté
    @GetMapping("/mes-paiements-jeune")
    public ResponseEntity<?> getPaiementsByJeunePrestateur(HttpServletRequest httpRequest) {
        try {
            Long jeuneId = jwtUtils.getUserIdFromToken(httpRequest);
            if (jeuneId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            List<PaiementDTO> paiements = paiementService.getPaiementsByJeunePrestateur(jeuneId).stream()
                    .map(paiementService::toDTO)
                    .toList();
            return ResponseEntity.ok(new ApiResponse(true, "Mes paiements", paiements));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    // Obtenir le paiement lié à une candidature
    @GetMapping("/candidature/{candidatureId}")
    public ResponseEntity<?> getPaiementByCandidature(@PathVariable Long candidatureId) {
        Optional<Paiement> paiement = paiementService.getPaiementByCandidature(candidatureId);
        if (paiement.isPresent()) {
            PaiementDTO dto = paiementService.toDTO(paiement.get());
            return ResponseEntity.ok(new ApiResponse(true, "Paiement trouvé", dto));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Aucun paiement trouvé pour cette candidature", null));
        }
    }

    // Lister les paiements par statut
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<PaiementDTO>> getPaiementsByStatut(@PathVariable String statut) {
        try {
            StatutPaiement statutPaiement = StatutPaiement.valueOf(statut.toUpperCase());
            List<PaiementDTO> paiements = paiementService.getPaiementsByStatut(statutPaiement).stream()
                    .map(paiementService::toDTO)
                    .toList();
            return ResponseEntity.ok(paiements);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(List.of());
        }
    }
}
