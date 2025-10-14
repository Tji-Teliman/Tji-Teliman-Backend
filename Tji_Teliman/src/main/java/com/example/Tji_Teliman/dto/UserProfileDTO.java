package com.example.Tji_Teliman.dto;

import java.util.List;

public class UserProfileDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String role;
    private String email;
    private String telephone;
    private String localisation;
    private List<String> competences;
    private Long nombreMissionsAccomplies;
    private Long nombreMissionsPubliees;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public List<String> getCompetences() { return competences; }
    public void setCompetences(List<String> competences) { this.competences = competences; }
    public Long getNombreMissionsAccomplies() { return nombreMissionsAccomplies; }
    public void setNombreMissionsAccomplies(Long nombreMissionsAccomplies) { this.nombreMissionsAccomplies = nombreMissionsAccomplies; }
    public Long getNombreMissionsPubliees() { return nombreMissionsPubliees; }
    public void setNombreMissionsPubliees(Long nombreMissionsPubliees) { this.nombreMissionsPubliees = nombreMissionsPubliees; }
}


