package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.config.JwtUtils;
import com.example.Tji_Teliman.services.ProfileService;
import com.example.Tji_Teliman.services.GoogleMapsService;
import com.example.Tji_Teliman.dto.JeunePrestateurProfileDTO;
import com.example.Tji_Teliman.dto.UserProfileDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.io.IOException;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;  

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final GoogleMapsService googleMapsService;
    private final JwtUtils jwtUtils;

    public ProfileController(ProfileService profileService, GoogleMapsService googleMapsService, JwtUtils jwtUtils) {
        this.profileService = profileService;
        this.googleMapsService = googleMapsService;
        this.jwtUtils = jwtUtils;
    }

    // Créer ou mettre à jour mon profil (jeune ou recruteur)
    @PostMapping("/mon-profil")
    public ResponseEntity<?> createOrUpdateMyProfile(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateNaissance,
        @RequestParam(required = false) MultipartFile photo,
        @RequestParam(required = false) MultipartFile carteIdentite,
        @RequestParam(required = false) List<String> competences,
        @RequestParam(required = false) String profession,
        @RequestParam(required = false) String typeRecruteur,
        @RequestParam(required = false) String nomEntreprise,
        @RequestParam(required = false) String secteurActivite,
        @RequestParam(required = false) String emailEntreprise,
        @RequestParam(required = false) String siteWeb,
        @RequestParam(required = false) Double latitude,
        @RequestParam(required = false) Double longitude,
        @RequestParam(required = false) String adresse,
        @RequestParam(required = false) String placeId,
        HttpServletRequest httpRequest
    ) throws IOException {
        try {
            Long userId = jwtUtils.getUserIdFromToken(httpRequest);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            
            // Logs de débogage pour tous les paramètres
            System.out.println("DEBUG - Tous les paramètres reçus:");
            System.out.println("typeRecruteur: '" + typeRecruteur + "'");
            System.out.println("nomEntreprise: '" + nomEntreprise + "'");
            System.out.println("secteurActivite: '" + secteurActivite + "'");
            System.out.println("emailEntreprise: '" + emailEntreprise + "'");
            System.out.println("siteWeb: '" + siteWeb + "'");
            System.out.println("profession: '" + profession + "'");
            System.out.println("dateNaissance: " + dateNaissance);
            
            // Déterminer le type d'utilisateur et mettre à jour le profil approprié
            UserProfileDTO userProfile = profileService.getUserProfile(userId);
            
            if (userProfile.getRole().equals("JEUNE_PRESTATEUR")) {
                // Mise à jour du profil jeune
                var jeune = profileService.updateJeune(userId, dateNaissance, photo, carteIdentite);
                if (competences != null && !competences.isEmpty()) {
                    jeune = profileService.setCompetencesJeune(userId, new java.util.ArrayList<>(competences));
                }
                // Mettre à jour la géolocalisation si fournie
                if (latitude != null || longitude != null || adresse != null || placeId != null) {
                    jeune = profileService.updateJeuneLocation(userId, latitude, longitude, adresse, placeId);
                }
                JeunePrestateurProfileDTO dto = profileService.toProfileDTO(jeune);
                return ResponseEntity.ok(new ApiResponse(true, "Profil jeune mis à jour", dto));
                
            } else if (userProfile.getRole().equals("RECRUTEUR")) {
                // Déterminer le type de recruteur
                System.out.println("DEBUG - typeRecruteur reçu: " + typeRecruteur);
                System.out.println("DEBUG - Comparaison avec ENTREPRISE: " + (typeRecruteur != null && typeRecruteur.equalsIgnoreCase("ENTREPRISE")));
                
                // Logique de détection du type de recruteur
                boolean isEntreprise = false;
                
                // Méthode 1: Vérifier le paramètre typeRecruteur
                if (typeRecruteur != null && typeRecruteur.equalsIgnoreCase("ENTREPRISE")) {
                    isEntreprise = true;
                    System.out.println("DEBUG - Type détecté via paramètre: ENTREPRISE");
                }
                // Méthode 2: Fallback - vérifier si des champs entreprise sont fournis
                else if (nomEntreprise != null || secteurActivite != null || emailEntreprise != null || siteWeb != null) {
                    isEntreprise = true;
                    System.out.println("DEBUG - Type détecté via champs entreprise: ENTREPRISE");
                }
                
                if (isEntreprise) {
                    // Recruteur entreprise
                    System.out.println("DEBUG - Création profil entreprise:");
                    System.out.println("nomEntreprise: " + nomEntreprise);
                    System.out.println("secteurActivite: " + secteurActivite);
                    System.out.println("emailEntreprise: " + emailEntreprise);
                    System.out.println("siteWeb: " + siteWeb);
                    var recruteur = profileService.updateRecruteurEntrepriseWithTypeAndLocation(userId, nomEntreprise, secteurActivite, emailEntreprise, siteWeb, photo, carteIdentite, latitude, longitude, adresse, placeId);
                    return ResponseEntity.ok(new ApiResponse(true, "Profil recruteur entreprise mis à jour", recruteur));
                } else {
                    // Recruteur particulier (par défaut)
                    System.out.println("DEBUG - Création profil particulier:");
                    System.out.println("dateNaissance: " + dateNaissance);
                    System.out.println("profession: " + profession);
                    System.out.println("DEBUG - Type détecté: PARTICULIER");
                    var recruteur = profileService.updateRecruteurParticulierWithTypeAndLocation(userId, dateNaissance, profession, photo, carteIdentite, latitude, longitude, adresse, placeId);
                    return ResponseEntity.ok(new ApiResponse(true, "Profil recruteur particulier mis à jour", recruteur));
                }
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Type d'utilisateur non supporté", null));
            }
            
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    // Récupérer mon profil
    @GetMapping("/mon-profil")
    public ResponseEntity<?> getMyProfile(HttpServletRequest httpRequest) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(httpRequest);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            UserProfileDTO dto = profileService.getUserProfile(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Profil récupéré", dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }


    /**
     * Endpoint pour le géocodage inverse : convertir lat/lng en placeId et adresse
     * Utile quand le frontend a seulement les coordonnées du clic sur la carte pour les profils
     */
    // Géocodage inverse pour profils (lat/lng -> placeId et adresse)
    @PostMapping("/reverse-geocode")
    public ResponseEntity<?> reverseGeocode(@RequestBody ReverseGeocodeRequest req) {
        try {
            var result = googleMapsService.reverseGeocode(req.latitude(), req.longitude());
            if (result != null) {
                return ResponseEntity.ok(new ApiResponse(true, "Géocodage inverse réussi", result));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Impossible de géocoder cette position", null));
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Erreur lors du géocodage inverse: " + ex.getMessage(), null));
        }
    }

    public record ReverseGeocodeRequest(Double latitude, Double longitude) {}
    public record ApiResponse(boolean success, String message, Object data) {}
}


