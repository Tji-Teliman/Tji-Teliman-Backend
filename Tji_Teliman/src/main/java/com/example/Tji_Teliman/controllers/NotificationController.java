package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.dto.NotificationDTO;
import com.example.Tji_Teliman.entites.Notification;
import com.example.Tji_Teliman.entites.Utilisateur;
import com.example.Tji_Teliman.repository.NotificationRepository;
import com.example.Tji_Teliman.repository.UtilisateurRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public NotificationController(NotificationRepository notificationRepository, UtilisateurRepository utilisateurRepository) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public record ApiResponse(boolean success, String message, Object data) {}

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<?> listByUtilisateur(@PathVariable Long utilisateurId) {
        Utilisateur u = utilisateurRepository.findById(utilisateurId).orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        List<Notification> notifications = notificationRepository.findByDestinataireOrderByDateCreationDesc(u);
        
        // Marquer toutes les notifications comme lues lors de la consultation
        for (Notification n : notifications) {
            if (!n.isEstLue()) {
                n.setEstLue(true);
                notificationRepository.save(n);
            }
        }
        
        List<NotificationDTO> list = notifications.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(new ApiResponse(true, "OK", list));
    }

    @PutMapping("/{notificationId}/lue")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        Notification n = notificationRepository.findById(notificationId).orElseThrow(() -> new IllegalArgumentException("Notification introuvable"));
        n.setEstLue(true);
        notificationRepository.save(n);
        return ResponseEntity.ok(new ApiResponse(true, "Notification marquée comme lue", toDTO(n)));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> delete(@PathVariable Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Notification introuvable", null));
        }
        notificationRepository.deleteById(notificationId);
        return ResponseEntity.ok(new ApiResponse(true, "Notification supprimée", null));
    }

    private NotificationDTO toDTO(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(n.getId());
        dto.setTitre(n.getTitre());
        dto.setContenu(n.getContenu());
        dto.setType(n.getType() == null ? null : n.getType().name());
        dto.setDateCreation(n.getDateCreation());
        dto.setEstLue(n.isEstLue());
        if (n.getDestinataire() != null) {
            dto.setDestinataireId(n.getDestinataire().getId());
            dto.setDestinataireNom(n.getDestinataire().getNom());
            dto.setDestinatairePrenom(n.getDestinataire().getPrenom());
        }
        // IDs de contexte supprimés du DTO selon la demande
        return dto;
    }
}
