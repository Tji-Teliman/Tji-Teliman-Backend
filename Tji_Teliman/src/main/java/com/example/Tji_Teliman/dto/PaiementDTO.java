package com.example.Tji_Teliman.dto;

import java.util.Date;

public class PaiementDTO {
    private Long id;
    private Double montant;
    private Date datePaiement;
    private String statutPaiement;
    private Long candidatureId;
    private Long recruteurId;
    private String recruteurNom;
    private String recruteurPrenom;
    private Long jeunePrestateurId;
    private String jeunePrestateurNom;
    private String jeunePrestateurPrenom;
    private Long missionId;
    private String missionTitre;
    private Double missionRemuneration;

    // Constructeurs
    public PaiementDTO() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public Date getDatePaiement() { return datePaiement; }
    public void setDatePaiement(Date datePaiement) { this.datePaiement = datePaiement; }

    public String getStatutPaiement() { return statutPaiement; }
    public void setStatutPaiement(String statutPaiement) { this.statutPaiement = statutPaiement; }

    public Long getCandidatureId() { return candidatureId; }
    public void setCandidatureId(Long candidatureId) { this.candidatureId = candidatureId; }

    public Long getRecruteurId() { return recruteurId; }
    public void setRecruteurId(Long recruteurId) { this.recruteurId = recruteurId; }

    public String getRecruteurNom() { return recruteurNom; }
    public void setRecruteurNom(String recruteurNom) { this.recruteurNom = recruteurNom; }

    public String getRecruteurPrenom() { return recruteurPrenom; }
    public void setRecruteurPrenom(String recruteurPrenom) { this.recruteurPrenom = recruteurPrenom; }

    public Long getJeunePrestateurId() { return jeunePrestateurId; }
    public void setJeunePrestateurId(Long jeunePrestateurId) { this.jeunePrestateurId = jeunePrestateurId; }

    public String getJeunePrestateurNom() { return jeunePrestateurNom; }
    public void setJeunePrestateurNom(String jeunePrestateurNom) { this.jeunePrestateurNom = jeunePrestateurNom; }

    public String getJeunePrestateurPrenom() { return jeunePrestateurPrenom; }
    public void setJeunePrestateurPrenom(String jeunePrestateurPrenom) { this.jeunePrestateurPrenom = jeunePrestateurPrenom; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public String getMissionTitre() { return missionTitre; }
    public void setMissionTitre(String missionTitre) { this.missionTitre = missionTitre; }

    public Double getMissionRemuneration() { return missionRemuneration; }
    public void setMissionRemuneration(Double missionRemuneration) { this.missionRemuneration = missionRemuneration; }
}
