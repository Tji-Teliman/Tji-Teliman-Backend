package com.example.Tji_Teliman.dto;

import java.util.Date;

public class CandidatureDTO {
    private Long id;
    private String statut;
    private Date dateSoumission;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Date getDateSoumission() { return dateSoumission; }
    public void setDateSoumission(Date dateSoumission) { this.dateSoumission = dateSoumission; }
}


