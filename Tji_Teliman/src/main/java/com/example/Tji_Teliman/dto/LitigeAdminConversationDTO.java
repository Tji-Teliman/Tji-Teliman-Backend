package com.example.Tji_Teliman.dto;

import java.time.LocalDateTime;

public class LitigeAdminConversationDTO {
    private Long litigeId;
    private String titreLitige;
    private String statutLitige;
    private String dernierMessage;
    private LocalDateTime dateDernierMessage;
    private int messagesNonLus;

    public Long getLitigeId() {
        return litigeId;
    }

    public void setLitigeId(Long litigeId) {
        this.litigeId = litigeId;
    }

    public String getTitreLitige() {
        return titreLitige;
    }

    public void setTitreLitige(String titreLitige) {
        this.titreLitige = titreLitige;
    }

    public String getStatutLitige() {
        return statutLitige;
    }

    public void setStatutLitige(String statutLitige) {
        this.statutLitige = statutLitige;
    }

    public String getDernierMessage() {
        return dernierMessage;
    }

    public void setDernierMessage(String dernierMessage) {
        this.dernierMessage = dernierMessage;
    }

    public LocalDateTime getDateDernierMessage() {
        return dateDernierMessage;
    }

    public void setDateDernierMessage(LocalDateTime dateDernierMessage) {
        this.dateDernierMessage = dateDernierMessage;
    }

    public int getMessagesNonLus() {
        return messagesNonLus;
    }

    public void setMessagesNonLus(int messagesNonLus) {
        this.messagesNonLus = messagesNonLus;
    }
}

