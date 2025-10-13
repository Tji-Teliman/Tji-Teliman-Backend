package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.dto.CandidatureDTO;
import com.example.Tji_Teliman.dto.MotivationDTO;
import com.example.Tji_Teliman.entites.Candidature;
import com.example.Tji_Teliman.entites.JeunePrestateur;
import com.example.Tji_Teliman.entites.Mission;
import com.example.Tji_Teliman.entites.Motivation;
import com.example.Tji_Teliman.entites.enums.StatutCandidature;
import com.example.Tji_Teliman.entites.enums.StatutMission;
import com.example.Tji_Teliman.repository.CandidatureRepository;
import com.example.Tji_Teliman.repository.JeunePrestateurRepository;
import com.example.Tji_Teliman.repository.MissionRepository;
import com.example.Tji_Teliman.repository.MotivationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidatureService {

    private final CandidatureRepository candidatureRepository;
    private final MotivationRepository motivationRepository;
    private final JeunePrestateurRepository jeunePrestateurRepository;
    private final MissionRepository missionRepository;
    private final MissionService missionService;
    private final NotificationService notificationService;

    public CandidatureService(
            CandidatureRepository candidatureRepository,
            MotivationRepository motivationRepository,
            JeunePrestateurRepository jeunePrestateurRepository,
            MissionRepository missionRepository,
            MissionService missionService,
            NotificationService notificationService) {
        this.candidatureRepository = candidatureRepository;
        this.motivationRepository = motivationRepository;
        this.jeunePrestateurRepository = jeunePrestateurRepository;
        this.missionRepository = missionRepository;
        this.missionService = missionService;
        this.notificationService = notificationService;
    }

    @Transactional
    public Candidature postuler(Long jeunePrestateurId, Long missionId, String motivationContenu) {
        JeunePrestateur jeunePrestateur = jeunePrestateurRepository.findById(jeunePrestateurId)
                .orElseThrow(() -> new IllegalArgumentException("Jeune prestateur introuvable"));
        
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("Mission introuvable"));
        
        // Vérifier si le jeune a déjà postulé à cette mission
        if (candidatureRepository.existsByJeunePrestateurAndMission(jeunePrestateur, mission)) {
            throw new IllegalArgumentException("Vous avez déjà postulé à cette mission");
        }
        
        // Créer la candidature
        Candidature candidature = new Candidature();
        candidature.setJeunePrestateur(jeunePrestateur);
        candidature.setMission(mission);
        candidature.setRecruteurValidateur(mission.getRecruteur());
        candidature.setStatut(StatutCandidature.EN_ATTENTE);
        candidature.setDateSoumission(new Date());
        
        candidature = candidatureRepository.save(candidature);
        
        // Créer la motivation
        if (motivationContenu != null && !motivationContenu.trim().isEmpty()) {
            Motivation motivation = new Motivation();
            motivation.setContenu(motivationContenu);
            motivation.setCandidature(candidature);
            motivationRepository.save(motivation);
        }
        
        return candidature;
    }
    
    @Transactional(readOnly = true)
    public List<CandidatureDTO> getCandidaturesByJeune(Long jeunePrestateurId) {
        JeunePrestateur jeunePrestateur = jeunePrestateurRepository.findById(jeunePrestateurId)
                .orElseThrow(() -> new IllegalArgumentException("Jeune prestateur introuvable"));
        
        return candidatureRepository.findByJeunePrestateur(jeunePrestateur).stream()
                .map(this::toCandidatureDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CandidatureDTO> getCandidaturesByMission(Long missionId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("Mission introuvable"));
        
        return candidatureRepository.findByMission(mission).stream()
                .map(this::toCandidatureDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MotivationDTO> getMotivationsByCandidature(Long candidatureId) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new IllegalArgumentException("Candidature introuvable"));
        
        return motivationRepository.findByCandidature(candidature).stream()
                .map(this::toMotivationDTO)
                .collect(Collectors.toList());
    }
    
    // Changer de private à public
    public CandidatureDTO toCandidatureDTO(Candidature candidature) {
        CandidatureDTO dto = new CandidatureDTO();
        dto.setId(candidature.getId());
        dto.setStatut(candidature.getStatut().name());
        dto.setDateSoumission(candidature.getDateSoumission());
        
        // Ajouter les informations du jeune prestateur
        if (candidature.getJeunePrestateur() != null) {
            dto.setJeunePrestateurId(candidature.getJeunePrestateur().getId());
            dto.setJeunePrestateurNom(candidature.getJeunePrestateur().getNom());
            dto.setJeunePrestateurPrenom(candidature.getJeunePrestateur().getPrenom());
        }
        
        // Ajouter les informations de la mission
        if (candidature.getMission() != null) {
            dto.setMissionId(candidature.getMission().getId());
            dto.setMissionTitre(candidature.getMission().getTitre());
        }
        
        // Ajouter les informations du recruteur
        if (candidature.getRecruteurValidateur() != null) {
            dto.setRecruteurId(candidature.getRecruteurValidateur().getId());
            dto.setRecruteurNom(candidature.getRecruteurValidateur().getNom());
            dto.setRecruteurPrenom(candidature.getRecruteurValidateur().getPrenom());
        }
        
        return dto;
    }
    
    private MotivationDTO toMotivationDTO(Motivation motivation) {
        MotivationDTO dto = new MotivationDTO();
        dto.setId(motivation.getId());
        dto.setContenu(motivation.getContenu());
        dto.setDateSoumission(motivation.getDateSoumission());
        dto.setCandidatureId(motivation.getCandidature().getId());
        return dto;
    }
    
    @Transactional
    public Candidature validerCandidature(Long candidatureId, Long recruteurId) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new IllegalArgumentException("Candidature introuvable"));
        
        // Vérifier que le recruteur est bien le propriétaire de la mission
        if (!candidature.getMission().getRecruteur().getId().equals(recruteurId)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à valider cette candidature");
        }
        
        // Vérifier que la candidature est en attente
        if (candidature.getStatut() != StatutCandidature.EN_ATTENTE) {
            throw new IllegalArgumentException("Cette candidature a déjà été traitée");
        }
        
        // Mettre à jour le statut de la candidature
        candidature.setStatut(StatutCandidature.ACCEPTEE);
        
        // Mettre à jour le statut de la mission
        Mission mission = candidature.getMission();
        mission.setStatut(StatutMission.EN_COURS);
        missionRepository.save(mission);
        
        Candidature saved = candidatureRepository.save(candidature);
        // Notification au jeune
        notificationService.notifyCandidatureAcceptee(saved.getJeunePrestateur(), saved);
        return saved;
    }
    
    @Transactional
    public Candidature rejeterCandidature(Long candidatureId, Long recruteurId) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new IllegalArgumentException("Candidature introuvable"));
        
        // Vérifier que le recruteur est bien le propriétaire de la mission
        if (!candidature.getMission().getRecruteur().getId().equals(recruteurId)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à rejeter cette candidature");
        }
        
        // Vérifier que la candidature est en attente
        if (candidature.getStatut() != StatutCandidature.EN_ATTENTE) {
            throw new IllegalArgumentException("Cette candidature a déjà été traitée");
        }
        
        // Mettre à jour le statut de la candidature
        candidature.setStatut(StatutCandidature.REFUSEE);
        
        return candidatureRepository.save(candidature);
    }

    @Transactional(readOnly = true)
    public List<CandidatureDTO> getMissionsAccompliesByJeune(Long jeunePrestateurId) {
        JeunePrestateur jeunePrestateur = jeunePrestateurRepository.findById(jeunePrestateurId)
                .orElseThrow(() -> new IllegalArgumentException("Jeune prestateur introuvable"));
        
        return candidatureRepository.findByJeunePrestateur(jeunePrestateur).stream()
                .filter(c -> c.getStatut() == StatutCandidature.ACCEPTEE && 
                           c.getMission().getStatut() == StatutMission.TERMINEE)
                .map(this::toCandidatureDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getNombreMissionsAccompliesByJeune(Long jeunePrestateurId) {
        JeunePrestateur jeunePrestateur = jeunePrestateurRepository.findById(jeunePrestateurId)
                .orElseThrow(() -> new IllegalArgumentException("Jeune prestateur introuvable"));
        
        return candidatureRepository.findByJeunePrestateur(jeunePrestateur).stream()
                .filter(c -> c.getStatut() == StatutCandidature.ACCEPTEE && 
                           c.getMission().getStatut() == StatutMission.TERMINEE)
                .count();
    }
}
