package com.example.Tji_Teliman.dto;

import java.util.Date;

public class MissionDTO {
    private Long id;
    private String titre;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private String localisation;
    private Double remuneration;
    private Date datePublication;
    private String statut;
    private String categorieNom;
    private String categorieUrlPhoto;
    private Long recruteurId;
    private String recruteurNom;
    private String recruteurPrenom;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public Double getRemuneration() { return remuneration; }
    public void setRemuneration(Double remuneration) { this.remuneration = remuneration; }
    public Date getDatePublication() { return datePublication; }
    public void setDatePublication(Date datePublication) { this.datePublication = datePublication; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getCategorieNom() { return categorieNom; }
    public void setCategorieNom(String categorieNom) { this.categorieNom = categorieNom; }
    public String getCategorieUrlPhoto() { return categorieUrlPhoto; }
    public void setCategorieUrlPhoto(String categorieUrlPhoto) { this.categorieUrlPhoto = categorieUrlPhoto; }
    public Long getRecruteurId() { return recruteurId; }
    public void setRecruteurId(Long recruteurId) { this.recruteurId = recruteurId; }
    public String getRecruteurNom() { return recruteurNom; }
    public void setRecruteurNom(String recruteurNom) { this.recruteurNom = recruteurNom; }
    public String getRecruteurPrenom() { return recruteurPrenom; }
    public void setRecruteurPrenom(String recruteurPrenom) { this.recruteurPrenom = recruteurPrenom; }
}


