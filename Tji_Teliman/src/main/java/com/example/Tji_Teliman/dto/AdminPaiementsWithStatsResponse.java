package com.example.Tji_Teliman.dto;

import java.util.List;

public class AdminPaiementsWithStatsResponse {
    private List<PaiementDTO> paiements;
    private long totalPaiementsEffectues; // REUSSIE
    private long totalPaiementsEnAttente; // EN_ATTENTE
    private long totalPaiementsEchoues;   // ECHEC
    private long totalPaiementsCeMoisCi;  // par datePaiement

    public AdminPaiementsWithStatsResponse() {}

    public AdminPaiementsWithStatsResponse(List<PaiementDTO> paiements, long totalPaiementsEffectues, long totalPaiementsEnAttente, long totalPaiementsEchoues, long totalPaiementsCeMoisCi) {
        this.paiements = paiements;
        this.totalPaiementsEffectues = totalPaiementsEffectues;
        this.totalPaiementsEnAttente = totalPaiementsEnAttente;
        this.totalPaiementsEchoues = totalPaiementsEchoues;
        this.totalPaiementsCeMoisCi = totalPaiementsCeMoisCi;
    }

    public List<PaiementDTO> getPaiements() { return paiements; }
    public void setPaiements(List<PaiementDTO> paiements) { this.paiements = paiements; }
    public long getTotalPaiementsEffectues() { return totalPaiementsEffectues; }
    public void setTotalPaiementsEffectues(long totalPaiementsEffectues) { this.totalPaiementsEffectues = totalPaiementsEffectues; }
    public long getTotalPaiementsEnAttente() { return totalPaiementsEnAttente; }
    public void setTotalPaiementsEnAttente(long totalPaiementsEnAttente) { this.totalPaiementsEnAttente = totalPaiementsEnAttente; }
    public long getTotalPaiementsEchoues() { return totalPaiementsEchoues; }
    public void setTotalPaiementsEchoues(long totalPaiementsEchoues) { this.totalPaiementsEchoues = totalPaiementsEchoues; }
    public long getTotalPaiementsCeMoisCi() { return totalPaiementsCeMoisCi; }
    public void setTotalPaiementsCeMoisCi(long totalPaiementsCeMoisCi) { this.totalPaiementsCeMoisCi = totalPaiementsCeMoisCi; }
}


