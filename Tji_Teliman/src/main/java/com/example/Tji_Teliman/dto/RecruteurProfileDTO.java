package com.example.Tji_Teliman.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecruteurProfileDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String role;
    private String genre;
    private String statut;
    private Date dateCreation;

    private String typeRecruteur;
    private Date dateNaissance;
    private String profession;

    private String adresse;

    @JsonProperty(required = false)
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String urlPhoto;
    
    @JsonProperty(required = false)
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String carteIdentite;

    private String nomEntreprise;
    private String secteurActivite;
    private String emailEntreprise;
    private String siteWeb;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
    public String getTypeRecruteur() { return typeRecruteur; }
    public void setTypeRecruteur(String typeRecruteur) { this.typeRecruteur = typeRecruteur; }
    public Date getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(Date dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getUrlPhoto() { return urlPhoto; }
    public void setUrlPhoto(String urlPhoto) { this.urlPhoto = urlPhoto; }
    public String getCarteIdentite() { return carteIdentite; }
    public void setCarteIdentite(String carteIdentite) { this.carteIdentite = carteIdentite; }
    public String getNomEntreprise() { return nomEntreprise; }
    public void setNomEntreprise(String nomEntreprise) { this.nomEntreprise = nomEntreprise; }
    public String getSecteurActivite() { return secteurActivite; }
    public void setSecteurActivite(String secteurActivite) { this.secteurActivite = secteurActivite; }
    public String getEmailEntreprise() { return emailEntreprise; }
    public void setEmailEntreprise(String emailEntreprise) { this.emailEntreprise = emailEntreprise; }
    public String getSiteWeb() { return siteWeb; }
    public void setSiteWeb(String siteWeb) { this.siteWeb = siteWeb; }
}
