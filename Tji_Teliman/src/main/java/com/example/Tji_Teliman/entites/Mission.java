package com.example.Tji_Teliman.entites;

import com.example.Tji_Teliman.entites.enums.StatutMission;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        Long jours = getDureJours();
        if (jours == null) return null;
        if (heureDebut != null && heureFin != null) {
            long heuresParJour = java.time.Duration.between(heureDebut, heureFin).toHours();
            return jours * Math.max(0, heuresParJour);
        }
        return null;
    }
}
