package com.example.Tji_Teliman.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recruteur_id", nullable = false)
    private Recruteur recruteur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "jeune_prestateur_id", nullable = false)
    private JeunePrestateur jeunePrestateur;

    // true si le message a été envoyé par le recruteur, false si par le jeune
    @Column(nullable = false)
    private boolean envoyeParRecruteur;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateMessage;

    @PrePersist
    private void onCreate() {
        if (dateMessage == null) {
            dateMessage = new Date();
        }
    }

    @Transient
    public Long getIdExpediteur() {
        return envoyeParRecruteur ? recruteur.getId() : jeunePrestateur.getId();
    }

    @Transient
    public Long getIdDestinataire() {
        return envoyeParRecruteur ? jeunePrestateur.getId() : recruteur.getId();
    }
}
