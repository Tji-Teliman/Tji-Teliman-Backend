package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.config.JwtUtils;
import com.example.Tji_Teliman.dto.NotificationDTO;
import com.example.Tji_Teliman.entites.Notification;
import com.example.Tji_Teliman.entites.Notation;
import com.example.Tji_Teliman.entites.Recruteur;
import com.example.Tji_Teliman.entites.Utilisateur;
import com.example.Tji_Teliman.entites.enums.TypeNotification;
import com.example.Tji_Teliman.repository.NotificationRepository;
import com.example.Tji_Teliman.repository.NotationRepository;
import com.example.Tji_Teliman.repository.UtilisateurRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotationRepository notationRepository;
    private final JwtUtils jwtUtils;

    public NotificationController(NotificationRepository notificationRepository, UtilisateurRepository utilisateurRepository, NotationRepository notationRepository, JwtUtils jwtUtils) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.notationRepository = notationRepository;
        this.jwtUtils = jwtUtils;
    }

    public record ApiResponse(boolean success, String message, Object data) {}

    // Lister les notifications de l'utilisateur connecté
    // Par défaut, ne marque PAS les notifications comme lues
    // Pour marquer comme lues, utiliser le paramètre ?marquerCommeLues=true
    @GetMapping("/mes-notifications")
    public ResponseEntity<?> listByUtilisateur(HttpServletRequest httpRequest,
                                               @RequestParam(name = "marquerCommeLues", defaultValue = "false") boolean marquerCommeLues) {
        try {
            Long utilisateurId = jwtUtils.getUserIdFromToken(httpRequest);
            if (utilisateurId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            Utilisateur u = utilisateurRepository.findById(utilisateurId).orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
            List<Notification> notifications = notificationRepository.findByDestinataireOrderByDateCreationDesc(u);
            
            // Marquer les notifications comme lues UNIQUEMENT si le paramètre est explicitement à true
            // Cela permet au frontend de récupérer les notifications sans les marquer comme lues
            // et de les marquer comme lues uniquement quand l'utilisateur consulte réellement la page
            if (marquerCommeLues) {
                List<Notification> notificationsAMettreAJour = new ArrayList<>();
                for (Notification n : notifications) {
                    if (!n.isEstLue()) {
                        n.setEstLue(true);
                        notificationsAMettreAJour.add(n);
                    }
                }
                if (!notificationsAMettreAJour.isEmpty()) {
                    notificationRepository.saveAll(notificationsAMettreAJour);
                }
            }
            
            List<NotificationDTO> list = notifications.stream().map(this::toDTO).toList();
            return ResponseEntity.ok(new ApiResponse(true, "Mes notifications", list));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage(), null));
        }
    }

    // Marquer une notification comme lue
    @PutMapping("/{notificationId}/lue")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        Notification n = notificationRepository.findById(notificationId).orElseThrow(() -> new IllegalArgumentException("Notification introuvable"));
        n.setEstLue(true);
        notificationRepository.save(n);
        return ResponseEntity.ok(new ApiResponse(true, "Notification marquée comme lue", toDTO(n)));
    }

    // Supprimer une notification
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
        dto.setDateCreationRelative(calculateRelativeTime(n.getDateCreation()));
        dto.setEstLue(n.isEstLue());
        if (n.getDestinataire() != null) {
            dto.setDestinataireId(n.getDestinataire().getId());
            dto.setDestinataireNom(n.getDestinataire().getNom());
            dto.setDestinatairePrenom(n.getDestinataire().getPrenom());
        }
        
        // Remplir les informations contextuelles selon le type de notification
        TypeNotification type = n.getType();
        if (type != null) {
            switch (type) {
                case CANDIDATURE_ACCEPTEE:
                    // Nom de la mission
                    if (n.getMission() != null) {
                        dto.setMissionTitre(n.getMission().getTitre());
                    } else if (n.getCandidature() != null && n.getCandidature().getMission() != null) {
                        dto.setMissionTitre(n.getCandidature().getMission().getTitre());
                    }

                    // Informations sur le recruteur (interlocuteur pour le chat)
                    Recruteur recruteur = n.getRecruteurContextuel();
                    if (recruteur != null) {
                        dto.setInterlocuteurId(recruteur.getId());
                        dto.setRecruteurId(recruteur.getId());
                        dto.setRecruteurNom(recruteur.getNom());
                        dto.setRecruteurPrenom(recruteur.getPrenom());
                        dto.setRecruteurPhoto(recruteur.getUrlPhoto());
                    }
                    break;
                    
                case MISSION_TERMINEE:
                    // Nom de la mission
                    if (n.getMission() != null) {
                        dto.setMissionTitre(n.getMission().getTitre());
                    }
                    break;
                    
                case PAIEMENT_EFFECTUE:
                    // Montant, nom de la mission et par qui
                    if (n.getPaiement() != null) {
                        dto.setMontantPaiement(n.getPaiement().getMontant());
                        dto.setMontantFrais(n.getPaiement().getFrais());
                        dto.setMontantTotalPaiement(n.getPaiement().getMontantTotal());
                        if (n.getPaiement().getRecruteur() != null) {
                            dto.setPaiementParNom(n.getPaiement().getRecruteur().getNom());
                            dto.setPaiementParPrenom(n.getPaiement().getRecruteur().getPrenom());
                        }
                    }
                    if (n.getMission() != null) {
                        dto.setMissionTitre(n.getMission().getTitre());
                    } else if (n.getCandidature() != null && n.getCandidature().getMission() != null) {
                        dto.setMissionTitre(n.getCandidature().getMission().getTitre());
                    } else if (n.getPaiement() != null && n.getPaiement().getCandidature() != null && n.getPaiement().getCandidature().getMission() != null) {
                        dto.setMissionTitre(n.getPaiement().getCandidature().getMission().getTitre());
                    }
                    break;
                    
                case DEMANDE_NOTATION_RECRUTEUR:
                    // Nom de la personne à noter (jeune prestataire) et nom de la mission
                    if (n.getCandidature() != null) {
                        // Vérifier si la notation (recruteur -> jeune) existe déjà
                        boolean dejaNote = notationRepository.findAll().stream()
                            .anyMatch(not -> not.getCandidature() != null
                                && not.getCandidature().getId().equals(n.getCandidature().getId())
                                && not.isInitieParRecruteur()); // notation faite par recruteur
                        dto.setNotationDejaFaite(dejaNote);

                        if (n.getCandidature().getJeunePrestateur() != null) {
                            dto.setPersonneANoterNom(n.getCandidature().getJeunePrestateur().getNom());
                            dto.setPersonneANoterPrenom(n.getCandidature().getJeunePrestateur().getPrenom());
                        }
                        if (n.getCandidature().getMission() != null) {
                            dto.setMissionTitre(n.getCandidature().getMission().getTitre());
                        }
                    }
                    break;
                    
                case DEMANDE_NOTATION_JEUNE:
                    // Nom de la personne à noter (recruteur) et nom de la mission
                    if (n.getCandidature() != null) {
                        // Vérifier si la notation (jeune -> recruteur) existe déjà
                        boolean dejaNote = notationRepository.findAll().stream()
                            .anyMatch(not -> not.getCandidature() != null
                                && not.getCandidature().getId().equals(n.getCandidature().getId())
                                && !not.isInitieParRecruteur()); // notation faite par jeune
                        dto.setNotationDejaFaite(dejaNote);

                        if (n.getCandidature().getRecruteurValidateur() != null) {
                            dto.setPersonneANoterNom(n.getCandidature().getRecruteurValidateur().getNom());
                            dto.setPersonneANoterPrenom(n.getCandidature().getRecruteurValidateur().getPrenom());
                        }
                        if (n.getCandidature().getMission() != null) {
                            dto.setMissionTitre(n.getCandidature().getMission().getTitre());
                        }
                    }
                    break;
                    
                case NOTATION_RECUE:
                    // La note reçue et la personne qui l'a donnée
                    if (n.getCandidature() != null) {
                        // Récupérer la notation la plus récente pour cette candidature
                        Optional<Notation> notationOpt = notationRepository.findAll().stream()
                            .filter(not -> not.getCandidature() != null && 
                                          not.getCandidature().getId().equals(n.getCandidature().getId()))
                            .sorted((not1, not2) -> {
                                if (not1.getDateNotation() == null && not2.getDateNotation() == null) return 0;
                                if (not1.getDateNotation() == null) return 1;
                                if (not2.getDateNotation() == null) return -1;
                                return not2.getDateNotation().compareTo(not1.getDateNotation());
                            })
                            .findFirst();
                        
                        if (notationOpt.isPresent()) {
                            Notation notation = notationOpt.get();
                            dto.setNoteRecue(notation.getNote());
                            
                            // Déterminer qui a noté (celui qui a initié la notation)
                            if (notation.isInitieParRecruteur()) {
                                if (notation.getRecruteur() != null) {
                                    dto.setPersonneQuiANoteNom(notation.getRecruteur().getNom());
                                    dto.setPersonneQuiANotePrenom(notation.getRecruteur().getPrenom());
                                }
                            } else {
                                if (notation.getJeunePrestateur() != null) {
                                    dto.setPersonneQuiANoteNom(notation.getJeunePrestateur().getNom());
                                    dto.setPersonneQuiANotePrenom(notation.getJeunePrestateur().getPrenom());
                                }
                            }
                        }
                        
                        // Nom de la mission
                        if (n.getCandidature().getMission() != null) {
                            dto.setMissionTitre(n.getCandidature().getMission().getTitre());
                        }
                    }
                    break;
            }
        }
        
        // Renseigner les identifiants génériques (candidature et mission)
        if (n.getCandidature() != null) {
            dto.setCandidatureId(n.getCandidature().getId());
        }
        // Récupérer la mission source (directe, via candidature, ou via paiement.candidature)
        var mission = n.getMission() != null ? n.getMission()
            : (n.getCandidature() != null ? n.getCandidature().getMission() : null);
        if (mission == null && n.getPaiement() != null && n.getPaiement().getCandidature() != null) {
            mission = n.getPaiement().getCandidature().getMission();
        }
        if (mission != null) {
            dto.setMissionId(mission.getId());
            if (dto.getMissionTitre() == null) {
                dto.setMissionTitre(mission.getTitre());
            }
            dto.setMissionDateFin(mission.getDateFin());
            dto.setMissionRemuneration(mission.getRemuneration());
        }
        
        return dto;
    }

    /**
     * Calcule le temps relatif depuis la date de création jusqu'à maintenant
     * Retourne une chaîne formatée en français (ex: "il y a 1 seconde", "il y a 30 secondes", "il y a 1 minute")
     */
    private String calculateRelativeTime(Date dateCreation) {
        if (dateCreation == null) {
            return "Date inconnue";
        }
        
        Date now = new Date();
        long diffInMillis = now.getTime() - dateCreation.getTime();
        
        // Si la date est dans le futur, retourner "à l'instant"
        if (diffInMillis < 0) {
            return "à l'instant";
        }
        
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
        long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        
        if (seconds < 1) {
            return "à l'instant";
        } else if (seconds < 60) {
            return "il y a " + seconds + (seconds == 1 ? " seconde" : " secondes");
        } else if (minutes < 60) {
            return "il y a " + minutes + (minutes == 1 ? " minute" : " minutes");
        } else if (hours < 24) {
            return "il y a " + hours + (hours == 1 ? " heure" : " heures");
        } else if (days < 30) {
            return "il y a " + days + (days == 1 ? " jour" : " jours");
        } else {
            long months = days / 30;
            if (months < 12) {
                return "il y a " + months + (months == 1 ? " mois" : " mois");
            } else {
                long years = days / 365;
                return "il y a " + years + (years == 1 ? " an" : " ans");
            }
        }
    }
}
