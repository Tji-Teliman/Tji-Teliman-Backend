package com.example.Tji_Teliman.entites;

import com.example.Tji_Teliman.entites.enums.StatutLitige;
import com.example.Tji_Teliman.entites.enums.TypeLitige;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "litige")
public class Litige {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeLitige type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutLitige statut = StatutLitige.OUVERT;

    // RELATIONS OBLIGATOIRES
    @ManyToOne(optional = false)
    @JoinColumn(name = "jeune_prestataire_id")
    private JeunePrestateur jeunePrestateur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recruteur_id")
    private Recruteur recruteur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    // RELATION OPTIONNELLE
    @ManyToOne(optional = true)
    @JoinColumn(name = "administrateur_id")
    private Administrateur administrateur;

    @Column(columnDefinition = "TEXT")
    private String decisionAdministrateur;

    private LocalDateTime dateCreation;
    private LocalDateTime dateResolution;

    // Constructeurs
    public Litige() {
        this.dateCreation = LocalDateTime.now();
    }

    public Litige(String titre, String description, TypeLitige type) {
        this();
        this.titre = titre;
        this.description = description;
        this.type = type;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TypeLitige getType() { return type; }
    public void setType(TypeLitige type) { this.type = type; }

    public StatutLitige getStatut() { return statut; }
    public void setStatut(StatutLitige statut) { this.statut = statut; }

    public JeunePrestateur getJeunePrestateur() { return jeunePrestateur; }
    public void setJeunePrestateur(JeunePrestateur jeunePrestateur) { this.jeunePrestateur = jeunePrestateur; }

    public Recruteur getRecruteur() { return recruteur; }
    public void setRecruteur(Recruteur recruteur) { this.recruteur = recruteur; }

    public Mission getMission() { return mission; }
    public void setMission(Mission mission) { this.mission = mission; }

    public Administrateur getAdministrateur() { return administrateur; }
    public void setAdministrateur(Administrateur administrateur) { this.administrateur = administrateur; }

    public String getDecisionAdministrateur() { return decisionAdministrateur; }
    public void setDecisionAdministrateur(String decisionAdministrateur) { this.decisionAdministrateur = decisionAdministrateur; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateResolution() { return dateResolution; }
    public void setDateResolution(LocalDateTime dateResolution) { this.dateResolution = dateResolution; }
}