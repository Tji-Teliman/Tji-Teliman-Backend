package com.example.Tji_Teliman.dto;


public class CreationLitigeDTO {
    private String titre;
    private String description;
    private String type;
    private Long jeunePrestateurId;
    private Long recruteurId;
    private Long missionId;

    // Constructeurs
    public CreationLitigeDTO() {}

    // Getters et Setters
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getJeunePrestateurId() { return jeunePrestateurId; }
    public void setJeunePrestateurId(Long jeunePrestateurId) { this.jeunePrestateurId = jeunePrestateurId; }

    public Long getRecruteurId() { return recruteurId; }
    public void setRecruteurId(Long recruteurId) { this.recruteurId = recruteurId; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }
}