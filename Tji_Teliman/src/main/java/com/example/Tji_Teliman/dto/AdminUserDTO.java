package com.example.Tji_Teliman.dto;

import java.util.Date;

public class AdminUserDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String role;
    private String telephone;
    private String email;
    private String genre;
    private String statut;
    private Date dateCreation;

    public AdminUserDTO() {}

    public AdminUserDTO(Long id, String nom, String prenom, String role, String telephone, String email, String genre, String statut, Date dateCreation) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.telephone = telephone;
        this.email = email;
        this.genre = genre;
        this.statut = statut;
        this.dateCreation = dateCreation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
}


