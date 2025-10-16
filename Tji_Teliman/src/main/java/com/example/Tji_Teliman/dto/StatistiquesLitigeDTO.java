package com.example.Tji_Teliman.dto;


public class StatistiquesLitigeDTO {
    private long totalLitiges;
    private long litigesOuverts;
    private long litigesEnCours;
    private long litigesResolus;
    private long litigesNonAssignes;

    // Getters et Setters
    public long getTotalLitiges() { return totalLitiges; }
    public void setTotalLitiges(long totalLitiges) { this.totalLitiges = totalLitiges; }

    public long getLitigesOuverts() { return litigesOuverts; }
    public void setLitigesOuverts(long litigesOuverts) { this.litigesOuverts = litigesOuverts; }

    public long getLitigesEnCours() { return litigesEnCours; }
    public void setLitigesEnCours(long litigesEnCours) { this.litigesEnCours = litigesEnCours; }

    public long getLitigesResolus() { return litigesResolus; }
    public void setLitigesResolus(long litigesResolus) { this.litigesResolus = litigesResolus; }

    public long getLitigesNonAssignes() { return litigesNonAssignes; }
    public void setLitigesNonAssignes(long litigesNonAssignes) { this.litigesNonAssignes = litigesNonAssignes; }
}