package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.services.ProfileService;
import com.example.Tji_Teliman.dto.JeunePrestateurProfileDTO;
import com.example.Tji_Teliman.dto.UserProfileDTO;
import java.util.List;
import java.io.IOException;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/jeunes/{id}")
    public ResponseEntity<?> createOrUpdateJeune(
        @PathVariable Long id,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateNaissance,
        @RequestParam(required = false) String localisation,
        @RequestParam(required = false) MultipartFile photo,
        @RequestParam(required = false) MultipartFile carteIdentite,
        @RequestParam(required = false) List<String> competences
    ) throws IOException {
        var jeune = profileService.updateJeune(id, dateNaissance, localisation, photo, carteIdentite);
        if (competences != null && !competences.isEmpty()) {
            jeune = profileService.setCompetencesJeune(id, new java.util.ArrayList<>(competences));
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
        @RequestParam(required = false) String adresse,
        @RequestParam(required = false) MultipartFile photo
    ) throws IOException {
        var r = profileService.updateRecruteurParticulier(id, dateNaissance, profession, adresse, photo);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/recruteurs/entreprise/{id}")
    public ResponseEntity<?> createOrUpdateRecruteurEntreprise(
        @PathVariable Long id,
        @RequestParam(required = false) String nomEntreprise,
        @RequestParam(required = false) String secteurActivite,
        @RequestParam(required = false) String emailEntreprise,
        @RequestParam(required = false) String siteWeb,
        @RequestParam(required = false) MultipartFile photo
    ) throws IOException {
        var r = profileService.updateRecruteurEntreprise(id, nomEntreprise, secteurActivite, emailEntreprise, siteWeb, photo);
        return ResponseEntity.ok(r);
    }
}


