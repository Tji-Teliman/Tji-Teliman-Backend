package com.example.Tji_Teliman.dto;

import java.util.Date;

public class SignalementMissionDTO {
    private Long id;
    private Long missionId;
    private String missionTitre;
    private Long jeuneId;
    private String jeuneNom;
    private String jeunePrenom;
    private String motif;
    private String description;
    private Date dateCreation;
   

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }
    public String getMissionTitre() { return missionTitre; }
    public void setMissionTitre(String missionTitre) { this.missionTitre = missionTitre; }
    public Long getJeuneId() { return jeuneId; }
    public void setJeuneId(Long jeuneId) { this.jeuneId = jeuneId; }
    public String getJeuneNom() { return jeuneNom; }
    public void setJeuneNom(String jeuneNom) { this.jeuneNom = jeuneNom; }
    public String getJeunePrenom() { return jeunePrenom; }
    public void setJeunePrenom(String jeunePrenom) { this.jeunePrenom = jeunePrenom; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
}


