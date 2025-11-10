package com.example.Tji_Teliman.entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.Tji_Teliman.entites.enums.TypeRecruteur;
import jakarta.persistence.*;

import java.util.ArrayList;
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

    private String adresse;

    private String urlPhoto;

    private String carteIdentite;

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

    @OneToMany(mappedBy = "recruteur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Litige> litiges = new ArrayList<>();

    public List<Litige> getLitiges() { return litiges; }
    public void setLitiges(List<Litige> litiges) { this.litiges = litiges; }

    public void addLitige(Litige litige) {
        litiges.add(litige);
        litige.setRecruteur(this);
    }
}
