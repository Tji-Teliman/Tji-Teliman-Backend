package com.example.Tji_Teliman.dto;

import java.util.Date;

public class NotationDTO {
    private Long id;
    private Integer note;
    private String commentaire;
    private Date dateNotation;
    private boolean initieParRecruteur;
    
    // Informations sur la candidature et mission
    private Long candidatureId;
    private Long missionId;
    private String missionTitre;
    
    // Informations sur le recruteur
    private Long recruteurId;
    private String recruteurNom;
    private String recruteurPrenom;
    
    // Informations sur le jeune prestataire
    private Long jeunePrestateurId;
    private String jeunePrestateurNom;
    private String jeunePrestateurPrenom;

    // Constructeurs
    public NotationDTO() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public Date getDateNotation() { return dateNotation; }
    public void setDateNotation(Date dateNotation) { this.dateNotation = dateNotation; }

    public boolean isInitieParRecruteur() { return initieParRecruteur; }
    public void setInitieParRecruteur(boolean initieParRecruteur) { this.initieParRecruteur = initieParRecruteur; }

    public Long getCandidatureId() { return candidatureId; }
    public void setCandidatureId(Long candidatureId) { this.candidatureId = candidatureId; }

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

    public Long getJeunePrestateurId() { return jeunePrestateurId; }
    public void setJeunePrestateurId(Long jeunePrestateurId) { this.jeunePrestateurId = jeunePrestateurId; }

    public String getJeunePrestateurNom() { return jeunePrestateurNom; }
    public void setJeunePrestateurNom(String jeunePrestateurNom) { this.jeunePrestateurNom = jeunePrestateurNom; }

    public String getJeunePrestateurPrenom() { return jeunePrestateurPrenom; }
    public void setJeunePrestateurPrenom(String jeunePrestateurPrenom) { this.jeunePrestateurPrenom = jeunePrestateurPrenom; }
}


