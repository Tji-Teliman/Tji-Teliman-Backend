package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.dto.*;
import com.example.Tji_Teliman.services.LitigeService;
import com.example.Tji_Teliman.config.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/litiges")
public class LitigeController {

    @Autowired
    private LitigeService litigeService;

    @Autowired
    private JwtUtils jwtUtils;

    //  Créer un litige - JSON (missionId dans le corps)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LitigeDTO> creerLitige(@RequestBody CreationLitigeDTO dto, HttpServletRequest request) {
        Long userId = jwtUtils.getUserIdFromToken(request);
        if (userId == null) {
            return ResponseEntity.badRequest().body(null);
        }
        if (dto.getMissionId() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        // Les IDs jeune/recruteur seront déduits depuis la mission côté service
        try {
            LitigeDTO litige = litigeService.creerLitige(dto);
            return ResponseEntity.ok(litige);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    //  Créer un litige - Multipart (document facultatif)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LitigeDTO> creerLitigeMultipart(
            @RequestParam("type") String type,
            @RequestParam("description") String description,
            @RequestParam("missionId") Long missionId,
            @RequestParam(value = "document", required = false) MultipartFile document,
            HttpServletRequest request) {
        Long userId = jwtUtils.getUserIdFromToken(request);
        if (userId == null) {
            return ResponseEntity.badRequest().body(null);
        }
        if (missionId == null) {
            return ResponseEntity.badRequest().body(null);
        }
        CreationLitigeDTO dto = new CreationLitigeDTO();
        dto.setType(type);
        dto.setDescription(description);
        dto.setMissionId(missionId);
        // Les IDs jeune/recruteur seront déduits depuis la mission côté service

        try {
            LitigeDTO litige = litigeService.creerLitige(dto, document);
            return ResponseEntity.ok(litige);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // - Tous les litiges
    @GetMapping
    public ResponseEntity<List<LitigeDTO>> getTousLesLitiges() {
        List<LitigeDTO> litiges = litigeService.getTousLesLitiges();
        return ResponseEntity.ok(litiges);
    }

    // - Les litiges par id
    @GetMapping("/{id}")
    public ResponseEntity<List<LitigeDTO>> getLitigeById(@PathVariable long id) {
        List<LitigeDTO> litiges = litigeService.getLitigeById(id);
        return ResponseEntity.ok(litiges);
    }

    //  Litiges par statut
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<LitigeDTO>> getLitigesParStatut(@PathVariable String statut) {
        List<LitigeDTO> litiges = litigeService.getLitigesParStatut(statut);
        return ResponseEntity.ok(litiges);
    }

    //  Litiges non assignés
    @GetMapping("/non-assignes")
    public ResponseEntity<List<LitigeDTO>> getLitigesNonAssignes() {
        List<LitigeDTO> litiges = litigeService.getLitigesNonAssignes();
        return ResponseEntity.ok(litiges);
    }

    //  Assigner un litige à un administrateur
    @PutMapping("/{id}/assigner")
    public ResponseEntity<LitigeDTO> assignerLitige(
            @PathVariable Long id,
            @RequestBody(required = false) AssignationLitigeDTO dto,
            HttpServletRequest request) {
        Long adminId = jwtUtils.getUserIdFromToken(request);
        if (adminId == null) {
            return ResponseEntity.badRequest().build();
        }
        LitigeDTO litige = litigeService.assignerLitige(id, adminId);
        return ResponseEntity.ok(litige);
    }

    //  Résoudre un litige
    @PutMapping("/{id}/resoudre")
    public ResponseEntity<LitigeDTO> resoudreLitige(
            @PathVariable Long id,
            @RequestBody ResolutionLitigeDTO dto,
            HttpServletRequest request) {
        Long adminId = jwtUtils.getUserIdFromToken(request);
        if (adminId != null) {
            dto.setAdministrateurId(adminId);
        }
        LitigeDTO litige = litigeService.resoudreLitige(id, dto);
        return ResponseEntity.ok(litige);
    }

    //  Litiges d'un jeune prestataire
    @GetMapping("/jeune")
    public ResponseEntity<List<LitigeDTO>> getLitigesParJeune(HttpServletRequest request) {
        Long jeuneId = jwtUtils.getUserIdFromToken(request);
        if (jeuneId == null) {
            return ResponseEntity.badRequest().build();
        }
        List<LitigeDTO> litiges = litigeService.getLitigesParJeunePrestateur(jeuneId);
        return ResponseEntity.ok(litiges);
    }

    // Litiges d'un recruteur
    @GetMapping("/recruteur")
    public ResponseEntity<List<LitigeDTO>> getLitigesParRecruteur(HttpServletRequest request) {
        Long recruteurId = jwtUtils.getUserIdFromToken(request);
        if (recruteurId == null) {
            return ResponseEntity.badRequest().build();
        }
        List<LitigeDTO> litiges = litigeService.getLitigesParRecruteur(recruteurId);
        return ResponseEntity.ok(litiges);
    }

    //  Litiges d'une mission
    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<LitigeDTO>> getLitigesParMission(@PathVariable Long missionId) {
        List<LitigeDTO> litiges = litigeService.getLitigesParMission(missionId);
        return ResponseEntity.ok(litiges);
    }

    //  Litiges d'un administrateur
    @GetMapping("/administrateur")
    public ResponseEntity<List<LitigeDTO>> getLitigesParAdministrateur(HttpServletRequest request) {
        Long adminId = jwtUtils.getUserIdFromToken(request);
        if (adminId == null) {
            return ResponseEntity.badRequest().build();
        }
        List<LitigeDTO> litiges = litigeService.getLitigesParAdministrateur(adminId);
        return ResponseEntity.ok(litiges);
    }

    //  Statistiques
    @GetMapping("/statistiques")
    public ResponseEntity<StatistiquesLitigeDTO> getStatistiques() {
        StatistiquesLitigeDTO stats = litigeService.getStatistiques();
        return ResponseEntity.ok(stats);
    }
}