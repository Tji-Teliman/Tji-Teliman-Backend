package com.example.Tji_Teliman.entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.Tji_Teliman.entites.enums.TypeRecruteur;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recruteur")
public class Recruteur extends Utilisateur {

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private TypeRecruteur typeRecruteur;

    @Temporal(TemporalType.DATE)
    private Date dateNaissance;

    private String profession;

    private String adresse; // deprecated: remplacée par adresse + lat/lng via Google Maps
    private Double latitude;
    private Double longitude;
    private String placeId;

    private String urlPhoto;

    private String nomEntreprise;

    private String secteurActivite;

    private String emailEntreprise;

    private String siteWeb;

    @OneToMany(mappedBy = "recruteur")
    @JsonIgnore
    private List<Message> messages;

    @OneToMany(mappedBy = "recruteur")
    @JsonIgnore
    private List<Notation> notations;

    @OneToMany(mappedBy = "recruteur")
    @JsonIgnore
    private Set<Paiement> paiements;

    @OneToMany(mappedBy = "recruteur")
    @JsonIgnore
    private Set<Mission> missions;
    @OneToMany(mappedBy = "recruteurValidateur")
    @JsonIgnore
    private List<Candidature> candidaturesValidees;

    // Validation retirée pour autoriser l'inscription minimale.
}
