package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.dto.SignalementMissionDTO;
import com.example.Tji_Teliman.entites.JeunePrestateur;
import com.example.Tji_Teliman.entites.Mission;
import com.example.Tji_Teliman.entites.SignalementMission;
import com.example.Tji_Teliman.entites.enums.TypeSignalement;
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
    public SignalementMission create(Long missionId, Long jeuneId, TypeSignalement type, String description) {
        Mission mission = missionRepository.findById(missionId).orElseThrow(() -> new IllegalArgumentException("Mission introuvable"));
        JeunePrestateur jeune = jeuneRepository.findById(jeuneId).orElseThrow(() -> new IllegalArgumentException("Jeune introuvable"));

        // Empêcher les signalements en doublon pour la même mission par le même jeune
        var dejaSignales = repo.findByMissionAndJeunePrestateur(mission, jeune);
        if (dejaSignales != null && !dejaSignales.isEmpty()) {
            throw new IllegalArgumentException("Vous avez déjà signalé cette mission");
        }

        SignalementMission s = new SignalementMission();
        s.setMission(mission);
        s.setJeunePrestateur(jeune);
        s.setType(type);
        s.setDescription(description);

        return repo.save(s);
    }

    @Transactional(readOnly = true)
    public List<SignalementMission> listByMission(Long missionId) {
        Mission mission = missionRepository.findById(missionId).orElseThrow(() -> new IllegalArgumentException("Mission introuvable"));
        return repo.findByMission(mission);
    }

    @Transactional(readOnly = true)
    public List<SignalementMission> listAll() { return repo.findAll(); }

    // Suppression de la gestion de statut

    @Transactional(readOnly = true)
    public SignalementMissionDTO toDTO(SignalementMission s) {
        SignalementMissionDTO dto = new SignalementMissionDTO();
        dto.setId(s.getId());
        dto.setMotif(s.getType() == null ? null : s.getType().name());
        dto.setDescription(s.getDescription());
        dto.setDateCreation(s.getDateCreation());
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


