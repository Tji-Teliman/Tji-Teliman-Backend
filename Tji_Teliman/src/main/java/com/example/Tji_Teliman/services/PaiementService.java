package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.entites.Candidature;
import com.example.Tji_Teliman.entites.Paiement;
import com.example.Tji_Teliman.entites.enums.StatutPaiement;
import com.example.Tji_Teliman.dto.PaiementDTO;
import com.example.Tji_Teliman.repository.CandidatureRepository;
import com.example.Tji_Teliman.repository.PaiementRepository;
import com.example.Tji_Teliman.entites.Mission;
import com.example.Tji_Teliman.entites.enums.StatutCandidature;
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

        Double remuneration = candidature.getMission() != null ? candidature.getMission().getRemuneration() : null;
        if (remuneration == null) {
            throw new IllegalArgumentException("La mission ne possède pas de rémunération définie");
        }

        double frais = calculateFrais(remuneration);
        double montantTotalAttendu = remuneration + frais;

        if (montant != null && Math.abs(montant - montantTotalAttendu) > 0.01) {
            throw new IllegalArgumentException(
                "Le montant à payer doit être égal à la rémunération (" + remuneration + ") + 2% de frais (" + frais + "), soit " + montantTotalAttendu
            );
        }

        Paiement paiement = candidature.getPaiement();
        if (paiement == null) {
            paiement = createPendingPaiement(candidature);
        } else if (paiement.getStatutPaiement() == StatutPaiement.REUSSIE) {
            throw new IllegalArgumentException("Un paiement a déjà été effectué pour cette candidature");
        }

        paiement.setMontant(remuneration);
        paiement.setFrais(frais);
        paiement.setMontantTotal(montantTotalAttendu);
        paiement.setTelephone(telephone);
        paiement.setDatePaiement(new Date());
        paiement.setStatutPaiement(StatutPaiement.REUSSIE);

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
    public List<PaiementDTO> getPaiementsByStatut(StatutPaiement statut) {
        return paiementRepository.findByStatutPaiement(statut).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PaiementDTO toDTO(Paiement p) {
        PaiementDTO dto = new PaiementDTO();
        dto.setId(p.getId());
        dto.setMontant(p.getMontant());
        dto.setFrais(p.getFrais());
        dto.setMontantTotal(p.getMontantTotal());
        dto.setTelephone(p.getTelephone());
        dto.setDatePaiement(p.getDatePaiement());
        dto.setStatutPaiement(p.getStatutPaiement() == null ? null : p.getStatutPaiement().name());
        
        if (p.getCandidature() != null) {
            dto.setCandidatureId(p.getCandidature().getId());
            
            if (p.getCandidature().getJeunePrestateur() != null) {
                dto.setJeunePrestateurId(p.getCandidature().getJeunePrestateur().getId());
                dto.setJeunePrestateurNom(p.getCandidature().getJeunePrestateur().getNom());
                dto.setJeunePrestateurPrenom(p.getCandidature().getJeunePrestateur().getPrenom());
                dto.setJeunePrestateurTelephone(p.getCandidature().getJeunePrestateur().getTelephone());
            }
            
            if (p.getCandidature().getMission() != null) {
                dto.setMissionId(p.getCandidature().getMission().getId());
                dto.setMissionTitre(p.getCandidature().getMission().getTitre());
                dto.setMissionRemuneration(p.getCandidature().getMission().getRemuneration());
                dto.setMissionStatut(p.getCandidature().getMission().getStatut() != null ? p.getCandidature().getMission().getStatut().name() : null);
                dto.setMissionDateDebut(p.getCandidature().getMission().getDateDebut());
                dto.setMissionDateFin(p.getCandidature().getMission().getDateFin());
            }
        }
        
        if (p.getRecruteur() != null) {
            dto.setRecruteurId(p.getRecruteur().getId());
            dto.setRecruteurNom(p.getRecruteur().getNom());
            dto.setRecruteurPrenom(p.getRecruteur().getPrenom());
        }
        
        return dto;
    }

    @Transactional
    public void createPendingPaiementsForMission(Mission mission) {
        if (mission == null) return;
        candidatureRepository.findByMission(mission).stream()
            .filter(c -> c.getStatut() == StatutCandidature.ACCEPTEE)
            .forEach(this::createPendingPaiement);
    }

    @Transactional
    public Paiement createPendingPaiement(Candidature candidature) {
        if (candidature == null) {
            throw new IllegalArgumentException("Candidature invalide");
        }
        Paiement existing = candidature.getPaiement();
        if (existing != null) {
            return existing;
        }

        Paiement paiement = new Paiement();
        Double remuneration = candidature.getMission() != null ? candidature.getMission().getRemuneration() : null;
        if (remuneration == null) {
            throw new IllegalArgumentException("La mission ne possède pas de rémunération définie");
        }

        double frais = calculateFrais(remuneration);
        paiement.setMontant(remuneration);
        paiement.setFrais(frais);
        paiement.setMontantTotal(remuneration + frais);
        paiement.setTelephone(null);
        paiement.setDatePaiement(null);
        paiement.setStatutPaiement(StatutPaiement.EN_ATTENTE);
        paiement.setCandidature(candidature);
        paiement.setRecruteur(candidature.getRecruteurValidateur());

        Paiement saved = paiementRepository.save(paiement);
        candidature.setPaiement(saved);
        candidatureRepository.save(candidature);
        return saved;
    }

    private double calculateFrais(Double remuneration) {
        return remuneration == null ? 0d : remuneration * 0.02;
    }
}
