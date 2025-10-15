package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.dto.MessageDTO;
import com.example.Tji_Teliman.entites.JeunePrestateur;
import com.example.Tji_Teliman.entites.Message;
import com.example.Tji_Teliman.entites.Recruteur;
import com.example.Tji_Teliman.mapper.MessageMapper;
import com.example.Tji_Teliman.repository.JeunePrestateurRepository;
import com.example.Tji_Teliman.repository.MessageRepository;
import com.example.Tji_Teliman.repository.RecruteurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RecruteurRepository recruteurRepository;

    @Autowired
    private JeunePrestateurRepository jeunePrestateurRepository;

    @Autowired
    private MessageMapper messageMapper;

    private final String UPLOAD_DIR = "uploads/vocaux/";

    // 1. Enregistrer un message vocal
    public MessageDTO enregistrerMessageVocal(Long recruteurId,
                                           Long jeunePrestateurId,
                                           boolean envoyeParRecruteur,
                                           MultipartFile file,
                                           Integer voiceDuration) {

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.write(filePath, file.getBytes());

            Recruteur recruteur = recruteurRepository.findById(recruteurId)
                    .orElseThrow(() -> new RuntimeException("Recruteur non trouvé"));
            JeunePrestateur jeune = jeunePrestateurRepository.findById(jeunePrestateurId)
                    .orElseThrow(() -> new RuntimeException("Jeune prestataire non trouvé"));

            Message message = new Message();
            message.setRecruteur(recruteur);
            message.setJeunePrestateur(jeune);
            message.setEnvoyeParRecruteur(envoyeParRecruteur);
            message.setTypeMessage(Message.MessageType.VOICE);
            message.setVoiceFileUrl("/uploads/vocaux/" + fileName);
            message.setVoiceDuration(voiceDuration);
            message.setContenu(null);
            message.setDateMessage(new Date());

            Message savedMessage = messageRepository.save(message);
            return messageMapper.toDto(savedMessage);

        } catch (IOException e) {
            throw new RuntimeException("Erreur d’enregistrement du fichier vocal", e);
        }
    }

    //  2. Enregistrer un message texte
    public MessageDTO enregistrerMessageTexte(Long recruteurId,
                                              Long jeunePrestateurId,
                                              boolean envoyeParRecruteur,
                                              String contenu) {

        Recruteur recruteur = recruteurRepository.findById(recruteurId)
                .orElseThrow(() -> new RuntimeException("Recruteur non trouvé"));
        JeunePrestateur jeune = jeunePrestateurRepository.findById(jeunePrestateurId)
                .orElseThrow(() -> new RuntimeException("Jeune prestataire non trouvé"));

        Message message = new Message();
        message.setRecruteur(recruteur);
        message.setJeunePrestateur(jeune);
        message.setEnvoyeParRecruteur(envoyeParRecruteur);
        message.setTypeMessage(Message.MessageType.TEXT);
        message.setContenu(contenu);
        message.setDateMessage(new Date());

        Message savedMessage = messageRepository.save(message);
        return messageMapper.toDto(savedMessage);
    }

    // 🔵 3. Lire tous les messages entre un recruteur et un jeune prestataire
    public List<MessageDTO> lireMessages(Long recruteurId, Long jeunePrestateurId) {
        List<Message> messages = messageRepository.findAllByRecruteurIdAndJeunePrestateurIdOrderByDateMessageAsc(
                recruteurId, jeunePrestateurId
        );

        return messages.stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }
}

