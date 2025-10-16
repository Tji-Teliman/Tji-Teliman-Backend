package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.config.JwtUtils;
import com.example.Tji_Teliman.entites.Mission;
import com.example.Tji_Teliman.services.MissionService;
import com.example.Tji_Teliman.services.GoogleMapsService;
import com.example.Tji_Teliman.dto.MissionDTO;
import com.example.Tji_Teliman.dto.CandidatureDTO;
import com.example.Tji_Teliman.services.CandidatureService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final GoogleMapsService googleMapsService;
    private final JwtUtils jwtUtils;
    private final CandidatureService candidatureService;

    public MissionController(MissionService missionService, GoogleMapsService googleMapsService, JwtUtils jwtUtils, CandidatureService candidatureService) {
        this.missionService = missionService;
        this.googleMapsService = googleMapsService;
        this.jwtUtils = jwtUtils;
        this.candidatureService = candidatureService;
    }

    public record CreateMissionRequest(
        String titre,
        String description,
        String exigence,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateDebut,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateFin,
        Double remuneration,
        String categorieNom,
        String heureDebut,
        String heureFin,
        Double latitude,
        Double longitude,
        String adresse,
        String placeId
    ) {}

    public record ApiResponse(boolean success, String message, Object data) {}

    @PostMapping("/creer")
    public ResponseEntity<?> create(@RequestBody CreateMissionRequest req, HttpServletRequest httpRequest) {
        try {
            Long recruteurId = jwtUtils.getUserIdFromToken(httpRequest);
            if (recruteurId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            Mission m = missionService.create(recruteurId, req.titre(), req.description(), req.exigence(), req.dateDebut(), req.dateFin(), req.remuneration(), req.categorieNom(), req.heureDebut(), req.heureFin(), req.latitude(), req.longitude(), req.adresse(), req.placeId());
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

    @GetMapping("/en-attente")
    public ResponseEntity<List<MissionDTO>> listEnAttente() {
        return ResponseEntity.ok(missionService.listEnAttente().stream().map(missionService::toDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "Mission", missionService.getById(id)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @GetMapping("/mes-missions")
    public ResponseEntity<?> listByRecruteur(HttpServletRequest httpRequest) {
        try {
            Long recruteurId = jwtUtils.getUserIdFromToken(httpRequest);
            if (recruteurId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            List<MissionDTO> missions = missionService.listByRecruteur(recruteurId).stream().map(missionService::toDTO).toList();
            return ResponseEntity.ok(new ApiResponse(true, "Mes missions", missions));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @GetMapping("/mes-stats")
    public ResponseEntity<?> getMissionsByRecruteurWithCount(HttpServletRequest httpRequest) {
        try {
            Long recruteurId = jwtUtils.getUserIdFromToken(httpRequest);
            if (recruteurId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            var missions = missionService.listByRecruteur(recruteurId).stream().map(missionService::toDTO).toList();
            var resp = new java.util.HashMap<String, Object>();
            resp.put("total", missions.size());
            resp.put("missions", missions);
            return ResponseEntity.ok(new ApiResponse(true, "Mes statistiques de missions", resp));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @GetMapping("/mes-missions-accomplies")
    public ResponseEntity<?> getMissionsAccompliesByJeune(HttpServletRequest httpRequest) {
        try {
            Long jeuneId = jwtUtils.getUserIdFromToken(httpRequest);
            if (jeuneId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            List<CandidatureDTO> missionsAccomplies = candidatureService.getMissionsAccompliesByJeune(jeuneId);
            Long nombreMissions = candidatureService.getNombreMissionsAccompliesByJeune(jeuneId);
            
            var response = new java.util.HashMap<String, Object>();
            response.put("nombreMissions", nombreMissions);
            response.put("missions", missionsAccomplies);
            
            return ResponseEntity.ok(new ApiResponse(true, "Missions accomplies récupérées", response));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @PutMapping("/{id}/terminer")
    public ResponseEntity<?> terminerMission(@PathVariable Long id) {
        try {
            Mission mission = missionService.terminerMission(id);
            MissionDTO dto = missionService.toDTO(mission);
            return ResponseEntity.ok(new ApiResponse(true, "Mission terminée avec succès", dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CreateMissionRequest req) {
        try {
            Mission m = missionService.update(id, req.titre(), req.description(), req.exigence(), req.dateDebut(), req.dateFin(), req.remuneration(), req.categorieNom(), null, req.heureDebut(), req.heureFin(), req.latitude(), req.longitude(), req.adresse(), req.placeId());
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

    /**
     * Endpoint pour le géocodage inverse : convertir lat/lng en placeId et adresse
     * Utile quand le frontend a seulement les coordonnées du clic sur la carte
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
}
