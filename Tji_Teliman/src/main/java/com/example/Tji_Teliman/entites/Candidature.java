package com.example.Tji_Teliman.entites;

import com.example.Tji_Teliman.entites.enums.StatutCandidature;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import java.util.Date;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "candidature", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"jeune_prestateur_id", "mission_id"})
})
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateSoumission;

    @OneToMany(mappedBy = "candidature")
    private Set<Motivation> motivations;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCandidature statut;

    @ManyToOne(optional = false)
    @JoinColumn(name = "jeune_prestateur_id", nullable = false)
    private JeunePrestateur jeunePrestateur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recruteur_id", nullable = false)
    private Recruteur recruteurValidateur;

    @OneToOne(mappedBy = "candidature", orphanRemoval = true, cascade = jakarta.persistence.CascadeType.ALL)
    private Paiement paiement;

    @OneToOne(mappedBy = "candidature", orphanRemoval = true, cascade = jakarta.persistence.CascadeType.ALL)
    private Notation notation;

    @PrePersist
    private void onCreate() {
        if (dateSoumission == null) {
            dateSoumission = new Date();
        }
        if (statut == null) {
            statut = StatutCandidature.EN_ATTENTE;
        }
    }
}
