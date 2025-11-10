package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.config.SecurityUtils;
import com.example.Tji_Teliman.dto.*;
import com.example.Tji_Teliman.services.LitigeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/litiges")
public class LitigeController {

    @Autowired
    private LitigeService litigeService;

    @Autowired
    private SecurityUtils securityUtils;

    // Créer un litige
    @PostMapping
    public ResponseEntity<LitigeDTO> creerLitige(@RequestBody CreationLitigeDTO dto) {
        Long userId = securityUtils.getCurrentUserId();
        LitigeDTO litige = litigeService.creerLitige(dto, userId);
        return ResponseEntity.ok(litige);
    }

    // Tous les litiges (pour l'utilisateur connecté)
    @GetMapping
    public ResponseEntity<List<LitigeDTO>> getTousLesLitiges() {
        Long userId = securityUtils.getCurrentUserId();
        List<LitigeDTO> litiges = litigeService.getTousLesLitiges(userId);
        return ResponseEntity.ok(litiges);
    }

    // Litiges par statut (pour l'utilisateur connecté)
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<LitigeDTO>> getLitigesParStatut(@PathVariable String statut) {
        Long userId = securityUtils.getCurrentUserId();
        List<LitigeDTO> litiges = litigeService.getLitigesParStatut(statut, userId);
        return ResponseEntity.ok(litiges);
    }

    // Litiges non assignés (admin seulement)
    @GetMapping("/non-assignes")
    public ResponseEntity<List<LitigeDTO>> getLitigesNonAssignes() {
        String role = securityUtils.getCurrentUserRole();
        if (!role.contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<LitigeDTO> litiges = litigeService.getLitigesNonAssignes();
        return ResponseEntity.ok(litiges);
    }

    // Assigner un litige à l'admin connecté
    @PutMapping("/{id}/assigner")
    public ResponseEntity<LitigeDTO> assignerLitige(@PathVariable Long id) {
        String role = securityUtils.getCurrentUserRole();
        if (!role.contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long adminId = securityUtils.getCurrentUserId();
        LitigeDTO litige = litigeService.assignerLitige(id, adminId);
        return ResponseEntity.ok(litige);
    }

    // Résoudre un litige
    @PutMapping("/{id}/resoudre")
    public ResponseEntity<LitigeDTO> resoudreLitige(
            @PathVariable Long id,
            @RequestBody ResolutionLitigeDTO dto) {
        String role = securityUtils.getCurrentUserRole();
        if (!role.contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long adminId = securityUtils.getCurrentUserId();
        LitigeDTO litige = litigeService.resoudreLitige(id, dto, adminId);
        return ResponseEntity.ok(litige);
    }

    // NOUVEAU : Mes litiges (utilise le token)
    @GetMapping("/mes-litiges")
    public ResponseEntity<List<LitigeDTO>> getMesLitiges() {
        Long userId = securityUtils.getCurrentUserId();
        List<LitigeDTO> litiges = litigeService.getLitigesParUtilisateur(userId);
        return ResponseEntity.ok(litiges);
    }

    // Litiges d'une mission (avec vérification d'accès)
    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<LitigeDTO>> getLitigesParMission(@PathVariable Long missionId) {
        Long userId = securityUtils.getCurrentUserId();
        List<LitigeDTO> litiges = litigeService.getLitigesParMissionPourUtilisateur(missionId, userId);
        return ResponseEntity.ok(litiges);
    }

    // Statistiques (pour l'utilisateur connecté)
    @GetMapping("/statistiques")
    public ResponseEntity<StatistiquesLitigeDTO> getStatistiques() {
        Long userId = securityUtils.getCurrentUserId();
        StatistiquesLitigeDTO stats = litigeService.getStatistiques(userId);
        return ResponseEntity.ok(stats);
    }
}