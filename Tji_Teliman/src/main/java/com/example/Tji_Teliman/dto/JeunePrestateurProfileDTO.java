package com.example.Tji_Teliman.dto;

import java.util.Date;
import java.util.List;

public class JeunePrestateurProfileDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String role;
    private String genre;
    private Date dateCreation;

    private Date dateNaissance;
    private String localisation;
    private String urlPhoto;
    private String carteIdentite;

    private List<String> competences; // only names
    private List<MessageDTO> messages;
    private List<NotationDTO> notations;
    private List<CandidatureDTO> candidatures;

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
    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
    public Date getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(Date dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public String getUrlPhoto() { return urlPhoto; }
    public void setUrlPhoto(String urlPhoto) { this.urlPhoto = urlPhoto; }
    public String getCarteIdentite() { return carteIdentite; }
    public void setCarteIdentite(String carteIdentite) { this.carteIdentite = carteIdentite; }
    public List<String> getCompetences() { return competences; }
    public void setCompetences(List<String> competences) { this.competences = competences; }
    public List<MessageDTO> getMessages() { return messages; }
    public void setMessages(List<MessageDTO> messages) { this.messages = messages; }
    public List<NotationDTO> getNotations() { return notations; }
    public void setNotations(List<NotationDTO> notations) { this.notations = notations; }
    public List<CandidatureDTO> getCandidatures() { return candidatures; }
    public void setCandidatures(List<CandidatureDTO> candidatures) { this.candidatures = candidatures; }
}


