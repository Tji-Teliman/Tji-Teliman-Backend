package com.example.Tji_Teliman.dto;

import java.util.Date;

public class MessageDTO {
    private Long id;
    private String contenu;
    private Date dateMessage;
    private boolean envoyeParRecruteur;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public Date getDateMessage() { return dateMessage; }
    public void setDateMessage(Date dateMessage) { this.dateMessage = dateMessage; }
    public boolean isEnvoyeParRecruteur() { return envoyeParRecruteur; }
    public void setEnvoyeParRecruteur(boolean envoyeParRecruteur) { this.envoyeParRecruteur = envoyeParRecruteur; }
}


