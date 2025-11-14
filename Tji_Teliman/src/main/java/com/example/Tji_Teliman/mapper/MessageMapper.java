package com.example.Tji_Teliman.mapper;

import com.example.Tji_Teliman.dto.MessageDTO;
import com.example.Tji_Teliman.entites.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageDTO toDto(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setContenu(message.getContenu());
        dto.setDateMessage(message.getDateMessage());
        dto.setTypeMessage(message.getTypeMessage().name());
        dto.setVoiceFileUrl(message.getVoiceFileUrl());
        dto.setVoiceDuration(message.getVoiceDuration());

        // Informations sur l'expéditeur
        if (message.isEnvoyeParRecruteur()) {
            dto.setExpediteurNom(message.getRecruteur().getNom());
            dto.setExpediteurPrenom(message.getRecruteur().getPrenom());
            dto.setExpediteurPhoto(convertToRelativePath(message.getRecruteur().getUrlPhoto()));
        } else {
            dto.setExpediteurNom(message.getJeunePrestateur().getNom());
            dto.setExpediteurPrenom(message.getJeunePrestateur().getPrenom());
            dto.setExpediteurPhoto(convertToRelativePath(message.getJeunePrestateur().getUrlPhoto()));
        }
        
        // Statut de lecture
        dto.setEstLu(message.isEstLu());

        return dto;
    }

    // Convertir le chemin absolu en chemin relatif
    private String convertToRelativePath(String absolutePath) {
        if (absolutePath == null) return null;
        
        // Remplacer les chemins absolus Windows par des chemins relatifs
        if (absolutePath.contains("uploads\\")) {
            return absolutePath.replaceAll(".*uploads\\\\", "/uploads/").replace("\\", "/");
        }
        if (absolutePath.contains("uploads/")) {
            return absolutePath.replaceAll(".*uploads/", "/uploads/");
        }
        
        return absolutePath;
    }
}