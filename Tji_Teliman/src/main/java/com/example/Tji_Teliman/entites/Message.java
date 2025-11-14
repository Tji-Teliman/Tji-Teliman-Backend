package com.example.Tji_Teliman.entites;

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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import java.util.Date;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message")
public class Message {

    public enum MessageType {
        TEXT,       // Message texte
        VOICE       // Message vocal
    }

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

    @Column(nullable = true)
    private String contenu;

    //  le type de message
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType typeMessage = MessageType.TEXT;

    //  pour stocker l'URL du fichier vocal
    @Column(name = "voice_file_url")
    private String voiceFileUrl;

    //  pour la durée du message vocal (en secondes)
    @Column(name = "voice_duration")
    private Integer voiceDuration;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateMessage;

    @Column(nullable = false)
    private boolean estLu = false;

    @PrePersist
    private void onCreate() {
        if (dateMessage == null) {
            dateMessage = new Date();
        }
        
        // Par défaut, un message n'est pas lu à sa création (déjà initialisé à false)

        // Validation de cohérence selon le type de message
        if (typeMessage == MessageType.VOICE && voiceFileUrl == null) {
            throw new IllegalStateException("Un message vocal doit avoir une URL de fichier");
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

    // Méthode utilitaire pour vérifier si c'est un message vocal
    @Transient
    public boolean isVoiceMessage() {
        return MessageType.VOICE.equals(typeMessage);
    }

    // Méthode utilitaire pour vérifier si c'est un message texte
    @Transient
    public boolean isTextMessage() {
        return MessageType.TEXT.equals(typeMessage);
    }
}