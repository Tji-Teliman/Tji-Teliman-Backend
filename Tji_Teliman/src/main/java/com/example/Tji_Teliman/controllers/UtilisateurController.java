package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.config.JwtUtils;
import com.example.Tji_Teliman.services.UtilisateurService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final JwtUtils jwtUtils;

    public UtilisateurController(UtilisateurService utilisateurService, JwtUtils jwtUtils) {
        this.utilisateurService = utilisateurService;
        this.jwtUtils = jwtUtils;
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

    // Nouveaux modèles d'inscription dédiés (sans champ role)
    public static class JeuneRegistrationRequest {
        public String nom;
        public String prenom;
        public String email; // facultatif
        public String motDePasse;
        public String confirmationMotDePasse; // pour validation côté backend
        public String telephone;
        public String genre;  // MASCULIN | FEMININ
    }

    public static class RecruteurRegistrationRequest {
        public String nom;
        public String prenom;
        public String email; // facultatif
        public String motDePasse;
        public String confirmationMotDePasse; // pour validation côté backend
        public String telephone;
        public String genre;  // MASCULIN | FEMININ
        public String typeRecruteur; // ENTREPRISE | PARTICULIER
    }

    public record ApiResponse(boolean success, String message, Object data) {}

    // Inscription utilisateur avec validations (téléphone, email gmail, mot de passe fort)
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

    // Nouvelle inscription JEUNE (role implicite)
    @PostMapping("/register/jeune")
    public ResponseEntity<?> registerJeune(@RequestBody JeuneRegistrationRequest req) {
        try {
            Object created = utilisateurService.registerJeune(req);
            return ResponseEntity.ok(new ApiResponse(true, "Inscription reussie", created));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "Inscription non reussie: " + ex.getMessage(), null));
        }
    }

    // Nouvelle inscription RECRUTEUR (role implicite + typeRecruteur requis)
    @PostMapping("/register/recruteur")
    public ResponseEntity<?> registerRecruteur(@RequestBody RecruteurRegistrationRequest req) {
        try {
            Object created = utilisateurService.registerRecruteur(req);
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

    // Connexion standard via téléphone et mot de passe
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

    public static class AdminLoginRequest {
        public String email;
        public String motDePasse;
    }

    // Connexion administrateur via email et mot de passe (réservé aux ADMINISTRATEUR)
    @PostMapping("/connexion-admin")
    public ResponseEntity<?> adminLogin(@RequestBody AdminLoginRequest req) {
        try {
            Object auth = utilisateurService.authenticateAdminByEmail(req.email, req.motDePasse);
            return ResponseEntity.ok(new ApiResponse(true, "Connexion administrateur reussie", auth));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "Connexion administrateur non reussie: " + ex.getMessage(), null));
        }
    }

    public record ChangePasswordRequest(
        String motDePasseActuel,
        String nouveauMotDePasse,
        String confirmationMotDePasse
    ) {}

    // Changer mon mot de passe (utilisateur connecté via token)
    @PostMapping("/change-password")
    public ResponseEntity<?> changeMyPassword(HttpServletRequest httpRequest, @RequestBody ChangePasswordRequest request) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(httpRequest);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            utilisateurService.changePassword(userId, request.motDePasseActuel(), request.nouveauMotDePasse(), request.confirmationMotDePasse());
            return ResponseEntity.ok(new ApiResponse(true, "Mot de passe modifié avec succès", null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "Erreur lors du changement de mot de passe: " + ex.getMessage(), null));
        }
    }
}
