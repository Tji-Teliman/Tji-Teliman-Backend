package com.example.Tji_Teliman.dto;

import java.util.Date;

public class MessageDTO {
    private Long id;
    private String contenu;
    private Date dateMessage;
    private String typeMessage;
    private String voiceFileUrl;
    private Integer voiceDuration;
    
    // Informations sur l'expéditeur
    private String expediteurNom;
    private String expediteurPrenom;
    private String expediteurPhoto;

    // Constructeurs
    public MessageDTO() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public Date getDateMessage() { return dateMessage; }
    public void setDateMessage(Date dateMessage) { this.dateMessage = dateMessage; }

    public String getTypeMessage() { return typeMessage; }
    public void setTypeMessage(String typeMessage) { this.typeMessage = typeMessage; }

    public String getVoiceFileUrl() { return voiceFileUrl; }
    public void setVoiceFileUrl(String voiceFileUrl) { this.voiceFileUrl = voiceFileUrl; }

    public Integer getVoiceDuration() { return voiceDuration; }
    public void setVoiceDuration(Integer voiceDuration) { this.voiceDuration = voiceDuration; }

    public String getExpediteurNom() { return expediteurNom; }
    public void setExpediteurNom(String expediteurNom) { this.expediteurNom = expediteurNom; }

    public String getExpediteurPrenom() { return expediteurPrenom; }
    public void setExpediteurPrenom(String expediteurPrenom) { this.expediteurPrenom = expediteurPrenom; }

    public String getExpediteurPhoto() { return expediteurPhoto; }
    public void setExpediteurPhoto(String expediteurPhoto) { this.expediteurPhoto = expediteurPhoto; }
}