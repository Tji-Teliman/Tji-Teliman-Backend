package com.example.Tji_Teliman.dto;

public class NotationMoyenneDTO {
    private Double moyenne;
    private Integer nombreNotations;

    public NotationMoyenneDTO() {}

    public NotationMoyenneDTO(Double moyenne, Integer nombreNotations) {
        this.moyenne = moyenne;
        this.nombreNotations = nombreNotations;
    }

    public Double getMoyenne() { return moyenne; }
    public void setMoyenne(Double moyenne) { this.moyenne = moyenne; }

    public Integer getNombreNotations() { return nombreNotations; }
    public void setNombreNotations(Integer nombreNotations) { this.nombreNotations = nombreNotations; }
}
