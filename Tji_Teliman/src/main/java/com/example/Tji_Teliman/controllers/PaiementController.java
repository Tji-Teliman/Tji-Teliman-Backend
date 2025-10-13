package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.entites.Paiement;
import com.example.Tji_Teliman.services.PaiementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
            return ResponseEntity.ok(new ApiResponse(true, "Paiement effectué", p));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }
}
