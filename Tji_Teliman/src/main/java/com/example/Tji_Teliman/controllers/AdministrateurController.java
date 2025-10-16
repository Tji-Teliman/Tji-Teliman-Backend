package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.dto.AdminUsersWithStatsResponse;
import com.example.Tji_Teliman.services.AdministrateurService;
import com.example.Tji_Teliman.dto.SystemStatsDTO;
import com.example.Tji_Teliman.dto.AdminMissionsWithStatsResponse;
import com.example.Tji_Teliman.dto.AdminPaiementsWithStatsResponse;
import com.example.Tji_Teliman.entites.enums.StatutUtilisateur;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdministrateurController {

    private final AdministrateurService administrateurService;

    public AdministrateurController(AdministrateurService administrateurService) {
        this.administrateurService = administrateurService;
    }

    @GetMapping("/{adminId}/utilisateurs")
    public ResponseEntity<AdminUsersWithStatsResponse> listUsersWithStats(@PathVariable Long adminId) {
        var payload = administrateurService.listUsersWithStats(adminId);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/{adminId}/stats")
    public ResponseEntity<SystemStatsDTO> getSystemStats(@PathVariable Long adminId) {
        var stats = administrateurService.getSystemStats(adminId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{adminId}/missions")
    public ResponseEntity<AdminMissionsWithStatsResponse> listMissionsWithStats(@PathVariable Long adminId) {
        var payload = administrateurService.listMissionsWithStats();
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/{adminId}/paiements")
    public ResponseEntity<AdminPaiementsWithStatsResponse> listPaiementsWithStats(@PathVariable Long adminId) {
        var payload = administrateurService.listPaiementsWithStats();
        return ResponseEntity.ok(payload);
    }

    @PostMapping("/{adminId}/utilisateurs/{userId}/bloquer")
    public ResponseEntity<?> blockUser(@PathVariable Long adminId, @PathVariable Long userId) {
        administrateurService.setUserStatut(userId, StatutUtilisateur.DESACTIVER);
        return ResponseEntity.ok("Utilisateur bloquer avec succes");
    }

    @PostMapping("/{adminId}/utilisateurs/{userId}/debloquer")
    public ResponseEntity<?> unblockUser(@PathVariable Long adminId, @PathVariable Long userId) {
        administrateurService.setUserStatut(userId, StatutUtilisateur.ACTIVER);
        return ResponseEntity.ok("Utilisateur debloquer avec succes");
    }
}
