package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.entites.Candidature;
import com.example.Tji_Teliman.entites.Paiement;
import com.example.Tji_Teliman.entites.enums.StatutPaiement;
import com.example.Tji_Teliman.dto.PaiementDTO;
import com.example.Tji_Teliman.repository.CandidatureRepository;
import com.example.Tji_Teliman.repository.PaiementRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
    public Paiement effectuerPaiement(Long candidatureId, Double montant, String telephone) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new IllegalArgumentException("Candidature introuvable"));

        // Vérifier que la candidature est acceptée
        if (candidature.getStatut() != com.example.Tji_Teliman.entites.enums.StatutCandidature.ACCEPTEE) {
            throw new IllegalArgumentException("Le paiement ne peut être effectué que pour une candidature acceptée");
        }

        // Vérifier que la mission est terminée
        if (candidature.getMission().getStatut() != com.example.Tji_Teliman.entites.enums.StatutMission.TERMINEE) {
            throw new IllegalArgumentException("Le paiement ne peut être effectué que pour une mission terminée");
        }

        // Vérifier qu'il n'y a pas déjà un paiement pour cette candidature
        if (candidature.getPaiement() != null) {
            throw new IllegalArgumentException("Un paiement a déjà été effectué pour cette candidature");
        }

        // Vérifier que le montant correspond à la rémunération de la mission
        if (candidature.getMission().getRemuneration() != null && 
            !candidature.getMission().getRemuneration().equals(montant)) {
            throw new IllegalArgumentException("Le montant doit correspondre à la rémunération de la mission (" + 
                candidature.getMission().getRemuneration() + ")");
        }

        Paiement paiement = new Paiement();
        paiement.setMontant(montant);
        paiement.setTelephone(telephone);
        paiement.setDatePaiement(new Date());
        paiement.setStatutPaiement(StatutPaiement.REUSSIE);
        paiement.setCandidature(candidature);
        paiement.setRecruteur(candidature.getRecruteurValidateur());

        Paiement saved = paiementRepository.save(paiement);

        // Notifier le recruteur (confirmation) et le jeune (paiement reçu)
        notificationService.notifyPaiementEffectue(saved.getRecruteur(), saved);
        notificationService.notifyPaiementEffectue(candidature.getJeunePrestateur(), saved);

        // Déclencher automatiquement la demande de notation du recruteur vers le jeune
        notificationService.notifyDemandeNotationRecruteur(saved.getRecruteur(), candidature);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Paiement> getAllPaiements() {
        return paiementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Paiement> getPaiementById(Long id) {
        return paiementRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Paiement> getPaiementsByRecruteur(Long recruteurId) {
        return paiementRepository.findAll().stream()
                .filter(p -> p.getRecruteur() != null && p.getRecruteur().getId().equals(recruteurId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Paiement> getPaiementsByJeunePrestateur(Long jeuneId) {
        return paiementRepository.findAll().stream()
                .filter(p -> p.getCandidature() != null && 
                           p.getCandidature().getJeunePrestateur() != null && 
                           p.getCandidature().getJeunePrestateur().getId().equals(jeuneId))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Paiement> getPaiementByCandidature(Long candidatureId) {
        return paiementRepository.findAll().stream()
                .filter(p -> p.getCandidature() != null && p.getCandidature().getId().equals(candidatureId))
                .findFirst();
    }

    @Transactional(readOnly = true)
    public List<Paiement> getPaiementsByStatut(StatutPaiement statut) {
        return paiementRepository.findAll().stream()
                .filter(p -> p.getStatutPaiement() == statut)
                .toList();
    }

    @Transactional(readOnly = true)
    public PaiementDTO toDTO(Paiement p) {
        PaiementDTO dto = new PaiementDTO();
        dto.setId(p.getId());
        dto.setMontant(p.getMontant());
        dto.setTelephone(p.getTelephone());
        dto.setDatePaiement(p.getDatePaiement());
        dto.setStatutPaiement(p.getStatutPaiement() == null ? null : p.getStatutPaiement().name());
        
        if (p.getCandidature() != null) {
            dto.setCandidatureId(p.getCandidature().getId());
            
            if (p.getCandidature().getJeunePrestateur() != null) {
                dto.setJeunePrestateurId(p.getCandidature().getJeunePrestateur().getId());
                dto.setJeunePrestateurNom(p.getCandidature().getJeunePrestateur().getNom());
                dto.setJeunePrestateurPrenom(p.getCandidature().getJeunePrestateur().getPrenom());
            }
            
            if (p.getCandidature().getMission() != null) {
                dto.setMissionId(p.getCandidature().getMission().getId());
                dto.setMissionTitre(p.getCandidature().getMission().getTitre());
                dto.setMissionRemuneration(p.getCandidature().getMission().getRemuneration());
            }
        }
        
        if (p.getRecruteur() != null) {
            dto.setRecruteurId(p.getRecruteur().getId());
            dto.setRecruteurNom(p.getRecruteur().getNom());
            dto.setRecruteurPrenom(p.getRecruteur().getPrenom());
        }
        
        return dto;
    }
}
