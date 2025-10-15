package com.example.Tji_Teliman.dto;

import java.util.List;

public class AdminUsersWithStatsResponse {
    private List<AdminUserDTO> utilisateurs;
    private long totalUtilisateurs;
    private long totalJeunes;
    private long totalRecruteurs;

    public AdminUsersWithStatsResponse() {}

    public AdminUsersWithStatsResponse(List<AdminUserDTO> utilisateurs, long totalUtilisateurs, long totalJeunes, long totalRecruteurs) {
        this.utilisateurs = utilisateurs;
        this.totalUtilisateurs = totalUtilisateurs;
        this.totalJeunes = totalJeunes;
        this.totalRecruteurs = totalRecruteurs;
    }

    public List<AdminUserDTO> getUtilisateurs() { return utilisateurs; }
    public void setUtilisateurs(List<AdminUserDTO> utilisateurs) { this.utilisateurs = utilisateurs; }
    public long getTotalUtilisateurs() { return totalUtilisateurs; }
    public void setTotalUtilisateurs(long totalUtilisateurs) { this.totalUtilisateurs = totalUtilisateurs; }
    public long getTotalJeunes() { return totalJeunes; }
    public void setTotalJeunes(long totalJeunes) { this.totalJeunes = totalJeunes; }
    public long getTotalRecruteurs() { return totalRecruteurs; }
    public void setTotalRecruteurs(long totalRecruteurs) { this.totalRecruteurs = totalRecruteurs; }
}


