package com.example.Tji_Teliman.dto;

import java.time.LocalDateTime;

public class LitigeMessageDTO {
    private Long id;
    private Long litigeId;
    private Long administrateurId;
    private String administrateurNom;
    private String administrateurPrenom;
    private String destinataireType;
    private Long destinataireId;
    private String destinataireNom;
    private String destinatairePrenom;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private boolean estLu;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLitigeId() {
        return litigeId;
    }

    public void setLitigeId(Long litigeId) {
        this.litigeId = litigeId;
    }

    public Long getAdministrateurId() {
        return administrateurId;
    }

    public void setAdministrateurId(Long administrateurId) {
        this.administrateurId = administrateurId;
    }

    public String getAdministrateurNom() {
        return administrateurNom;
    }

    public void setAdministrateurNom(String administrateurNom) {
        this.administrateurNom = administrateurNom;
    }

    public String getAdministrateurPrenom() {
        return administrateurPrenom;
    }

    public void setAdministrateurPrenom(String administrateurPrenom) {
        this.administrateurPrenom = administrateurPrenom;
    }

    public String getDestinataireType() {
        return destinataireType;
    }

    public void setDestinataireType(String destinataireType) {
        this.destinataireType = destinataireType;
    }

    public Long getDestinataireId() {
        return destinataireId;
    }

    public void setDestinataireId(Long destinataireId) {
        this.destinataireId = destinataireId;
    }

    public String getDestinataireNom() {
        return destinataireNom;
    }

    public void setDestinataireNom(String destinataireNom) {
        this.destinataireNom = destinataireNom;
    }

    public String getDestinatairePrenom() {
        return destinatairePrenom;
    }

    public void setDestinatairePrenom(String destinatairePrenom) {
        this.destinatairePrenom = destinatairePrenom;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public boolean isEstLu() {
        return estLu;
    }

    public void setEstLu(boolean estLu) {
        this.estLu = estLu;
    }
}

