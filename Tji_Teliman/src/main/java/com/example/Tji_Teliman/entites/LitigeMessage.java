package com.example.Tji_Teliman.entites;

import com.example.Tji_Teliman.entites.enums.LitigeMessageDestinataire;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "litige_message")
public class LitigeMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "litige_id")
    private Litige litige;

    @ManyToOne(optional = false)
    @JoinColumn(name = "administrateur_id")
    private Administrateur administrateur;

    @ManyToOne(optional = true)
    @JoinColumn(name = "jeune_prestateur_id")
    private JeunePrestateur jeunePrestateur;

    @ManyToOne(optional = true)
    @JoinColumn(name = "recruteur_id")
    private Recruteur recruteur;

    @Enumerated(EnumType.STRING)
    @Column(name = "destinataire_type", nullable = false)
    private LitigeMessageDestinataire destinataireType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    @Column(name = "est_lu", nullable = false)
    private boolean estLu = false;

    @PrePersist
    public void onCreate() {
        if (dateEnvoi == null) {
            dateEnvoi = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Litige getLitige() {
        return litige;
    }

    public void setLitige(Litige litige) {
        this.litige = litige;
    }

    public Administrateur getAdministrateur() {
        return administrateur;
    }

    public void setAdministrateur(Administrateur administrateur) {
        this.administrateur = administrateur;
    }

    public JeunePrestateur getJeunePrestateur() {
        return jeunePrestateur;
    }

    public void setJeunePrestateur(JeunePrestateur jeunePrestateur) {
        this.jeunePrestateur = jeunePrestateur;
    }

    public Recruteur getRecruteur() {
        return recruteur;
    }

    public void setRecruteur(Recruteur recruteur) {
        this.recruteur = recruteur;
    }

    public LitigeMessageDestinataire getDestinataireType() {
        return destinataireType;
    }

    public void setDestinataireType(LitigeMessageDestinataire destinataireType) {
        this.destinataireType = destinataireType;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public boolean isEstLu() {
        return estLu;
    }

    public void setEstLu(boolean estLu) {
        this.estLu = estLu;
    }
}

