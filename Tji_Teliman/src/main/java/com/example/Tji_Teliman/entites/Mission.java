package com.example.Tji_Teliman.entites;

import com.example.Tji_Teliman.entites.enums.StatutMission;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.CascadeType;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mission")
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String exigence;

    @Temporal(TemporalType.DATE)
    private Date dateDebut;

    @Temporal(TemporalType.DATE)
    private Date dateFin;

    @Transient
    private Long dureJours; // Durée en jours
    
    @Transient
    private Long dureHeures; // Durée estimée en heures (en fonction des heures/jour)

    // Coordonnées précises (Google Maps)
    private Double latitude;
    private Double longitude;
    private String adresse;
    private String placeId;

    private Double remuneration;

    @Temporal(TemporalType.TIMESTAMP)
    private Date datePublication;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recruteur_id", nullable = false)
    private Recruteur recruteur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutMission statut;

    @OneToMany(mappedBy = "mission")
    private Set<Candidature> candidatures;

    @OneToMany(mappedBy = "mission")
    @JsonIgnore
    private List<SignalementMission> signalements;

    private Integer nbSignalements;

    @Temporal(TemporalType.TIMESTAMP)
    private Date derniereDateSignalement;
    
    private LocalTime heureDebut;
    
    private LocalTime heureFin;
    
    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (dateDebut != null && dateFin != null && dateFin.before(dateDebut)) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }
    
    @Transient
    public Long getDureJours() {
        if (dateDebut == null || dateFin == null) {
            return null;
        }
        long diffInMillies = Math.abs(dateFin.getTime() - dateDebut.getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
    
    @Transient
    public Long getDureHeures() {
        if (dateDebut == null || dateFin == null) {
            return null;
        }
        
        // Si les heures de début et fin sont définies, calculer la durée en heures
        if (heureDebut != null && heureFin != null) {
            // Si c'est le même jour (dateDebut == dateFin)
            if (dateDebut.equals(dateFin)) {
                long heures = java.time.Duration.between(heureDebut, heureFin).toHours();
                return Math.max(0, heures);
            } else {
                // Si c'est sur plusieurs jours, calculer la durée totale
                Long jours = getDureJours();
                if (jours == null) return null;
                long heuresParJour = java.time.Duration.between(heureDebut, heureFin).toHours();
                return jours * Math.max(0, heuresParJour);
            }
        }
        
        return null;
    }

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Litige> litiges = new ArrayList<>();

    public List<Litige> getLitiges() { return litiges; }
    public void setLitiges(List<Litige> litiges) { this.litiges = litiges; }

    public void addLitige(Litige litige) {
        litiges.add(litige);
        litige.setMission(this);
    }

}
