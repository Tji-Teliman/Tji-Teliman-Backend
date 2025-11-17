package com.example.Tji_Teliman.mapper;

import com.example.Tji_Teliman.dto.LitigeDTO;
import com.example.Tji_Teliman.entites.Litige;
import org.springframework.stereotype.Component;

@Component
public class LitigeMapper {

    public LitigeDTO toDto(Litige litige) {
        if (litige == null) {
            return null;
        }

        LitigeDTO dto = new LitigeDTO();
        dto.setId(litige.getId());
        dto.setTitre(litige.getTitre());
        dto.setDescription(litige.getDescription());
        dto.setDecisionAdministrateur(litige.getDecisionAdministrateur());
        dto.setDateCreation(litige.getDateCreation());
        dto.setDateResolution(litige.getDateResolution());
        dto.setDocumentUrl(litige.getDocumentUrl());

        if (litige.getType() != null) {
            dto.setType(litige.getType().name());
        }

        if (litige.getStatut() != null) {
            dto.setStatut(litige.getStatut().name());
        }

        // Mapping JeunePrestataire
        if (litige.getJeunePrestateur() != null) {
            dto.setJeunePrestateurId(litige.getJeunePrestateur().getId());
            dto.setJeunePrestateurNom(
                    litige.getJeunePrestateur().getPrenom() + " " +
                            litige.getJeunePrestateur().getNom()
            );
        }

        // Mapping Recruteur
        if (litige.getRecruteur() != null) {
            dto.setRecruteurId(litige.getRecruteur().getId());
            dto.setRecruteurNom(
                    litige.getRecruteur().getPrenom() + " " +
                            litige.getRecruteur().getNom()
            );
        }

        // Mapping Mission
        if (litige.getMission() != null) {
            dto.setMissionId(litige.getMission().getId());
            dto.setMissionTitre(litige.getMission().getTitre());
        }

        // Mapping Administrateur
        if (litige.getAdministrateur() != null) {
            dto.setAdministrateurId(litige.getAdministrateur().getId());
            dto.setAdministrateurNom(
                    litige.getAdministrateur().getPrenom() + " " +
                            litige.getAdministrateur().getNom()
            );
        }

        return dto;
    }
}