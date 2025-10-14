package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.entites.Mission;
import com.example.Tji_Teliman.services.MissionService;
import com.example.Tji_Teliman.dto.MissionDTO;
import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/missions")
public class MissionController {

    private final MissionService missionService;

    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    public record CreateMissionRequest(
        String titre,
        String description,
        String exigence,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateDebut,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateFin,
        String localisation,
        Double remuneration,
        String categorieNom,
        String heureDebut,
        String heureFin
    ) {}

    public record ApiResponse(boolean success, String message, Object data) {}

    @PostMapping("/recruteur/{recruteurId}")
    public ResponseEntity<?> create(@PathVariable Long recruteurId, @RequestBody CreateMissionRequest req) {
        try {
            Mission m = missionService.create(recruteurId, req.titre(), req.description(), req.exigence(), req.dateDebut(), req.dateFin(), req.localisation(), req.remuneration(), req.categorieNom(), req.heureDebut(), req.heureFin());
            MissionDTO dto = missionService.toDTO(m);
            return ResponseEntity.ok(new ApiResponse(true, "Mission créée", dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<MissionDTO>> listAll() {
        return ResponseEntity.ok(missionService.listAll().stream().map(missionService::toDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "Mission", missionService.getById(id)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<List<MissionDTO>> listByRecruteur(@PathVariable Long recruteurId) {
        return ResponseEntity.ok(missionService.listByRecruteur(recruteurId).stream().map(missionService::toDTO).toList());
    }

    @GetMapping("/recruteur/{recruteurId}/stats")
    public ResponseEntity<?> getMissionsByRecruteurWithCount(@PathVariable Long recruteurId) {
        var missions = missionService.listByRecruteur(recruteurId).stream().map(missionService::toDTO).toList();
        var resp = new java.util.HashMap<String, Object>();
        resp.put("total", missions.size());
        resp.put("missions", missions);
        return ResponseEntity.ok(new ApiResponse(true, "Missions du recruteur", resp));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CreateMissionRequest req) {
        try {
            Mission m = missionService.update(id, req.titre(), req.description(), req.exigence(), req.dateDebut(), req.dateFin(), req.localisation(), req.remuneration(), req.categorieNom(), null, req.heureDebut(), req.heureFin());
            MissionDTO dto = missionService.toDTO(m);
            return ResponseEntity.ok(new ApiResponse(true, "Mission mise à jour", dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            missionService.delete(id);
            return ResponseEntity.ok(new ApiResponse(true, "Mission supprimée", null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @PostMapping("/verifier-terminaison")
    public ResponseEntity<?> verifierTerminaison() {
        missionService.verifierMissionsTerminees();
        return ResponseEntity.ok(new ApiResponse(true, "Vérification des missions terminées effectuée", null));
    }
}
