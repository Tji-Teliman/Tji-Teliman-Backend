package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.services.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    public static class UserRegistrationRequest {
        public String nom;
        public String prenom;
        public String email; // facultatif
        public String motDePasse;
        public String telephone;
        public String role;   // JEUNE_PRESTATEUR | RECRUTEUR
        public String genre;  // MASCULIN | FEMININ
        public String typeRecruteur; // requis si role=RECRUTEUR: ENTREPRISE | PARTICULIER
    }

    public record ApiResponse(boolean success, String message, Object data) {}

    @PostMapping("/inscription")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest req) {
        try {
            Object created = utilisateurService.register(req);
            return ResponseEntity.ok(new ApiResponse(true, "Inscription reussie", created));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "Inscription non reussie: " + ex.getMessage(), null));
        }
    }

    public static class LoginRequest {
        public String telephone;
        public String motDePasse;
    }

    @PostMapping("/connexion")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            Object auth = utilisateurService.authenticate(req.telephone, req.motDePasse);
            return ResponseEntity.ok(new ApiResponse(true, "Connexion reussie", auth));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "Connexion non reussie: " + ex.getMessage(), null));
        }
    }
}
