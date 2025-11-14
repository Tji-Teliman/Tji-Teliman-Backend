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
    private Long interlocuteurId; // Id utilisé côté chat (ex: recruteur à contacter)
    private Long recruteurId;
    private String recruteurNom;
    private String recruteurPrenom;
    private String recruteurPhoto;
    
    // Informations contextuelles selon le type de notification
    // Pour CANDIDATURE_ACCEPTEE et MISSION_TERMINEE
    private Long missionId;
    private String missionTitre;
    private Long candidatureId;
    private java.util.Date missionDateFin;
    private Double missionRemuneration;
    
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
    
    // Pour DEMANDE_NOTATION_* : indiquer si la notation a déjà été faite
    private boolean notationDejaFaite;

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
    public Long getInterlocuteurId() { return interlocuteurId; }
    public void setInterlocuteurId(Long interlocuteurId) { this.interlocuteurId = interlocuteurId; }
    public Long getRecruteurId() { return recruteurId; }
    public void setRecruteurId(Long recruteurId) { this.recruteurId = recruteurId; }
    public String getRecruteurNom() { return recruteurNom; }
    public void setRecruteurNom(String recruteurNom) { this.recruteurNom = recruteurNom; }
    public String getRecruteurPrenom() { return recruteurPrenom; }
    public void setRecruteurPrenom(String recruteurPrenom) { this.recruteurPrenom = recruteurPrenom; }
    public String getRecruteurPhoto() { return recruteurPhoto; }
    public void setRecruteurPhoto(String recruteurPhoto) { this.recruteurPhoto = recruteurPhoto; }
    
    // Getters et Setters pour les champs contextuels
    public String getMissionTitre() { return missionTitre; }
    public void setMissionTitre(String missionTitre) { this.missionTitre = missionTitre; }
    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }
    
    public Long getCandidatureId() { return candidatureId; }
    public void setCandidatureId(Long candidatureId) { this.candidatureId = candidatureId; }
    
    public java.util.Date getMissionDateFin() { return missionDateFin; }
    public void setMissionDateFin(java.util.Date missionDateFin) { this.missionDateFin = missionDateFin; }
    
    public Double getMissionRemuneration() { return missionRemuneration; }
    public void setMissionRemuneration(Double missionRemuneration) { this.missionRemuneration = missionRemuneration; }
    
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
    
    public boolean isNotationDejaFaite() { return notationDejaFaite; }
    public void setNotationDejaFaite(boolean notationDejaFaite) { this.notationDejaFaite = notationDejaFaite; }
}


