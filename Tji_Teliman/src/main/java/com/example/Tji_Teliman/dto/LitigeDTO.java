package com.example.Tji_Teliman.dto;

import java.time.LocalDateTime;

public class LitigeDTO {
    private Long id;
    private String titre;
    private String description;
    private String type;
    private String statut;
    private Long jeunePrestateurId;
    private String jeunePrestateurNom;
    private String jeunePrestateurEmail;
    private String jeunePrestateurPhotoUrl;

    private Long recruteurId;
    private String recruteurNom;
    private String recruteurEmail;
    private String recruteurPhotoUrl;

    private Long missionId;
    private String missionTitre;
    private Long administrateurId;
    private String administrateurNom;
    private String decisionAdministrateur;
    private LocalDateTime dateCreation;
    private LocalDateTime dateResolution;
    private String documentUrl;


    // Constructeurs
    public LitigeDTO() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Long getJeunePrestateurId() { return jeunePrestateurId; }
    public void setJeunePrestateurId(Long jeunePrestateurId) { this.jeunePrestateurId = jeunePrestateurId; }

    public String getJeunePrestateurNom() { return jeunePrestateurNom; }
    public void setJeunePrestateurNom(String jeunePrestateurNom) { this.jeunePrestateurNom = jeunePrestateurNom; }

    public Long getRecruteurId() { return recruteurId; }
    public void setRecruteurId(Long recruteurId) { this.recruteurId = recruteurId; }

    public String getRecruteurNom() { return recruteurNom; }
    public void setRecruteurNom(String recruteurNom) { this.recruteurNom = recruteurNom; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public String getMissionTitre() { return missionTitre; }
    public void setMissionTitre(String missionTitre) { this.missionTitre = missionTitre; }

    public Long getAdministrateurId() { return administrateurId; }
    public void setAdministrateurId(Long administrateurId) { this.administrateurId = administrateurId; }

    public String getAdministrateurNom() { return administrateurNom; }
    public void setAdministrateurNom(String administrateurNom) { this.administrateurNom = administrateurNom; }

    public String getDecisionAdministrateur() { return decisionAdministrateur; }
    public void setDecisionAdministrateur(String decisionAdministrateur) { this.decisionAdministrateur = decisionAdministrateur; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateResolution() { return dateResolution; }
    public void setDateResolution(LocalDateTime dateResolution) { this.dateResolution = dateResolution; }

    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }

    public String getJeunePrestateurEmail() {
        return jeunePrestateurEmail;
    }

    public void setJeunePrestateurEmail(String jeunePrestateurEmail) {
        this.jeunePrestateurEmail = jeunePrestateurEmail;
    }

    public String getJeunePrestateurPhotoUrl() {
        return jeunePrestateurPhotoUrl;
    }

    public void setJeunePrestateurPhotoUrl(String jeunePrestateurPhotoUrl) {
        this.jeunePrestateurPhotoUrl = jeunePrestateurPhotoUrl;
    }

    public String getRecruteurEmail() {
        return recruteurEmail;
    }

    public void setRecruteurEmail(String recruteurEmail) {
        this.recruteurEmail = recruteurEmail;
    }

    public String getRecruteurPhotoUrl() {
        return recruteurPhotoUrl;
    }

    public void setRecruteurPhotoUrl(String recruteurPhotoUrl) {
        this.recruteurPhotoUrl = recruteurPhotoUrl;
    }
}