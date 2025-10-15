package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.entites.Notation;
import com.example.Tji_Teliman.services.NotationService;
import com.example.Tji_Teliman.dto.NotationDTO;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
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

    public NotationController(NotationService notationService) {
        this.notationService = notationService;
    }

    public record NoterJeuneRequest(Integer note, String commentaire) {}
    public record NoterRecruteurRequest(Integer note, String commentaire) {}
    public record ApiResponse(boolean success, String message, Object data) {}

    /**
     * Noter un jeune prestataire par le recruteur
     * Se déclenche automatiquement après un paiement
     */
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

    @GetMapping("/all")
    public ResponseEntity<List<NotationDTO>> getAllNotations() {
        List<NotationDTO> notations = notationService.getAllNotations().stream()
                .map(notationService::toDTO)
                .toList();
        return ResponseEntity.ok(notations);
    }

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

    @GetMapping("/candidature/{candidatureId}")
    public ResponseEntity<?> getNotationsByCandidature(@PathVariable Long candidatureId) {
        List<NotationDTO> notations = notationService.getNotationsByCandidature(candidatureId).stream()
                .map(notationService::toDTO)
                .toList();
        return ResponseEntity.ok(new ApiResponse(true, "Notations trouvées", notations));
    }

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

    @GetMapping("/recruteur/{recruteurId}")
    public ResponseEntity<List<NotationDTO>> getNotationsByRecruteur(@PathVariable Long recruteurId) {
        List<NotationDTO> notations = notationService.getNotationsByRecruteur(recruteurId).stream()
                .map(notationService::toDTO)
                .toList();
        return ResponseEntity.ok(notations);
    }

    @GetMapping("/jeune/{jeuneId}")
    public ResponseEntity<List<NotationDTO>> getNotationsByJeunePrestateur(@PathVariable Long jeuneId) {
        List<NotationDTO> notations = notationService.getNotationsByJeunePrestateur(jeuneId).stream()
                .map(notationService::toDTO)
                .toList();
        return ResponseEntity.ok(notations);
    }

    @GetMapping("/recruteur/{recruteurId}/recues")
    public ResponseEntity<List<NotationDTO>> getNotationsRecuesParRecruteur(@PathVariable Long recruteurId) {
        List<NotationDTO> notations = notationService.getNotationsRecuesParRecruteur(recruteurId).stream()
                .map(notationService::toDTO)
                .toList();
        return ResponseEntity.ok(notations);
    }

    @GetMapping("/jeune/{jeuneId}/recues")
    public ResponseEntity<List<NotationDTO>> getNotationsRecuesParJeune(@PathVariable Long jeuneId) {
        List<NotationDTO> notations = notationService.getNotationsRecuesParJeune(jeuneId).stream()
                .map(notationService::toDTO)
                .toList();
        return ResponseEntity.ok(notations);
    }

    @GetMapping("/recruteur/{recruteurId}/moyenne")
    public ResponseEntity<?> getMoyenneNotesRecruteur(@PathVariable Long recruteurId) {
        Double moyenne = notationService.getMoyenneNotesRecruteur(recruteurId);
        if (moyenne != null) {
            return ResponseEntity.ok(new ApiResponse(true, "Moyenne calculée", moyenne));
        } else {
            return ResponseEntity.ok(new ApiResponse(false, "Aucune notation reçue", null));
        }
    }

    @GetMapping("/jeune/{jeuneId}/moyenne")
    public ResponseEntity<?> getMoyenneNotesJeune(@PathVariable Long jeuneId) {
        Double moyenne = notationService.getMoyenneNotesJeune(jeuneId);
        if (moyenne != null) {
            return ResponseEntity.ok(new ApiResponse(true, "Moyenne calculée", moyenne));
        } else {
            return ResponseEntity.ok(new ApiResponse(false, "Aucune notation reçue", null));
        }
    }
}
