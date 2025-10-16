package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.dto.SignalementMissionDTO;
import com.example.Tji_Teliman.entites.JeunePrestateur;
import com.example.Tji_Teliman.entites.Mission;
import com.example.Tji_Teliman.entites.SignalementMission;
import com.example.Tji_Teliman.entites.enums.StatutSignalement;
import com.example.Tji_Teliman.repository.JeunePrestateurRepository;
import com.example.Tji_Teliman.repository.MissionRepository;
import com.example.Tji_Teliman.repository.SignalementMissionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignalementMissionService {

    private final SignalementMissionRepository repo;
    private final MissionRepository missionRepository;
    private final JeunePrestateurRepository jeuneRepository;

    public SignalementMissionService(SignalementMissionRepository repo, MissionRepository missionRepository, JeunePrestateurRepository jeuneRepository) {
        this.repo = repo;
        this.missionRepository = missionRepository;
        this.jeuneRepository = jeuneRepository;
    }

    @Transactional
    public SignalementMission create(Long missionId, Long jeuneId, String motif, String description, String pieceJointe) {
        Mission mission = missionRepository.findById(missionId).orElseThrow(() -> new IllegalArgumentException("Mission introuvable"));
        JeunePrestateur jeune = jeuneRepository.findById(jeuneId).orElseThrow(() -> new IllegalArgumentException("Jeune introuvable"));

        SignalementMission s = new SignalementMission();
        s.setMission(mission);
        s.setJeunePrestateur(jeune);
        s.setMotif(motif);
        s.setDescription(description);
        s.setPieceJointe(pieceJointe);
        s.setStatut(StatutSignalement.OUVERT);

        return repo.save(s);
    }

    @Transactional(readOnly = true)
    public List<SignalementMission> listByMission(Long missionId) {
        Mission mission = missionRepository.findById(missionId).orElseThrow(() -> new IllegalArgumentException("Mission introuvable"));
        return repo.findByMission(mission);
    }

    @Transactional(readOnly = true)
    public List<SignalementMission> listAll() { return repo.findAll(); }

    @Transactional
    public SignalementMission updateStatut(Long id, StatutSignalement statut) {
        SignalementMission s = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Signalement introuvable"));
        s.setStatut(statut);
        return repo.save(s);
    }

    @Transactional(readOnly = true)
    public SignalementMissionDTO toDTO(SignalementMission s) {
        SignalementMissionDTO dto = new SignalementMissionDTO();
        dto.setId(s.getId());
        dto.setMotif(s.getMotif());
        dto.setDescription(s.getDescription());
        dto.setStatut(s.getStatut() == null ? null : s.getStatut().name());
        dto.setDateCreation(s.getDateCreation());
        dto.setPieceJointe(s.getPieceJointe());
        if (s.getMission() != null) {
            dto.setMissionId(s.getMission().getId());
            dto.setMissionTitre(s.getMission().getTitre());
        }
        if (s.getJeunePrestateur() != null) {
            dto.setJeuneId(s.getJeunePrestateur().getId());
            dto.setJeuneNom(s.getJeunePrestateur().getNom());
            dto.setJeunePrenom(s.getJeunePrestateur().getPrenom());
        }
        return dto;
    }
}


