package com.example.Tji_Teliman.entites;

import com.example.Tji_Teliman.entites.enums.TypeRecruteur;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "recruteur")
public class Recruteur extends Utilisateur {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeRecruteur typeRecruteur;

    @Temporal(TemporalType.DATE)
    private Date dateNaissance;

    private String profession;

    private String adresse;

    private String urlPhoto;

    private String nomEntreprise;

    private String secteurActivite;

    private String emailEntreprise;

    private String siteWeb;

    @OneToMany(mappedBy = "recruteur")
    private List<Message> messages;

    @OneToMany(mappedBy = "recruteur")
    private List<Notation> notations;

    @OneToMany(mappedBy = "recruteur")
    private Set<Paiement> paiements;

    @OneToMany(mappedBy = "recruteur")
    private Set<Mission> missions;
    @OneToMany(mappedBy = "recruteurValidateur")
    private List<Candidature> candidaturesValidees;

    @PrePersist
    @PreUpdate
    private void validateSelonType() {
        if (typeRecruteur == null) {
            return;
        }

        if (typeRecruteur == TypeRecruteur.ENTREPRISE) {
            // Champs entreprise requis
            requireNotBlank(nomEntreprise, "nomEntreprise");
            requireNotBlank(secteurActivite, "secteurActivite");
            requireNotBlank(emailEntreprise, "emailEntreprise");
            // siteWeb facultatif -> pas de validation stricte
        } else if (typeRecruteur == TypeRecruteur.PARTICULIER) {
            // Champs particulier requis
            if (dateNaissance == null) {
                throw new IllegalStateException("dateNaissance est requis pour un recruteur de type PARTICULIER");
            }
            requireNotBlank(profession, "profession");
            requireNotBlank(adresse, "adresse");
        }
    }

    private void requireNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(fieldName + " est requis pour un recruteur de type " + typeRecruteur);
        }
    }
}
