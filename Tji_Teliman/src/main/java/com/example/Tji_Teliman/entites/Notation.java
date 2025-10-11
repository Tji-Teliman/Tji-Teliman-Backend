package com.example.Tji_Teliman.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.persistence.OneToOne;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "notation")
public class Notation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recruteur_id", nullable = false)
    private Recruteur recruteur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "jeune_prestateur_id", nullable = false)
    private JeunePrestateur jeunePrestateur;

    // true si c'est le recruteur qui note, false si c'est le jeune
    @Column(nullable = false)
    private boolean initieParRecruteur;

    @Column(nullable = false)
    private Integer note; // 1..5

    @Column(columnDefinition = "TEXT")
    private String commentaire; // facultatif

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateNotation;

    @OneToOne(optional = false)
    @JoinColumn(name = "candidature_id", nullable = false, unique = true)
    private Candidature candidature;

    @PrePersist
    private void onCreate() {
        if (dateNotation == null) {
            dateNotation = new Date();
        }
        validateNote();
    }

    @PreUpdate
    private void onUpdate() {
        validateNote();
    }

    private void validateNote() {
        if (note == null || note < 1 || note > 5) {
            throw new IllegalStateException("La note doit être comprise entre 1 et 5");
        }
    }

    @Transient
    public Long getIdInitiateur() {
        return initieParRecruteur ? recruteur.getId() : jeunePrestateur.getId();
    }

    @Transient
    public Long getIdReceveur() {
        return initieParRecruteur ? jeunePrestateur.getId() : recruteur.getId();
    }
}
