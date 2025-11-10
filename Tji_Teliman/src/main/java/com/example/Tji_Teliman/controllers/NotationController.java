package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.config.JwtUtils;
import com.example.Tji_Teliman.entites.Notation;
import com.example.Tji_Teliman.services.NotationService;
import com.example.Tji_Teliman.dto.NotationDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notations")
public class NotationController {

    private final NotationService notationService;
    private final JwtUtils jwtUtils;

    public NotationController(NotationService notationService, JwtUtils jwtUtils) {
        this.notationService = notationService;
        this.jwtUtils = jwtUtils;
    }

    public record NoterJeuneRequest(Integer note, String commentaire) {}
    public record NoterRecruteurRequest(Integer note, String commentaire) {}
    public record ApiResponse(boolean success, String message, Object data) {}

    /**
     * Noter un jeune prestataire par le recruteur
     * Se déclenche automatiquement après un paiement
     */
    // Noter un jeune (par le recruteur) pour une candidature donnée
    @PostMapping("/recruteur-noter-jeune/{candidatureId}")
    public ResponseEntity<?> noterJeuneParRecruteur(@PathVariable Long candidatureId, @RequestBody NoterJeuneRequest req) {
        try {
            Notation notation = notationService.noterJeuneParRecruteur(candidatureId, req.note(), req.commentaire());
            NotationDTO dto = notationService.toDTO(notation);
            return ResponseEntity.ok(new ApiResponse(true, "Jeune prestataire noté avec succès", dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    /**
     * Noter un recruteur par le jeune prestataire
     * Peut être effectuée après que le recruteur ait noté le jeune
     */
    // Noter un recruteur (par le jeune) pour une candidature donnée
    @PostMapping("/jeune-noter-recruteur/{candidatureId}")
    public ResponseEntity<?> noterRecruteurParJeune(@PathVariable Long candidatureId, @RequestBody NoterRecruteurRequest req) {
        try {
            Notation notation = notationService.noterRecruteurParJeune(candidatureId, req.note(), req.commentaire());
            NotationDTO dto = notationService.toDTO(notation);
            return ResponseEntity.ok(new ApiResponse(true, "Recruteur noté avec succès", dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    // Lister toutes les notations
    @GetMapping("/all")
    public ResponseEntity<List<NotationDTO>> getAllNotations() {
        List<NotationDTO> notations = notationService.getAllNotations().stream()
                .map(notationService::toDTO)
                .toList();
        return ResponseEntity.ok(notations);
    }

    // Obtenir une notation par son ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getNotationById(@PathVariable Long id) {
        Optional<Notation> notation = notationService.getNotationById(id);
        if (notation.isPresent()) {
            NotationDTO dto = notationService.toDTO(notation.get());
            return ResponseEntity.ok(new ApiResponse(true, "Notation trouvée", dto));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Notation introuvable", null));
        }
    }

    // Lister les notations liées à une candidature
    @GetMapping("/candidature/{candidatureId}")
    public ResponseEntity<?> getNotationsByCandidature(@PathVariable Long candidatureId) {
        List<NotationDTO> notations = notationService.getNotationsByCandidature(candidatureId).stream()
                .map(notationService::toDTO)
                .toList();
        return ResponseEntity.ok(new ApiResponse(true, "Notations trouvées", notations));
    }

    // Obtenir la notation du recruteur pour une candidature donnée
    @GetMapping("/candidature/{candidatureId}/recruteur")
    public ResponseEntity<?> getNotationRecruteurByCandidature(@PathVariable Long candidatureId) {
        Optional<Notation> notation = notationService.getNotationRecruteurByCandidature(candidatureId);
        if (notation.isPresent()) {
            NotationDTO dto = notationService.toDTO(notation.get());
            return ResponseEntity.ok(new ApiResponse(true, "Notation du recruteur trouvée", dto));
        } else {
            return ResponseEntity.ok(new ApiResponse(false, "Aucune notation du recruteur trouvée", null));
        }
    }

    // Obtenir la notation du jeune pour une candidature donnée
    @GetMapping("/candidature/{candidatureId}/jeune")
    public ResponseEntity<?> getNotationJeuneByCandidature(@PathVariable Long candidatureId) {
        Optional<Notation> notation = notationService.getNotationJeuneByCandidature(candidatureId);
        if (notation.isPresent()) {
            NotationDTO dto = notationService.toDTO(notation.get());
            return ResponseEntity.ok(new ApiResponse(true, "Notation du jeune trouvée", dto));
        } else {
            return ResponseEntity.ok(new ApiResponse(false, "Aucune notation du jeune trouvée", null));
        }
    }

    // Lister les notations du recruteur connecté
    @GetMapping("/mes-notations-recruteur")
    public ResponseEntity<?> getNotationsByRecruteur(HttpServletRequest httpRequest) {
        try {
            Long recruteurId = jwtUtils.getUserIdFromToken(httpRequest);
            if (recruteurId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            List<NotationDTO> notations = notationService.getNotationsByRecruteur(recruteurId).stream()
                    .map(notationService::toDTO)
                    .toList();
            return ResponseEntity.ok(new ApiResponse(true, "Mes notations récupérées", notations));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    // Lister les notations du jeune prestataire connecté
    @GetMapping("/mes-notations")
    public ResponseEntity<?> getNotationsByJeunePrestateur(HttpServletRequest httpRequest) {
        try {
            Long jeuneId = jwtUtils.getUserIdFromToken(httpRequest);
            if (jeuneId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            List<NotationDTO> notations = notationService.getNotationsByJeunePrestateur(jeuneId).stream()
                    .map(notationService::toDTO)
                    .toList();
            return ResponseEntity.ok(new ApiResponse(true, "Mes notations récupérées", notations));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    // Lister les notations reçues par l'utilisateur connecté
    @GetMapping("/mes-notations-recues")
    public ResponseEntity<?> getNotationsRecuesParUtilisateur(HttpServletRequest httpRequest) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(httpRequest);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            List<NotationDTO> notations = notationService.getNotationsRecuesParUtilisateur(userId).stream()
                    .map(notationService::toDTO)
                    .toList();
            return ResponseEntity.ok(new ApiResponse(true, "Mes notations reçues", notations));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    // Obtenir la moyenne des notes de l'utilisateur connecté
    @GetMapping("/moyenne")
    public ResponseEntity<?> getMoyenneNotesUtilisateur(HttpServletRequest httpRequest) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(httpRequest);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            Double moyenne = notationService.getMoyenneNotesUtilisateur(userId);
            if (moyenne != null) {
                return ResponseEntity.ok(new ApiResponse(true, "Moyenne calculée", moyenne));
            } else {
                return ResponseEntity.ok(new ApiResponse(false, "Aucune notation reçue", null));
            }
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }
}
