package com.example.Tji_Teliman.entites;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "jeune_prestateur")
public class JeunePrestateur extends Utilisateur {

    @Temporal(TemporalType.DATE)
    private Date dateNaissance;

    private String localisation;

    @ManyToMany
    @JoinTable(
        name = "jeune_prestateur_competence",
        joinColumns = @JoinColumn(name = "jeune_prestateur_id"),
        inverseJoinColumns = @JoinColumn(name = "competence_id")
    )
    private List<Competence> competences;

    private String urlPhoto;

    private String carteIdentite;

    @OneToMany(mappedBy = "jeunePrestateur")
    private List<Message> messages;

    @OneToMany(mappedBy = "jeunePrestateur")
    private List<Notation> notations;

    @OneToMany(mappedBy = "jeunePrestateur")
    private Set<Candidature> candidatures;
}
