package com.example.Tji_Teliman.dto;

import java.util.Date;

public class ConversationDTO {
    private Long destinataireId;
    private String destinataireNom;
    private String destinatairePrenom;
    private String destinatairePhoto;
    private String dernierMessage;
    private Date dateDernierMessage;
    private int messagesNonLus;
    private String typeDernierMessage;

    // Constructeurs
    public ConversationDTO() {}

    // Getters et Setters
    public Long getDestinataireId() { return destinataireId; }
    public void setDestinataireId(Long destinataireId) { this.destinataireId = destinataireId; }

    public String getDestinataireNom() { return destinataireNom; }
    public void setDestinataireNom(String destinataireNom) { this.destinataireNom = destinataireNom; }

    public String getDestinatairePrenom() { return destinatairePrenom; }
    public void setDestinatairePrenom(String destinatairePrenom) { this.destinatairePrenom = destinatairePrenom; }

    public String getDestinatairePhoto() { return destinatairePhoto; }
    public void setDestinatairePhoto(String destinatairePhoto) { this.destinatairePhoto = destinatairePhoto; }

    public String getDernierMessage() { return dernierMessage; }
    public void setDernierMessage(String dernierMessage) { this.dernierMessage = dernierMessage; }

    public Date getDateDernierMessage() { return dateDernierMessage; }
    public void setDateDernierMessage(Date dateDernierMessage) { this.dateDernierMessage = dateDernierMessage; }

    public int getMessagesNonLus() { return messagesNonLus; }
    public void setMessagesNonLus(int messagesNonLus) { this.messagesNonLus = messagesNonLus; }

    public String getTypeDernierMessage() { return typeDernierMessage; }
    public void setTypeDernierMessage(String typeDernierMessage) { this.typeDernierMessage = typeDernierMessage; }
}
