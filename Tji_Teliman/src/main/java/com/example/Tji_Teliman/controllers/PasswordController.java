package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.services.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
public class PasswordController {

    private final UtilisateurService utilisateurService;

    public PasswordController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/change/{userId}")
    public ResponseEntity<?> changePassword(@PathVariable Long userId, @RequestBody ChangePasswordRequest request) {
        try {
            utilisateurService.changePassword(userId, request.motDePasseActuel(), request.nouveauMotDePasse(), request.confirmationMotDePasse());
            return ResponseEntity.ok(new ApiResponse(true, "Mot de passe modifié avec succès", null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "Erreur lors du changement de mot de passe: " + ex.getMessage(), null));
        }
    }

    public record ChangePasswordRequest(
        String motDePasseActuel,
        String nouveauMotDePasse,
        String confirmationMotDePasse
    ) {}

    public record ApiResponse(boolean success, String message, Object data) {}
}
