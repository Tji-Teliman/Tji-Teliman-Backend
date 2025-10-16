package com.example.Tji_Teliman.dto;

public class SystemStatsDTO {
    private long totalUtilisateurs;
    private long totalRecruteurs;
    private long totalJeunes;
    private long totalMissionsPubliees;

    public SystemStatsDTO() {}

    public SystemStatsDTO(long totalUtilisateurs, long totalRecruteurs, long totalJeunes, long totalMissionsPubliees) {
        this.totalUtilisateurs = totalUtilisateurs;
        this.totalRecruteurs = totalRecruteurs;
        this.totalJeunes = totalJeunes;
        this.totalMissionsPubliees = totalMissionsPubliees;
    }

    public long getTotalUtilisateurs() { return totalUtilisateurs; }
    public void setTotalUtilisateurs(long totalUtilisateurs) { this.totalUtilisateurs = totalUtilisateurs; }
    public long getTotalRecruteurs() { return totalRecruteurs; }
    public void setTotalRecruteurs(long totalRecruteurs) { this.totalRecruteurs = totalRecruteurs; }
    public long getTotalJeunes() { return totalJeunes; }
    public void setTotalJeunes(long totalJeunes) { this.totalJeunes = totalJeunes; }
    public long getTotalMissionsPubliees() { return totalMissionsPubliees; }
    public void setTotalMissionsPubliees(long totalMissionsPubliees) { this.totalMissionsPubliees = totalMissionsPubliees; }
}


