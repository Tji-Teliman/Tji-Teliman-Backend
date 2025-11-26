package com.example.Tji_Teliman.mapper;

import com.example.Tji_Teliman.dto.LitigeMessageDTO;
import com.example.Tji_Teliman.entites.LitigeMessage;
import com.example.Tji_Teliman.entites.enums.LitigeMessageDestinataire;
import org.springframework.stereotype.Component;

@Component
public class LitigeMessageMapper {

    public LitigeMessageDTO toDto(LitigeMessage message) {
        LitigeMessageDTO dto = new LitigeMessageDTO();
        dto.setId(message.getId());
        dto.setLitigeId(message.getLitige().getId());
        dto.setAdministrateurId(message.getAdministrateur().getId());
        dto.setAdministrateurNom(message.getAdministrateur().getNom());
        dto.setAdministrateurPrenom(message.getAdministrateur().getPrenom());
        dto.setDestinataireType(message.getDestinataireType().name());
        dto.setContenu(message.getContenu());
        dto.setDateEnvoi(message.getDateEnvoi());
        dto.setEstLu(message.isEstLu());

        if (message.getDestinataireType() == LitigeMessageDestinataire.JEUNE && message.getJeunePrestateur() != null) {
            dto.setDestinataireId(message.getJeunePrestateur().getId());
            dto.setDestinataireNom(message.getJeunePrestateur().getNom());
            dto.setDestinatairePrenom(message.getJeunePrestateur().getPrenom());
        } else if (message.getDestinataireType() == LitigeMessageDestinataire.RECRUTEUR && message.getRecruteur() != null) {
            dto.setDestinataireId(message.getRecruteur().getId());
            dto.setDestinataireNom(message.getRecruteur().getNom());
            dto.setDestinatairePrenom(message.getRecruteur().getPrenom());
        }

        return dto;
    }
}

