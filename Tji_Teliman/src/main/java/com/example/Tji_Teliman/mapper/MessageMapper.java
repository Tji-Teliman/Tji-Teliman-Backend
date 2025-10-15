package com.example.Tji_Teliman.mapper;

import com.example.Tji_Teliman.dto.MessageDTO;
import com.example.Tji_Teliman.entites.Message;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.time.ZoneId;

@Component
public class MessageMapper {

    public MessageDTO toDto(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setContenu(message.getContenu());

        // Conversion Date vers LocalDate
        if (message.getDateMessage() != null) {
            LocalDate localDate = message.getDateMessage().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            dto.setDateMessage(localDate);
        }

        dto.setEnvoyeParRecruteur(message.isEnvoyeParRecruteur());
        dto.setTypeMessage(message.getTypeMessage().name());
        dto.setVoiceFileUrl(message.getVoiceFileUrl());
        dto.setVoiceDuration(message.getVoiceDuration());

        // Déterminer idExpediteur et idDestinataire
        if (message.isEnvoyeParRecruteur()) {
            dto.setIdExpediteur(message.getRecruteur().getId());
            dto.setIdDestinataire(message.getJeunePrestateur().getId());
        } else {
            dto.setIdExpediteur(message.getJeunePrestateur().getId());
            dto.setIdDestinataire(message.getRecruteur().getId());
        }

        return dto;
    }
}