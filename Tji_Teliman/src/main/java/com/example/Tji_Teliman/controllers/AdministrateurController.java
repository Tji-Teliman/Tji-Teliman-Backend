package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.services.AdministrateurService;
import com.example.Tji_Teliman.entites.enums.StatutUtilisateur;
import com.example.Tji_Teliman.config.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdministrateurController {

    private final AdministrateurService administrateurService;
    private final JwtUtils jwtUtils;

    public AdministrateurController(AdministrateurService administrateurService, JwtUtils jwtUtils) {
        this.administrateurService = administrateurService;
        this.jwtUtils = jwtUtils;
    }

    // Lister les utilisateurs avec statistiques (admin connecté)
    @GetMapping("/utilisateurs")
    public ResponseEntity<?> listUsersWithStats(HttpServletRequest httpRequest) {
        Long adminId = jwtUtils.getUserIdFromToken(httpRequest);
        if (adminId == null || !jwtUtils.isAdmin(httpRequest)) {
            return ResponseEntity.status(403).body("Accès refusé: administrateur requis");
        }
        var payload = administrateurService.listUsersWithStats(adminId);
        return ResponseEntity.ok(payload);
    }

    // Obtenir les statistiques système (admin connecté)
    @GetMapping("/stats")
    public ResponseEntity<?> getSystemStats(HttpServletRequest httpRequest) {
        Long adminId = jwtUtils.getUserIdFromToken(httpRequest);
        System.out.println("L'id de l'utilisateur est : "+httpRequest);
        if (adminId == null || !jwtUtils.isAdmin(httpRequest)) {
            return ResponseEntity.status(403).body("Accès refusé: administrateur requis");
        }
        var stats = administrateurService.getSystemStats(adminId);
        return ResponseEntity.ok(stats);
    }
//    public ResponseEntity<?> getSystemStats(@RequestParam Long adminId) {
//        Long adminId = jwtUtils.getUserIdFromToken(httpRequest);
//        System.out.println("L'id de l'utilisateur est : "+httpRequest);
//        if (adminId == null || !jwtUtils.isAdmin(httpRequest)) {
//            return ResponseEntity.status(403).body("Accès refusé: administrateur requis");
//        }
//        var stats = administrateurService.getSystemStats(adminId);
//        return ResponseEntity.ok(stats);
//    }

    // Lister les missions avec statistiques (admin connecté)
    @GetMapping("/missions")
    public ResponseEntity<?> listMissionsWithStats(HttpServletRequest httpRequest) {
        Long adminId = jwtUtils.getUserIdFromToken(httpRequest);
        if (adminId == null || !jwtUtils.isAdmin(httpRequest)) {
            return ResponseEntity.status(403).body("Accès refusé: administrateur requis");
        }
        var payload = administrateurService.listMissionsWithStats();
        return ResponseEntity.ok(payload);
    }

    // Lister les paiements avec statistiques (admin connecté)
    @GetMapping("/paiements")
    public ResponseEntity<?> listPaiementsWithStats(HttpServletRequest httpRequest) {
        Long adminId = jwtUtils.getUserIdFromToken(httpRequest);
        if (adminId == null || !jwtUtils.isAdmin(httpRequest)) {
            return ResponseEntity.status(403).body("Accès refusé: administrateur requis");
        }
        var payload = administrateurService.listPaiementsWithStats();
        return ResponseEntity.ok(payload);
    }

    // Bloquer un utilisateur (admin connecté)
    @PostMapping("/utilisateurs/{userId}/bloquer")
    public ResponseEntity<?> blockUser(HttpServletRequest httpRequest, @PathVariable Long userId) {
        Long adminId = jwtUtils.getUserIdFromToken(httpRequest);
        if (adminId == null || !jwtUtils.isAdmin(httpRequest)) {
            return ResponseEntity.status(403).body("Accès refusé: administrateur requis");
        }
        administrateurService.setUserStatut(userId, StatutUtilisateur.DESACTIVER);
        return ResponseEntity.ok("Utilisateur bloquer avec succes");
    }

    // Débloquer un utilisateur (admin connecté)
    @PostMapping("/utilisateurs/{userId}/debloquer")
    public ResponseEntity<?> unblockUser(HttpServletRequest httpRequest, @PathVariable Long userId) {
        Long adminId = jwtUtils.getUserIdFromToken(httpRequest);
        if (adminId == null || !jwtUtils.isAdmin(httpRequest)) {
            return ResponseEntity.status(403).body("Accès refusé: administrateur requis");
        }
        administrateurService.setUserStatut(userId, StatutUtilisateur.ACTIVER);
        return ResponseEntity.ok("Utilisateur debloquer avec succes");
    }
}
