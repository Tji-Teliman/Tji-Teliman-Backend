package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.services.ProfileService;
import com.example.Tji_Teliman.services.GoogleMapsService;
import com.example.Tji_Teliman.dto.JeunePrestateurProfileDTO;
import com.example.Tji_Teliman.dto.UserProfileDTO;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;  

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final GoogleMapsService googleMapsService;

    public ProfileController(ProfileService profileService, GoogleMapsService googleMapsService) {
        this.profileService = profileService;
        this.googleMapsService = googleMapsService;
    }

    @PostMapping("/jeunes/{id}")
    public ResponseEntity<?> createOrUpdateJeune(
        @PathVariable Long id,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateNaissance,
        @RequestParam(required = false) MultipartFile photo,
        @RequestParam(required = false) MultipartFile carteIdentite,
        @RequestParam(required = false) List<String> competences,
        @RequestParam(required = false) Double latitude,
        @RequestParam(required = false) Double longitude,
        @RequestParam(required = false) String adresse,
        @RequestParam(required = false) String placeId
    ) throws IOException {
        var jeune = profileService.updateJeune(id, dateNaissance, photo, carteIdentite);
        if (competences != null && !competences.isEmpty()) {
            jeune = profileService.setCompetencesJeune(id, new java.util.ArrayList<>(competences));
        }
        // Mettre à jour la géolocalisation si fournie
        if (latitude != null || longitude != null || adresse != null || placeId != null) {
            jeune = profileService.updateJeuneLocation(id, latitude, longitude, adresse, placeId);
        }
        JeunePrestateurProfileDTO dto = profileService.toProfileDTO(jeune);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        try {
            UserProfileDTO dto = profileService.getUserProfile(id);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/recruteurs/particulier/{id}")
    public ResponseEntity<?> createOrUpdateRecruteurParticulier(
        @PathVariable Long id,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateNaissance,
        @RequestParam(required = false) String profession,
        @RequestParam(required = false) MultipartFile photo,
        @RequestParam(required = false) Double latitude,
        @RequestParam(required = false) Double longitude,
        @RequestParam(required = false) String adresse,
        @RequestParam(required = false) String placeId
    ) throws IOException {
        var r = profileService.updateRecruteurParticulier(id, dateNaissance, profession, photo);
        // Mettre à jour la géolocalisation si fournie
        if (latitude != null || longitude != null || adresse != null || placeId != null) {
            r = profileService.updateRecruteurLocation(id, latitude, longitude, adresse, placeId);
        }
        return ResponseEntity.ok(r);
    }

    @PostMapping("/recruteurs/entreprise/{id}")
    public ResponseEntity<?> createOrUpdateRecruteurEntreprise(
        @PathVariable Long id,
        @RequestParam(required = false) String nomEntreprise,
        @RequestParam(required = false) String secteurActivite,
        @RequestParam(required = false) String emailEntreprise,
        @RequestParam(required = false) String siteWeb,
        @RequestParam(required = false) MultipartFile photo,
        @RequestParam(required = false) Double latitude,
        @RequestParam(required = false) Double longitude,
        @RequestParam(required = false) String adresse,
        @RequestParam(required = false) String placeId
    ) throws IOException {
        var r = profileService.updateRecruteurEntreprise(id, nomEntreprise, secteurActivite, emailEntreprise, siteWeb, photo);
        // Mettre à jour la géolocalisation si fournie
        if (latitude != null || longitude != null || adresse != null || placeId != null) {
            r = profileService.updateRecruteurLocation(id, latitude, longitude, adresse, placeId);
        }
        return ResponseEntity.ok(r);
    }

    /**
     * Endpoint pour le géocodage inverse : convertir lat/lng en placeId et adresse
     * Utile quand le frontend a seulement les coordonnées du clic sur la carte pour les profils
     */
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


