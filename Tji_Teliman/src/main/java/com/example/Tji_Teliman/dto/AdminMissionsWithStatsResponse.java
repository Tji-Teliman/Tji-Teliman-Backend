package com.example.Tji_Teliman.dto;

import java.util.List;

public class AdminMissionsWithStatsResponse {
    private List<MissionDTO> missions;
    private long totalMissionsPubliees;
    private long totalMissionsTerminees;
    private long totalMissionsNonTerminees;
    private long totalMissionsPublieesCeMoisCi;

    public AdminMissionsWithStatsResponse() {}

    public AdminMissionsWithStatsResponse(List<MissionDTO> missions, long totalMissionsPubliees, long totalMissionsTerminees, long totalMissionsNonTerminees, long totalMissionsPublieesCeMoisCi) {
        this.missions = missions;
        this.totalMissionsPubliees = totalMissionsPubliees;
        this.totalMissionsTerminees = totalMissionsTerminees;
        this.totalMissionsNonTerminees = totalMissionsNonTerminees;
        this.totalMissionsPublieesCeMoisCi = totalMissionsPublieesCeMoisCi;
    }

    public List<MissionDTO> getMissions() { return missions; }
    public void setMissions(List<MissionDTO> missions) { this.missions = missions; }
    public long getTotalMissionsPubliees() { return totalMissionsPubliees; }
    public void setTotalMissionsPubliees(long totalMissionsPubliees) { this.totalMissionsPubliees = totalMissionsPubliees; }
    public long getTotalMissionsTerminees() { return totalMissionsTerminees; }
    public void setTotalMissionsTerminees(long totalMissionsTerminees) { this.totalMissionsTerminees = totalMissionsTerminees; }
    public long getTotalMissionsNonTerminees() { return totalMissionsNonTerminees; }
    public void setTotalMissionsNonTerminees(long totalMissionsNonTerminees) { this.totalMissionsNonTerminees = totalMissionsNonTerminees; }
    public long getTotalMissionsPublieesCeMoisCi() { return totalMissionsPublieesCeMoisCi; }
    public void setTotalMissionsPublieesCeMoisCi(long totalMissionsPublieesCeMoisCi) { this.totalMissionsPublieesCeMoisCi = totalMissionsPublieesCeMoisCi; }
}


