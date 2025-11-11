package com.example.Tji_Teliman.dto;

import java.util.Date;

public class NotificationDTO {
    private Long id;
    private String titre;
    private String contenu;
    private String type;
    private Date dateCreation;
    private String dateCreationRelative; // Format: "il y a 1 seconde", "il y a 30 secondes", etc.
    private boolean estLue;
    private Long destinataireId;
    private String destinataireNom;
    private String destinatairePrenom;
    
    // Informations contextuelles selon le type de notification
    // Pour CANDIDATURE_ACCEPTEE et MISSION_TERMINEE
    private String missionTitre;
    
    // Pour PAIEMENT_EFFECTUE
    private Double montantPaiement;
    private String paiementParNom;
    private String paiementParPrenom;
    
    // Pour DEMANDE_NOTATION_RECRUTEUR et DEMANDE_NOTATION_JEUNE
    private String personneANoterNom;
    private String personneANoterPrenom;
    
    // Pour NOTATION_RECUE
    private Integer noteRecue;
    private String personneQuiANoteNom;
    private String personneQuiANotePrenom;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
    public String getDateCreationRelative() { return dateCreationRelative; }
    public void setDateCreationRelative(String dateCreationRelative) { this.dateCreationRelative = dateCreationRelative; }
    public boolean isEstLue() { return estLue; }
    public void setEstLue(boolean estLue) { this.estLue = estLue; }
    public Long getDestinataireId() { return destinataireId; }
    public void setDestinataireId(Long destinataireId) { this.destinataireId = destinataireId; }
    public String getDestinataireNom() { return destinataireNom; }
    public void setDestinataireNom(String destinataireNom) { this.destinataireNom = destinataireNom; }
    public String getDestinatairePrenom() { return destinatairePrenom; }
    public void setDestinatairePrenom(String destinatairePrenom) { this.destinatairePrenom = destinatairePrenom; }
    
    // Getters et Setters pour les champs contextuels
    public String getMissionTitre() { return missionTitre; }
    public void setMissionTitre(String missionTitre) { this.missionTitre = missionTitre; }
    
    public Double getMontantPaiement() { return montantPaiement; }
    public void setMontantPaiement(Double montantPaiement) { this.montantPaiement = montantPaiement; }
    
    public String getPaiementParNom() { return paiementParNom; }
    public void setPaiementParNom(String paiementParNom) { this.paiementParNom = paiementParNom; }
    
    public String getPaiementParPrenom() { return paiementParPrenom; }
    public void setPaiementParPrenom(String paiementParPrenom) { this.paiementParPrenom = paiementParPrenom; }
    
    public String getPersonneANoterNom() { return personneANoterNom; }
    public void setPersonneANoterNom(String personneANoterNom) { this.personneANoterNom = personneANoterNom; }
    
    public String getPersonneANoterPrenom() { return personneANoterPrenom; }
    public void setPersonneANoterPrenom(String personneANoterPrenom) { this.personneANoterPrenom = personneANoterPrenom; }
    
    public Integer getNoteRecue() { return noteRecue; }
    public void setNoteRecue(Integer noteRecue) { this.noteRecue = noteRecue; }
    
    public String getPersonneQuiANoteNom() { return personneQuiANoteNom; }
    public void setPersonneQuiANoteNom(String personneQuiANoteNom) { this.personneQuiANoteNom = personneQuiANoteNom; }
    
    public String getPersonneQuiANotePrenom() { return personneQuiANotePrenom; }
    public void setPersonneQuiANotePrenom(String personneQuiANotePrenom) { this.personneQuiANotePrenom = personneQuiANotePrenom; }
}


