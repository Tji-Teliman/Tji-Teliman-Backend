package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.entites.Candidature;
import com.example.Tji_Teliman.entites.Mission;
import com.example.Tji_Teliman.entites.Notification;
import com.example.Tji_Teliman.entites.Paiement;
import com.example.Tji_Teliman.entites.Utilisateur;
import com.example.Tji_Teliman.entites.enums.TypeNotification;
import com.example.Tji_Teliman.repository.NotificationRepository;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Notification notify(Utilisateur destinataire, String titre, String contenu, TypeNotification type, Mission mission, Candidature candidature, Paiement paiement) {
        Objects.requireNonNull(destinataire, "destinataire obligatoire");
        Objects.requireNonNull(type, "type obligatoire");
        Objects.requireNonNull(contenu, "contenu obligatoire");
        Notification n = new Notification();
        n.setDestinataire(destinataire);
        n.setTitre(titre);
        n.setContenu(contenu);
        n.setType(type);
        n.setEstLue(false);
        n.setMission(mission);
        n.setCandidature(candidature);
        n.setPaiement(paiement);
        return notificationRepository.save(n);
    }

    @Transactional
    public Notification notifyCandidatureAcceptee(Utilisateur jeune, Candidature candidature) {
        String titre = "Candidature acceptée";
        String contenu = "Votre candidature pour la mission '" + candidature.getMission().getTitre() + "' a été acceptée.";
        return notify(jeune, titre, contenu, TypeNotification.CANDIDATURE_ACCEPTEE, candidature.getMission(), candidature, null);
    }

    @Transactional
    public Notification notifyMissionTerminee(Utilisateur recruteur, Mission mission) {
        String titre = "Mission terminée";
        String contenu = "La mission '" + mission.getTitre() + "' est terminée. Merci d'effectuer le paiement.";
        return notify(recruteur, titre, contenu, TypeNotification.MISSION_TERMINEE, mission, null, null);
    }

    @Transactional
    public Notification notifyPaiementEffectue(Utilisateur destinataire, Paiement paiement) {
        String titre = "Paiement effectué";
        String contenu = "Le paiement de " + paiement.getMontant() + " a été effectué.";
        return notify(destinataire, titre, contenu, TypeNotification.PAIEMENT_EFFECTUE, paiement.getCandidature().getMission(), paiement.getCandidature(), paiement);
    }
}
