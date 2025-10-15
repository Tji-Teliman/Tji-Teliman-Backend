package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.entites.Paiement;
import com.example.Tji_Teliman.services.PaiementService;
import com.example.Tji_Teliman.dto.PaiementDTO;
import com.example.Tji_Teliman.entites.enums.StatutPaiement;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService paiementService;

    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    public record EffectuerPaiementRequest(Double montant) {}
    public record ApiResponse(boolean success, String message, Object data) {}

    @PostMapping("/candidature/{candidatureId}")
    public ResponseEntity<?> effectuerPaiement(@PathVariable Long candidatureId, @RequestBody EffectuerPaiementRequest req) {
        try {
            Paiement p = paiementService.effectuerPaiement(candidatureId, req.montant());
            PaiementDTO dto = paiementService.toDTO(p);
            return ResponseEntity.ok(new ApiResponse(true, "Paiement effectué", dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<PaiementDTO>> getAllPaiements() {
        List<PaiementDTO> paiements = paiementService.getAllPaiements().stream()
                .map(paiementService::toDTO)
                .toList();
        return ResponseEntity.ok(paiements);
    }

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

    @GetMapping("/recruteur/{recruteurId}")
    public ResponseEntity<List<PaiementDTO>> getPaiementsByRecruteur(@PathVariable Long recruteurId) {
        List<PaiementDTO> paiements = paiementService.getPaiementsByRecruteur(recruteurId).stream()
                .map(paiementService::toDTO)
                .toList();
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/jeune/{jeuneId}")
    public ResponseEntity<List<PaiementDTO>> getPaiementsByJeunePrestateur(@PathVariable Long jeuneId) {
        List<PaiementDTO> paiements = paiementService.getPaiementsByJeunePrestateur(jeuneId).stream()
                .map(paiementService::toDTO)
                .toList();
        return ResponseEntity.ok(paiements);
    }

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
