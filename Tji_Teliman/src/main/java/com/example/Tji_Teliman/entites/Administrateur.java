package com.example.Tji_Teliman.entites;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "administrateur")
public class Administrateur extends Utilisateur {
    @OneToMany(mappedBy = "administrateur")
    private java.util.Set<Categorie> categories;

    @OneToMany(mappedBy = "administrateur")
    private java.util.Set<Competence> competences;
}
