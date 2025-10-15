package com.example.Tji_Teliman.entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
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
@Table(name = "jeune_prestateur")
public class JeunePrestateur extends Utilisateur {

    @Temporal(TemporalType.DATE)
    private Date dateNaissance;

    private String localisation;

    @OneToMany(mappedBy = "jeunePrestateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JeuneCompetence> competences;

    private String urlPhoto;

    private String carteIdentite;

    @OneToMany(mappedBy = "jeunePrestateur")
    @JsonIgnore
    private List<Message> messages;

    @OneToMany(mappedBy = "jeunePrestateur")
    private List<Notation> notations;

    @OneToMany(mappedBy = "jeunePrestateur")
    private Set<Candidature> candidatures;
}
