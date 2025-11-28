package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.dto.CandidatureDTO;
import com.example.Tji_Teliman.dto.MotivationDTO;
import com.example.Tji_Teliman.dto.ProfilCandidatureDTO;
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
    private final NotationService notationService;

    public CandidatureService(
            CandidatureRepository candidatureRepository,
            MotivationRepository motivationRepository,
            JeunePrestateurRepository jeunePrestateurRepository,
            MissionRepository missionRepository,
            MissionService missionService,
            NotificationService notificationService,
            NotationService notationService) {
        this.candidatureRepository = candidatureRepository;
        this.motivationRepository = motivationRepository;
        this.jeunePrestateurRepository = jeunePrestateurRepository;
        this.missionRepository = missionRepository;
        this.missionService = missionService;
        this.notificationService = notificationService;
        this.notationService = notationService;
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
                .filter(c -> c.getMission() == null || c.getMission().getStatut() != StatutMission.ANNULEE)
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
    public List<CandidatureDTO> getCandidaturesAcceptees() {
        return candidatureRepository.findByStatut(StatutCandidature.ACCEPTEE).stream()
                .map(this::toCandidatureDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CandidatureDTO> getCandidaturesRefusees() {
        return candidatureRepository.findByStatut(StatutCandidature.REFUSEE).stream()
                .map(this::toCandidatureDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public java.util.Map<String, Long> getCountsAccepteesEtRefusees() {
        long acceptees = candidatureRepository.countByStatut(StatutCandidature.ACCEPTEE);
        long refusees = candidatureRepository.countByStatut(StatutCandidature.REFUSEE);
        java.util.Map<String, Long> map = new java.util.HashMap<>();
        map.put("acceptees", acceptees);
        map.put("refusees", refusees);
        return map;
    }
    
    @Transactional(readOnly = true)
    public List<MotivationDTO> getMotivationsByCandidature(Long candidatureId) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new IllegalArgumentException("Candidature introuvable"));
        
        return motivationRepository.findByCandidature(candidature).stream()
                .map(this::toMotivationDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MotivationDTO> getMotivationsByMission(Long missionId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("Mission introuvable"));
        return candidatureRepository.findByMission(mission).stream()
                .flatMap(c -> motivationRepository.findByCandidature(c).stream())
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
            dto.setJeunePrestateurUrlPhoto(candidature.getJeunePrestateur().getUrlPhoto());
        }
        
        // Ajouter la motivation (prendre la première motivation si elle existe)
        if (candidature.getMotivations() != null && !candidature.getMotivations().isEmpty()) {
            Motivation firstMotivation = candidature.getMotivations().iterator().next();
            dto.setMotivationContenu(firstMotivation.getContenu());
        }
        
        // Ajouter les informations de la mission
        if (candidature.getMission() != null) {
            dto.setMissionId(candidature.getMission().getId());
            dto.setMissionTitre(candidature.getMission().getTitre());
            dto.setMissionLieu(candidature.getMission().getAdresse());
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

    @Transactional(readOnly = true)
    public ProfilCandidatureDTO getProfilCandidature(Long candidatureId) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new IllegalArgumentException("Candidature introuvable"));
        
        ProfilCandidatureDTO dto = new ProfilCandidatureDTO();
        
        // Informations de la candidature
        dto.setCandidatureId(candidature.getId());
        dto.setStatutCandidature(candidature.getStatut().name());
        dto.setDateSoumission(candidature.getDateSoumission());
        
        // Informations du jeune
        JeunePrestateur jeune = candidature.getJeunePrestateur();
        if (jeune != null) {
            dto.setJeuneId(jeune.getId());
            dto.setJeuneNom(jeune.getNom());
            dto.setJeunePrenom(jeune.getPrenom());
            dto.setJeuneUrlPhoto(jeune.getUrlPhoto());
            
            // Compétences du jeune
            if (jeune.getCompetences() != null) {
                List<String> competences = jeune.getCompetences().stream()
                    .filter(jc -> jc.getCompetence() != null)
                    .map(jc -> jc.getCompetence().getNom())
                    .collect(Collectors.toList());
                dto.setCompetences(competences);
            }
            
            // Statistiques du jeune
            dto.setTotalMissionsAccomplies(getNombreMissionsAccompliesByJeune(jeune.getId()));
            
            // Moyenne des notes
            Double moyenneNotes = notationService.getMoyenneNotesJeune(jeune.getId());
            if (moyenneNotes != null) {
                dto.setMoyenneNotes(moyenneNotes);
                dto.setNombreEvaluations(notationService.getNotationsRecuesParJeune(jeune.getId()).size());
            }
        }
        
        // Motivation pour cette candidature
        if (candidature.getMotivations() != null && !candidature.getMotivations().isEmpty()) {
            Motivation motivation = candidature.getMotivations().iterator().next();
            dto.setMotivationContenu(motivation.getContenu());
            dto.setMotivationDateSoumission(motivation.getDateSoumission());
        }
        
        // Informations de la mission
        Mission mission = candidature.getMission();
        if (mission != null) {
            dto.setMissionId(mission.getId());
            dto.setMissionTitre(mission.getTitre());
            dto.setMissionDescription(mission.getDescription());
        }
        
        return dto;
    }
}
