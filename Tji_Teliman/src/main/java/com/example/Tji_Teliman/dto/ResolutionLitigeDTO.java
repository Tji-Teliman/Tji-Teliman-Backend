package com.example.Tji_Teliman.dto;

public class ResolutionLitigeDTO {
    private String decisionAdministrateur;
    private String statut;
    private Long administrateurId;

    // Constructeurs
    public ResolutionLitigeDTO() {}

    // Getters et Setters
    public String getDecisionAdministrateur() { return decisionAdministrateur; }
    public void setDecisionAdministrateur(String decisionAdministrateur) { this.decisionAdministrateur = decisionAdministrateur; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Long getAdministrateurId() { return administrateurId; }
    public void setAdministrateurId(Long administrateurId) { this.administrateurId = administrateurId; }
}