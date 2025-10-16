package com.example.Tji_Teliman.controllers;


import com.example.Tji_Teliman.dto.*;
        import com.example.Tji_Teliman.services.LitigeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/litiges")
public class LitigeController {

    @Autowired
    private LitigeService litigeService;

    //  Créer un litige
    @PostMapping
    public ResponseEntity<LitigeDTO> creerLitige(@RequestBody CreationLitigeDTO dto) {
        LitigeDTO litige = litigeService.creerLitige(dto);
        return ResponseEntity.ok(litige);
    }

    // - Tous les litiges
    @GetMapping
    public ResponseEntity<List<LitigeDTO>> getTousLesLitiges() {
        List<LitigeDTO> litiges = litigeService.getTousLesLitiges();
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
            @RequestBody AssignationLitigeDTO dto) {
        LitigeDTO litige = litigeService.assignerLitige(id, dto.getAdministrateurId());
        return ResponseEntity.ok(litige);
    }

    //  Résoudre un litige
    @PutMapping("/{id}/resoudre")
    public ResponseEntity<LitigeDTO> resoudreLitige(
            @PathVariable Long id,
            @RequestBody ResolutionLitigeDTO dto) {
        LitigeDTO litige = litigeService.resoudreLitige(id, dto);
        return ResponseEntity.ok(litige);
    }

    //  Litiges d'un jeune prestataire
    @GetMapping("/jeune/{jeuneId}")
    public ResponseEntity<List<LitigeDTO>> getLitigesParJeune(@PathVariable Long jeuneId) {
        List<LitigeDTO> litiges = litigeService.getLitigesParJeunePrestateur(jeuneId);
        return ResponseEntity.ok(litiges);
    }

    // Litiges d'un recruteur
    @GetMapping("/recruteur/{recruteurId}")
    public ResponseEntity<List<LitigeDTO>> getLitigesParRecruteur(@PathVariable Long recruteurId) {
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
    @GetMapping("/administrateur/{adminId}")
    public ResponseEntity<List<LitigeDTO>> getLitigesParAdministrateur(@PathVariable Long adminId) {
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