package com.example.Tji_Teliman.entites;

import com.example.Tji_Teliman.entites.enums.StatutPaiement;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "paiement")
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double montant;

    @Temporal(TemporalType.TIMESTAMP)
    private Date datePaiement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement statutPaiement;

    @OneToOne(optional = false)
    @JoinColumn(name = "candidature_id", nullable = false, unique = true)
    private Candidature candidature;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recruteur_id", nullable = false)
    private Recruteur recruteur;
}
