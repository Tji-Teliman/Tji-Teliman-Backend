package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.dto.MessageDTO;
import com.example.Tji_Teliman.dto.ConversationDTO;
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
import org.springframework.transaction.annotation.Transactional;

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

    // 🔵 4. Supprimer un message
    @Transactional
    public void supprimerMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));

        // Vérifier que l'utilisateur est l'expéditeur du message
        Long expediteurId = message.getIdExpediteur();
        if (!expediteurId.equals(userId)) {
            throw new RuntimeException("Vous ne pouvez supprimer que vos propres messages");
        }

        messageRepository.delete(message);
    }

    // 🔵 6. Obtenir les conversations d'un utilisateur
    @Transactional(readOnly = true)
    public List<ConversationDTO> getConversations(Long userId) {
        // Récupérer toutes les conversations où l'utilisateur est impliqué
        List<Message> messages = messageRepository.findConversationsByUserId(userId);
        
        // Grouper par l'autre personne dans la conversation (pas l'utilisateur connecté)
        return messages.stream()
                .collect(Collectors.groupingBy(msg -> {
                    // Déterminer l'ID de l'autre personne dans la conversation par rapport à l'utilisateur connecté
                    Long recruteurId = msg.getRecruteur().getId();
                    Long jeuneId = msg.getJeunePrestateur().getId();
                    
                    // Si l'utilisateur connecté est le recruteur, l'autre personne est le jeune
                    if (recruteurId.equals(userId)) {
                        return jeuneId;
                    } else {
                        // Si l'utilisateur connecté est le jeune, l'autre personne est le recruteur
                        return recruteurId;
                    }
                }))
                .entrySet().stream()
                .filter(entry -> {
                    // Filtrer pour éviter les conversations avec soi-même (ne devrait pas arriver, mais sécurité)
                    Long autrePersonneId = entry.getKey();
                    return !autrePersonneId.equals(userId);
                })
                .map(entry -> {
                    Long autrePersonneId = entry.getKey();
                    List<Message> conversationMessages = entry.getValue();
                    
                    // Trouver le dernier message
                    Message dernierMessage = conversationMessages.stream()
                            .max((m1, m2) -> m1.getDateMessage().compareTo(m2.getDateMessage()))
                            .orElse(null);
                    
                    ConversationDTO conversation = new ConversationDTO();
                    conversation.setDestinataireId(autrePersonneId);
                    conversation.setDernierMessage(dernierMessage != null ? dernierMessage.getContenu() : "");
                    conversation.setDateDernierMessage(dernierMessage != null ? dernierMessage.getDateMessage() : null);
                    conversation.setMessagesNonLus(0); // Pas de gestion des messages non lus pour le moment
                    conversation.setTypeDernierMessage(dernierMessage != null ? dernierMessage.getTypeMessage().toString() : "");
                    
                    // Récupérer les informations de l'autre personne dans la conversation
                    if (dernierMessage != null) {
                        // Déterminer qui est l'autre personne par rapport à l'utilisateur connecté
                        if (dernierMessage.getRecruteur().getId().equals(userId)) {
                            // L'utilisateur connecté est le recruteur, l'autre personne est le jeune
                            conversation.setDestinataireNom(dernierMessage.getJeunePrestateur().getNom());
                            conversation.setDestinatairePrenom(dernierMessage.getJeunePrestateur().getPrenom());
                            conversation.setDestinatairePhoto(dernierMessage.getJeunePrestateur().getUrlPhoto());
                        } else {
                            // L'utilisateur connecté est le jeune, l'autre personne est le recruteur
                            conversation.setDestinataireNom(dernierMessage.getRecruteur().getNom());
                            conversation.setDestinatairePrenom(dernierMessage.getRecruteur().getPrenom());
                            conversation.setDestinatairePhoto(dernierMessage.getRecruteur().getUrlPhoto());
                        }
                    }
                    
                    return conversation;
                })
                .collect(Collectors.toList());
    }

    // 🔵 7. Obtenir un message par ID
    @Transactional(readOnly = true)
    public MessageDTO getMessageById(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));
        return messageMapper.toDto(message);
    }
}

