package com.example.Tji_Teliman.dto;

import java.time.LocalDate;
import java.util.Date;

public class MessageDTO {
    private Long id;
    private String contenu;
    private LocalDate dateMessage; // ou Date selon votre besoin
    private boolean envoyeParRecruteur;
    private String typeMessage;
    private Long idExpediteur;
    private Long idDestinataire;
    private boolean textMessage;
    private boolean voiceMessage;
    private String voiceFileUrl;
    private Integer voiceDuration;

    // Constructeurs
    public MessageDTO() {}

    public MessageDTO(Long id, String contenu, LocalDate dateMessage, boolean envoyeParRecruteur,
                      String typeMessage, Long idExpediteur, Long idDestinataire) {
        this.id = id;
        this.contenu = contenu;
        this.dateMessage = dateMessage;
        this.envoyeParRecruteur = envoyeParRecruteur;
        this.typeMessage = typeMessage;
        this.idExpediteur = idExpediteur;
        this.idDestinataire = idDestinataire;
        this.textMessage = "TEXT".equals(typeMessage);
        this.voiceMessage = "VOICE".equals(typeMessage);
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDate getDateMessage() { return dateMessage; }
    public void setDateMessage(LocalDate dateMessage) { this.dateMessage = dateMessage; }

    public boolean isEnvoyeParRecruteur() { return envoyeParRecruteur; }
    public void setEnvoyeParRecruteur(boolean envoyeParRecruteur) { this.envoyeParRecruteur = envoyeParRecruteur; }

    public String getTypeMessage() { return typeMessage; }
    public void setTypeMessage(String typeMessage) {
        this.typeMessage = typeMessage;
        this.textMessage = "TEXT".equals(typeMessage);
        this.voiceMessage = "VOICE".equals(typeMessage);
    }

    public Long getIdExpediteur() { return idExpediteur; }
    public void setIdExpediteur(Long idExpediteur) { this.idExpediteur = idExpediteur; }

    public Long getIdDestinataire() { return idDestinataire; }
    public void setIdDestinataire(Long idDestinataire) { this.idDestinataire = idDestinataire; }

    public boolean isTextMessage() { return textMessage; }
    public void setTextMessage(boolean textMessage) { this.textMessage = textMessage; }

    public boolean isVoiceMessage() { return voiceMessage; }
    public void setVoiceMessage(boolean voiceMessage) { this.voiceMessage = voiceMessage; }

    public String getVoiceFileUrl() { return voiceFileUrl; }
    public void setVoiceFileUrl(String voiceFileUrl) { this.voiceFileUrl = voiceFileUrl; }

    public Integer getVoiceDuration() { return voiceDuration; }
    public void setVoiceDuration(Integer voiceDuration) { this.voiceDuration = voiceDuration; }
}