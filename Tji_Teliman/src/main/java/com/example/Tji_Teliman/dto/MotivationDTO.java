package com.example.Tji_Teliman.dto;

import java.util.Date;

public class MotivationDTO {
    private Long id;
    private String contenu;
    private Date dateSoumission;
    private Long candidatureId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public Date getDateSoumission() { return dateSoumission; }
    public void setDateSoumission(Date dateSoumission) { this.dateSoumission = dateSoumission; }
    public Long getCandidatureId() { return candidatureId; }
    public void setCandidatureId(Long candidatureId) { this.candidatureId = candidatureId; }
}