package com.example.Tji_Teliman.dto;

import java.util.Date;

public class CandidatureDTO {
    private Long id;
    private String statut;
    private Date dateSoumission;
    private Long jeunePrestateurId;
    private String jeunePrestateurNom;
    private String jeunePrestateurPrenom;
    private Long missionId;
    private String missionTitre;
    private Long recruteurId;
    private String recruteurNom;
    private String recruteurPrenom;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Date getDateSoumission() { return dateSoumission; }
    public void setDateSoumission(Date dateSoumission) { this.dateSoumission = dateSoumission; }
    public Long getJeunePrestateurId() { return jeunePrestateurId; }
    public void setJeunePrestateurId(Long jeunePrestateurId) { this.jeunePrestateurId = jeunePrestateurId; }
    public String getJeunePrestateurNom() { return jeunePrestateurNom; }
    public void setJeunePrestateurNom(String jeunePrestateurNom) { this.jeunePrestateurNom = jeunePrestateurNom; }
    public String getJeunePrestateurPrenom() { return jeunePrestateurPrenom; }
    public void setJeunePrestateurPrenom(String jeunePrestateurPrenom) { this.jeunePrestateurPrenom = jeunePrestateurPrenom; }
    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }
    public String getMissionTitre() { return missionTitre; }
    public void setMissionTitre(String missionTitre) { this.missionTitre = missionTitre; }
    public Long getRecruteurId() { return recruteurId; }
    public void setRecruteurId(Long recruteurId) { this.recruteurId = recruteurId; }
    public String getRecruteurNom() { return recruteurNom; }
    public void setRecruteurNom(String recruteurNom) { this.recruteurNom = recruteurNom; }
    public String getRecruteurPrenom() { return recruteurPrenom; }
    public void setRecruteurPrenom(String recruteurPrenom) { this.recruteurPrenom = recruteurPrenom; }
}


