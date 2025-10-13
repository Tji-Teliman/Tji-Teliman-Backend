package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.entites.Candidature;
import com.example.Tji_Teliman.entites.Paiement;
import com.example.Tji_Teliman.entites.enums.StatutPaiement;
import com.example.Tji_Teliman.repository.CandidatureRepository;
import com.example.Tji_Teliman.repository.PaiementRepository;
import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final CandidatureRepository candidatureRepository;
    private final NotificationService notificationService;

    public PaiementService(PaiementRepository paiementRepository, CandidatureRepository candidatureRepository, NotificationService notificationService) {
        this.paiementRepository = paiementRepository;
        this.candidatureRepository = candidatureRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Paiement effectuerPaiement(Long candidatureId, Double montant) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new IllegalArgumentException("Candidature introuvable"));

        Paiement paiement = new Paiement();
        paiement.setMontant(montant);
        paiement.setDatePaiement(new Date());
        paiement.setStatutPaiement(StatutPaiement.REUSSIE);
        paiement.setCandidature(candidature);
        paiement.setRecruteur(candidature.getRecruteurValidateur());

        Paiement saved = paiementRepository.save(paiement);

        // Notifier le recruteur (confirmation) et le jeune (paiement reçu)
        notificationService.notifyPaiementEffectue(saved.getRecruteur(), saved);
        notificationService.notifyPaiementEffectue(candidature.getJeunePrestateur(), saved);

        return saved;
    }
}
