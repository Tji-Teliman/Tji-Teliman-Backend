package com.example.Tji_Teliman.entites;

import com.example.Tji_Teliman.entites.enums.TypeNotification;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.PrePersist;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 50)
    private TypeNotification type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;
    
    @Column(nullable = false)
    private boolean estLue;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataire;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission; // Contexte éventuel

    @ManyToOne
    @JoinColumn(name = "candidature_id")
    private Candidature candidature; // Contexte éventuel

    @ManyToOne
    @JoinColumn(name = "paiement_id")
    private Paiement paiement; // Contexte éventuel

    @PrePersist
    private void onCreate() {
        if (dateCreation == null) {
            dateCreation = new Date();
        }
        // Par défaut non lue
        // estLue peut être laissé à false par défaut via valeur primitive
    }
}
