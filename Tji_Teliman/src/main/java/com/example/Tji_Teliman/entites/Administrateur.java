package com.example.Tji_Teliman.entites;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "administrateur")
public class Administrateur extends Utilisateur {
    @OneToMany(mappedBy = "administrateur")
    @JsonIgnore
    private java.util.Set<Categorie> categories;

    @OneToMany(mappedBy = "administrateur")
    @JsonIgnore
    private java.util.Set<Competence> competences;
}
