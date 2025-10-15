package com.example.Tji_Teliman.dto;

import java.util.Date;

public class ProfilCandidatureDTO {
    private Long candidatureId;
    private String statutCandidature;
    private Date dateSoumission;
    
    // Informations du jeune
    private Long jeuneId;
    private String jeuneNom;
    private String jeunePrenom;
    private String jeuneUrlPhoto;
    
    // Statistiques du jeune
    private Double moyenneNotes;
    private Integer nombreEvaluations;
    private Long totalMissionsAccomplies;
    
    // Motivation pour cette candidature
    private String motivationContenu;
    private Date motivationDateSoumission;
    
    // Informations de la mission
    private Long missionId;
    private String missionTitre;
    private String missionDescription;

    // Constructeurs
    public ProfilCandidatureDTO() {}

    // Getters et Setters
    public Long getCandidatureId() { return candidatureId; }
    public void setCandidatureId(Long candidatureId) { this.candidatureId = candidatureId; }
    
    public String getStatutCandidature() { return statutCandidature; }
    public void setStatutCandidature(String statutCandidature) { this.statutCandidature = statutCandidature; }
    
    public Date getDateSoumission() { return dateSoumission; }
    public void setDateSoumission(Date dateSoumission) { this.dateSoumission = dateSoumission; }
    
    public Long getJeuneId() { return jeuneId; }
    public void setJeuneId(Long jeuneId) { this.jeuneId = jeuneId; }
    
    public String getJeuneNom() { return jeuneNom; }
    public void setJeuneNom(String jeuneNom) { this.jeuneNom = jeuneNom; }
    
    public String getJeunePrenom() { return jeunePrenom; }
    public void setJeunePrenom(String jeunePrenom) { this.jeunePrenom = jeunePrenom; }
    
    public String getJeuneUrlPhoto() { return jeuneUrlPhoto; }
    public void setJeuneUrlPhoto(String jeuneUrlPhoto) { this.jeuneUrlPhoto = jeuneUrlPhoto; }
    
    public Double getMoyenneNotes() { return moyenneNotes; }
    public void setMoyenneNotes(Double moyenneNotes) { this.moyenneNotes = moyenneNotes; }
    
    public Integer getNombreEvaluations() { return nombreEvaluations; }
    public void setNombreEvaluations(Integer nombreEvaluations) { this.nombreEvaluations = nombreEvaluations; }
    
    public Long getTotalMissionsAccomplies() { return totalMissionsAccomplies; }
    public void setTotalMissionsAccomplies(Long totalMissionsAccomplies) { this.totalMissionsAccomplies = totalMissionsAccomplies; }
    
    public String getMotivationContenu() { return motivationContenu; }
    public void setMotivationContenu(String motivationContenu) { this.motivationContenu = motivationContenu; }
    
    public Date getMotivationDateSoumission() { return motivationDateSoumission; }
    public void setMotivationDateSoumission(Date motivationDateSoumission) { this.motivationDateSoumission = motivationDateSoumission; }
    
    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }
    
    public String getMissionTitre() { return missionTitre; }
    public void setMissionTitre(String missionTitre) { this.missionTitre = missionTitre; }
    
    public String getMissionDescription() { return missionDescription; }
    public void setMissionDescription(String missionDescription) { this.missionDescription = missionDescription; }
}
