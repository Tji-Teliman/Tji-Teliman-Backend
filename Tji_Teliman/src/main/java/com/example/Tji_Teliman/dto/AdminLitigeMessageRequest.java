package com.example.Tji_Teliman.dto;

public class AdminLitigeMessageRequest {
    private String destinataire; // JEUNE ou RECRUTEUR
    private String contenu;

    public String getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(String destinataire) {
        this.destinataire = destinataire;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
}

