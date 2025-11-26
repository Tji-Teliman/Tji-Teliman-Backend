package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.dto.LitigeAdminConversationDTO;
import com.example.Tji_Teliman.dto.LitigeMessageDTO;
import com.example.Tji_Teliman.entites.Administrateur;
import com.example.Tji_Teliman.entites.JeunePrestateur;
import com.example.Tji_Teliman.entites.Litige;
import com.example.Tji_Teliman.entites.LitigeMessage;
import com.example.Tji_Teliman.entites.Recruteur;
import com.example.Tji_Teliman.entites.enums.LitigeMessageDestinataire;
import com.example.Tji_Teliman.mapper.LitigeMessageMapper;
import com.example.Tji_Teliman.repository.AdministrateurRepository;
import com.example.Tji_Teliman.repository.LitigeMessageRepository;
import com.example.Tji_Teliman.repository.LitigeRepository;
import com.example.Tji_Teliman.repository.JeunePrestateurRepository;
import com.example.Tji_Teliman.repository.RecruteurRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class LitigeMessageService {

    private final LitigeRepository litigeRepository;
    private final AdministrateurRepository administrateurRepository;
    private final JeunePrestateurRepository jeunePrestateurRepository;
    private final RecruteurRepository recruteurRepository;
    private final LitigeMessageRepository litigeMessageRepository;
    private final LitigeMessageMapper litigeMessageMapper;

    public LitigeMessageService(LitigeRepository litigeRepository,
                                AdministrateurRepository administrateurRepository,
                                JeunePrestateurRepository jeunePrestateurRepository,
                                RecruteurRepository recruteurRepository,
                                LitigeMessageRepository litigeMessageRepository,
                                LitigeMessageMapper litigeMessageMapper) {
        this.litigeRepository = litigeRepository;
        this.administrateurRepository = administrateurRepository;
        this.jeunePrestateurRepository = jeunePrestateurRepository;
        this.recruteurRepository = recruteurRepository;
        this.litigeMessageRepository = litigeMessageRepository;
        this.litigeMessageMapper = litigeMessageMapper;
    }

    @Transactional
    public LitigeMessageDTO envoyerMessage(Long litigeId,
                                           Long adminId,
                                           LitigeMessageDestinataire destinataire,
                                           String contenu) {

        if (contenu == null || contenu.trim().isEmpty()) {
            throw new IllegalArgumentException("Le contenu du message est obligatoire");
        }

        Litige litige = litigeRepository.findById(litigeId)
                .orElseThrow(() -> new RuntimeException("Litige non trouvé"));

        Administrateur administrateur = administrateurRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));

        if (litige.getAdministrateur() == null) {
            // aucun admin encore affecté : on assigne automatiquement l'expéditeur
            litige.setAdministrateur(administrateur);
            litigeRepository.save(litige);
        }

        LitigeMessage message = new LitigeMessage();
        message.setLitige(litige);
        message.setAdministrateur(administrateur);
        message.setContenu(contenu.trim());
        message.setDestinataireType(destinataire);

        if (destinataire == LitigeMessageDestinataire.JEUNE) {
            JeunePrestateur jeune = litige.getJeunePrestateur();
            if (jeune == null) {
                throw new RuntimeException("Aucun jeune associé à ce litige");
            }
            message.setJeunePrestateur(jeune);
        } else {
            Recruteur recruteur = litige.getRecruteur();
            if (recruteur == null) {
                throw new RuntimeException("Aucun recruteur associé à ce litige");
            }
            message.setRecruteur(recruteur);
        }

        LitigeMessage saved = litigeMessageRepository.save(message);
        return litigeMessageMapper.toDto(saved);
    }

    @Transactional
    public void verifierAccesLitige(Long litigeId, Long utilisateurId) {
        Litige litige = litigeRepository.findById(litigeId)
                .orElseThrow(() -> new RuntimeException("Litige non trouvé"));

        boolean estImpliqué = (litige.getAdministrateur() != null && litige.getAdministrateur().getId().equals(utilisateurId))
                || (litige.getJeunePrestateur() != null && litige.getJeunePrestateur().getId().equals(utilisateurId))
                || (litige.getRecruteur() != null && litige.getRecruteur().getId().equals(utilisateurId));

        if (!estImpliqué) {
            throw new RuntimeException("Accès refusé à ce litige");
        }
    }

    @Transactional
    public void marquerCommeLu(Long messageId, Long utilisateurId) {
        LitigeMessage message = litigeMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));

        boolean estDestinataire = (message.getDestinataireType() == LitigeMessageDestinataire.JEUNE
                && message.getJeunePrestateur() != null
                && message.getJeunePrestateur().getId().equals(utilisateurId))
                ||
                (message.getDestinataireType() == LitigeMessageDestinataire.RECRUTEUR
                        && message.getRecruteur() != null
                        && message.getRecruteur().getId().equals(utilisateurId));

        if (!estDestinataire) {
            throw new RuntimeException("Vous ne pouvez marquer que vos messages comme lus");
        }

        if (!message.isEstLu()) {
            message.setEstLu(true);
            litigeMessageRepository.save(message);
        }
    }

    @Transactional
    public List<LitigeMessageDTO> getMessagesPourLitige(Long litigeId, Long utilisateurId) {
        verifierAccesLitige(litigeId, utilisateurId);
        List<LitigeMessage> messages = litigeMessageRepository.findByLitigeIdOrderByDateEnvoiAsc(litigeId);

        for (LitigeMessage message : messages) {
            if (estDestinataire(message, utilisateurId) && !message.isEstLu()) {
                message.setEstLu(true);
                litigeMessageRepository.save(message);
            }
        }

        return messages.stream()
                .map(litigeMessageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<LitigeAdminConversationDTO> getConversations(Long utilisateurId) {
        var type = determinerTypeUtilisateur(utilisateurId);
        List<LitigeMessage> messages;

        switch (type) {
            case ADMIN -> messages = litigeMessageRepository.findByAdministrateurIdOrderByDateEnvoiDesc(utilisateurId);
            case JEUNE -> messages = litigeMessageRepository.findByJeunePrestateurIdOrderByDateEnvoiDesc(utilisateurId);
            case RECRUTEUR -> messages = litigeMessageRepository.findByRecruteurIdOrderByDateEnvoiDesc(utilisateurId);
            default -> messages = litigeMessageRepository.findAllConversationsForUser(utilisateurId);
        }

        Map<Long, List<LitigeMessage>> messagesParLitige = new LinkedHashMap<>();
        for (LitigeMessage message : messages) {
            Long litigeId = message.getLitige().getId();
            messagesParLitige.computeIfAbsent(litigeId, id -> new java.util.ArrayList<>()).add(message);
        }

        return messagesParLitige.entrySet().stream()
                .map(entry -> {
                    List<LitigeMessage> conversationMessages = entry.getValue();
                    LitigeMessage dernierMessage = conversationMessages.stream()
                            .max((m1, m2) -> m1.getDateEnvoi().compareTo(m2.getDateEnvoi()))
                            .orElse(null);

                    LitigeAdminConversationDTO dto = new LitigeAdminConversationDTO();
                    dto.setLitigeId(entry.getKey());
                    if (dernierMessage != null) {
                        dto.setDernierMessage(dernierMessage.getContenu());
                        dto.setDateDernierMessage(dernierMessage.getDateEnvoi());
                        dto.setTitreLitige(dernierMessage.getLitige().getTitre());
                        dto.setStatutLitige(dernierMessage.getLitige().getStatut() != null ? dernierMessage.getLitige().getStatut().name() : null);
                    }

                    long nonLus = conversationMessages.stream()
                            .filter(m -> !m.isEstLu() && estDestinataire(m, utilisateurId))
                            .count();
                    dto.setMessagesNonLus((int) nonLus);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    private boolean estDestinataire(LitigeMessage message, Long utilisateurId) {
        return (message.getJeunePrestateur() != null && utilisateurId.equals(message.getJeunePrestateur().getId()))
                || (message.getRecruteur() != null && utilisateurId.equals(message.getRecruteur().getId()));
    }

    private ParticipantType determinerTypeUtilisateur(Long utilisateurId) {
        if (administrateurRepository.findById(utilisateurId).isPresent()) {
            return ParticipantType.ADMIN;
        }
        if (jeunePrestateurRepository.findById(utilisateurId).isPresent()) {
            return ParticipantType.JEUNE;
        }
        if (recruteurRepository.findById(utilisateurId).isPresent()) {
            return ParticipantType.RECRUTEUR;
        }
        throw new RuntimeException("Utilisateur introuvable");
    }

    private enum ParticipantType {
        ADMIN,
        JEUNE,
        RECRUTEUR
    }
}

