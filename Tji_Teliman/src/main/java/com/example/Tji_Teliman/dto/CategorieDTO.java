package com.example.Tji_Teliman.dto;

public class CategorieDTO {
    private Long id;
    private String nom;
    private String urlPhoto;
    private Integer missionsCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getUrlPhoto() { return urlPhoto; }
    public void setUrlPhoto(String urlPhoto) { this.urlPhoto = urlPhoto; }
    public Integer getMissionsCount() { return missionsCount; }
    public void setMissionsCount(Integer missionsCount) { this.missionsCount = missionsCount; }
}


